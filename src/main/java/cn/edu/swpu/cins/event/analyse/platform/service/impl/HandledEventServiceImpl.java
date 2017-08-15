package cn.edu.swpu.cins.event.analyse.platform.service.impl;

import cn.edu.swpu.cins.event.analyse.platform.dao.HandledEventDao;
import cn.edu.swpu.cins.event.analyse.platform.enums.FeedbackEnum;
import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.exception.IlleagalArgumentException;
import cn.edu.swpu.cins.event.analyse.platform.exception.OperationFailureException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.HandledEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.view.HandledEventPage;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;
import cn.edu.swpu.cins.event.analyse.platform.service.HandledEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lp-deepin on 17-5-22.
 */
@Service
public class HandledEventServiceImpl implements HandledEventService {

    private HandledEventDao handledEventDao;
    private final int pageSize;

    @Autowired
    public HandledEventServiceImpl(HandledEventDao handledEventDao, @Value("${event.service.page-count}") int pageSize) {
        this.handledEventDao = handledEventDao;
        this.pageSize = pageSize;
    }

    /**
     * 现在使用一个接口返回事件列表和相应总页数　　使用VO类
     *
     * @param page
     * @param more
     * @param isHandled  　是否已处置
     * @param isFeedBack 是否已反馈
     * @return
     * @throws BaseException
     */
    @Override
    public ViewObject getHandledEvents(int page, int more, int isHandled, int isFeedBack) throws BaseException {
        ViewObject vo = new ViewObject();
        int pageSize = this.pageSize;

        if (more > 0) {
            pageSize += more;
        }

        if (page <= 0) {
            throw new IlleagalArgumentException();
        }

        List<HandledEvent> handledEventList = handledEventDao.selectByCondition((--page) * pageSize, pageSize, isHandled, isFeedBack);

        //用事件总数计算页数
        int eventCount = handledEventDao.selectCountByCondition(isHandled, isFeedBack);
        int pageCount = eventCount/pageSize + (eventCount % pageSize != 0?1:0);

        List<HandledEventPage> list = handledEventList
                .stream()
                .map(HandledEventPage::new)
                .collect(Collectors.toList());

        vo.setEventPageList(list);
        vo.setPages(pageCount);
        return vo;
    }

    /**
     * 处置事件接口
     * @param handledEventPage
     * @return
     * @throws BaseException
     */
    @Override
    public int handle(HandledEventPage handledEventPage) throws BaseException {

        String eventHandler = handledEventPage.getEventHandler();
        String detail = handledEventPage.getDetail();
        String handledCondition = handledEventPage.getHandledCondition();
        String condition = handledEventPage.getFeedbackCondition();
        short conditionShort = FeedbackEnum.getIndexByFeedback(condition);

        if (eventHandler == null || detail == null || handledCondition == null || conditionShort == -1) {
            throw new IlleagalArgumentException("参数不可为空");
        } else if (eventHandler.length() > 10 || detail.length() > 100 || handledCondition.length() > 30) {
            throw new IlleagalArgumentException("输入长度超出限制");
        }

        HandledEvent handledEvent = new HandledEvent();
        handledEvent.setEventHandler(eventHandler);
        handledEvent.setDetail(detail);
        handledEvent.setFeedbackCondition(conditionShort);
        handledEvent.setHandledTime(new Date());
        handledEvent.setHandledCondition(handledCondition);
        handledEvent.setId(handledEventPage.getId());
        int updateCount = handledEventDao.updateHandledEvent(handledEvent);

        if (updateCount < 1) {
            throw new OperationFailureException();
        }

        return 0;
    }

    /**
     * 批量删除归集事件
     * @param ids
     * @return
     * @throws BaseException
     */
    @Override
    public int deleteEvents(List<Integer> ids) throws BaseException {

        try {
            return handledEventDao.deleteByIds(ids);
        } catch (Exception e) {
            throw new BaseException("服务器内部错误", HttpStatus.BAD_REQUEST);
        }


    }
}
