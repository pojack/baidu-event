package cn.edu.swpu.cins.event.analyse.platform.service.impl;

import cn.edu.swpu.cins.event.analyse.platform.dao.DailyEventDao;
import cn.edu.swpu.cins.event.analyse.platform.dao.TopicDao;
import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.exception.NoEventException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.DailyEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.Topic;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;
import cn.edu.swpu.cins.event.analyse.platform.service.SpecialEventService;
import cn.edu.swpu.cins.event.analyse.platform.utility.redis.JedisAdapter;
import cn.edu.swpu.cins.event.analyse.platform.utility.redis.RedisKeyUtil;
import cn.edu.swpu.cins.event.analyse.platform.utility.serializer.ProtostuffSerializationUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Created by muyi on 17-6-7.
 */
@Service
public class SpecialEventServiceImpl implements SpecialEventService {

    private DailyEventDao dailyEventDao;
    private TopicDao topicDao;
    private int pageSize;
    private JedisAdapter jedisAdapter;
    private RuntimeSchema<DailyEvent> schema = RuntimeSchema.createFrom(DailyEvent.class);

    @Autowired
    public SpecialEventServiceImpl(DailyEventDao dailyEventDao
            , JedisAdapter jedisAdapter
            , TopicDao topicDao
            , @Value("${event.service.page-count}") int pageSize) {
        this.dailyEventDao = dailyEventDao;
        this.topicDao = topicDao;
        this.pageSize = pageSize;
        this.jedisAdapter = jedisAdapter;
    }

    @Override
    public ViewObject getSpecialEventByPage(int page, int more, List<Integer> topicIds) throws BaseException {
        ViewObject<DailyEvent> vo = new ViewObject<>();
        int pageSize = this.pageSize;

        if (more > 0) {
            pageSize += more;
        }

        int offset = --page * pageSize;

        //所有需要查找的专题的列表
        List<Topic> topics = topicDao.selectByIds(topicIds);

        List<DailyEvent> list = getEventByTopics(topics);

        //分页获取事件
        int limit = (offset + pageSize) > list.size() ? list.size() : (offset + pageSize);

        if (offset >= list.size() || offset < 0) {
            throw new NoEventException();
        }
        //get page count
        int pages = list.size() / pageSize + (list.size() % pageSize == 0 ? 0 : 1);
        vo.setEventPageList(list.subList(offset, limit));
        vo.setPages(pages);

        return vo;
    }

    @Override
    public List<DailyEvent> getAllSpecialEvent() throws BaseException {
        //缓存映射集
        Map<String, List<DailyEvent>> cacheMap = new HashMap<>();
        //所有专题的列表
        List<Topic> topics = topicDao.selectAll();

        return getEventByTopics(topics);
    }

    //根据填写专题的地域获取事件信息（事件均未处置）
    private List<DailyEvent> getEventByRegions(List<String> regions) {
        List<DailyEvent> dailyEvents;
        dailyEvents = dailyEventDao.selectByRegions(regions);

        return dailyEvents;
    }

    private int matchEventByTopics(DailyEvent event, List<Topic> topics) {
        String content = event.getMainView();

        for (Topic topic : topics) {
            for (String region : topic.getRegion()) {

                if (content.contains(region)) {
                    for (String rule : topic.getRules()) {
                        if (content.contains(rule)) {
                            return topic.getId();
                        }
                    }
                }
            }
        }

        return -1;
    }

    private List<DailyEvent> getEventByTopics(List<Topic> topics){
        //缓存映射集
        Map<String, List<DailyEvent>> cacheMap = new HashMap<>();
        //需要查找的专题列表
        List<Topic> searchTopics = new ArrayList<>();
        //模糊查找得到的事件列表
        List<DailyEvent> toMatchEvents;
        //匹配的事件列表
        List<DailyEvent> matchedEventList;
        //最终所得的结果集，用哈希集来去重
        Set<DailyEvent> resultSet = new HashSet<>();
        //用于模糊匹配的地域集
        Set<String> toMatchRegionSet = new HashSet<>();

        //遍历所有的专题
        for (Topic topic : topics) {
            int id = topic.getId();
            String key = RedisKeyUtil.getSpecialEventSetKey(id);
            //得到redis中缓存的专题事件
            Set<byte[]> eventByteSet = jedisAdapter.smembers(key.getBytes());
            //初始化cachemap中的事件列表
            cacheMap.put(key, new ArrayList<>());

            //若专题在redis中无匹配事件则将其添加入待查询专题列表中
            if (eventByteSet == null || eventByteSet.size() == 0) {
                searchTopics.add(topic);

                for (String region : topic.getRegion()) {
                    toMatchRegionSet.add(region);
                }

            } else {
                //将set中的dailyevent实例化并插入日常事件集合中
                eventByteSet
                        .forEach(bytes -> {
                            DailyEvent dailyEvent = ProtostuffSerializationUtil.deserializer(bytes,DailyEvent.class);
                            resultSet.add(dailyEvent);
                        });
            }
        }

        //convert set to list
        toMatchEvents = getEventByRegions(toMatchRegionSet.stream().collect(toList()));
        //collect the matched event
        matchedEventList = toMatchEvents
                .stream()
                .filter(dailyEvent -> dailyEvent.getCollectionStatus() == 0)//剔除已归集事件
                .filter(dailyEvent -> {
                    int matchedTopicId = matchEventByTopics(dailyEvent, searchTopics);

                    if (matchedTopicId < 0) {
                        return false;
                    }

                    String key = RedisKeyUtil.getSpecialEventSetKey(matchedTopicId);

                    //为对应的redis中的set添加日常事件
                    cacheMap.get(key)
                            .add(dailyEvent);

                    return true;
                })//匹配事件与专题
                .sorted(comparing(DailyEvent::getPostTime).reversed()) //按时间降序排序
                .collect(toList());

        //将cachemap中的元素缓存到redis中
        for (Map.Entry<String, List<DailyEvent>> entry : cacheMap.entrySet()) {
            String key = entry.getKey();
            List<DailyEvent> list = entry.getValue();

            list.forEach(dailyEvent -> {
                //用protostuff将dailyevent 序列化
                byte[] value = ProtostuffSerializationUtil.serializer(dailyEvent);
                //缓存事件
                jedisAdapter.sadd(key.getBytes(), value);
                jedisAdapter.expire(key, 3600);
            });
        }

        //将所得的添入结果集
        resultSet.addAll(matchedEventList);

        //将结果集转化为列表返回
        return resultSet
                .stream()
                .collect(toList());
    }
}
