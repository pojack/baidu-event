package cn.edu.swpu.cins.event.analyse.platform.service;

import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.DailyEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;

import java.util.List;

/**
 * Created by muyi on 17-6-7.
 */
public interface SpecialEventService{

    ViewObject getSpecialEventByPage(int page, int more, List<Integer> ids) throws BaseException;

    List<DailyEvent> getAllSpecialEvent() throws BaseException;
}
