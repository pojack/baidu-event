package cn.edu.swpu.cins.event.analyse.platform.service;

import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.DailyEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ViewObject;

import java.util.List;

/**
 * Created by muyi on 17-6-7.
 */
public interface SpecialEventService{

    ViewObject getSpecialEvent(int page, boolean getAll , int more, List<Integer> ids) throws BaseException;

    public List<DailyEvent> getSpecialEvent(int page, boolean getAll ,int more) throws BaseException;
}
