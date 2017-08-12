package cn.edu.swpu.cins.event.analyse.platform.model.view;

import java.util.List;

public class ViewObject<T> {

    //返回事件列表和总页数

    private List<T> EventPageList;
    private int pageCount;


    public ViewObject(List<T> eventPageList, int pages) {
        EventPageList = eventPageList;
        this.pageCount = pages;
    }


    public ViewObject() {
    }

    public List<T> getEventPageList() {
        return EventPageList;
    }

    public void setEventPageList(List<T> eventPageList) {
        EventPageList = eventPageList;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
