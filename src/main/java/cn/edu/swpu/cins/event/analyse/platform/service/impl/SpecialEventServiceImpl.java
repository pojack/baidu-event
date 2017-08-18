package cn.edu.swpu.cins.event.analyse.platform.service.impl;

import cn.edu.swpu.cins.event.analyse.platform.dao.DailyEventDao;
import cn.edu.swpu.cins.event.analyse.platform.dao.TopicDao;
import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.exception.NoEventException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.DailyEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.Topic;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;
import cn.edu.swpu.cins.event.analyse.platform.service.SpecialEventService;
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

    @Autowired
    public SpecialEventServiceImpl(DailyEventDao dailyEventDao, TopicDao topicDao
            , @Value("${event.service.page-count}") int pageSize) {
        this.dailyEventDao = dailyEventDao;
        this.topicDao = topicDao;
        this.pageSize = pageSize;
    }

    @Override
    public ViewObject getSpecialEventByPage(int page, int more, List<Integer> ids) throws BaseException {
        ViewObject vo = new ViewObject();
        int pageSize = this.pageSize;

        if (more > 0) {
            pageSize += more;
        }
        int offset = --page * pageSize;
        List<Topic> topics = topicDao.selectAll().stream().filter(topic -> ids.contains(topic.getId())).collect(toList());
        List<DailyEvent> dailyEvents;
        Set<String> regions = new HashSet<>();

        for (Topic topic : topics) {
            for (String reg : topic.getRegion()) {
                regions.add(reg);
            }

        }
        dailyEvents = getEventByRegions(regions.stream().collect(toList()));
        //collect the matched event
        List<DailyEvent> list = dailyEvents
                .stream()
                .filter(dailyEvent -> matchEventByTopics(dailyEvent, topics))
                .filter(dailyEvent -> dailyEvent.getCollectionStatus() == 0)
                .sorted(comparing(DailyEvent::getPostTime).reversed())
                .collect(toList());

        //分页获取事件
        int limit = (offset + pageSize) > list.size() ? list.size() : (offset + pageSize);

        if (offset >= list.size() || offset < 0) {
            throw new NoEventException();
        }
        //get page count
        int pages = list.size()/pageSize + (list.size() % pageSize == 0 ? 0 : 1);
        vo.setEventPageList(list.subList(offset, limit));
        vo.setPages(pages);

        return vo;
    }

    @Override
    public List<DailyEvent> getAllSpecialEvent() throws BaseException {
        List<Topic> topics = topicDao.selectAll();
        List<DailyEvent> dailyEvents;
        Set<String> regions = new HashSet<>();

        for (Topic topic : topics) {

            for (String reg : topic.getRegion()) {
                regions.add(reg);
            }
        }
        //convert set to list
        dailyEvents = getEventByRegions(regions.stream().collect(toList()));
        //collect the matched event
        List<DailyEvent> list = dailyEvents
                .stream()
                .filter(dailyEvent -> matchEventByTopics(dailyEvent, topics))
                .filter(dailyEvent -> dailyEvent.getCollectionStatus() == 0)
                .sorted(comparing(DailyEvent::getPostTime).reversed())
                .collect(toList());

        return list;
    }

    //根据填写专题的地域获取事件信息（事件均未处置）
    private List<DailyEvent> getEventByRegions(List<String> regions) {
        List<DailyEvent> dailyEvents;
        dailyEvents = dailyEventDao.selectByRegions(regions);

        return dailyEvents;
    }

    private boolean matchEventByTopics(DailyEvent event, List<Topic> topics) {
        String content = event.getMainView();

        for (Topic topic : topics) {
            for (String region : topic.getRegion()) {

                if (content.contains(region)) {
                    for (String rule : topic.getRules()) {
                        if (content.contains(rule)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
