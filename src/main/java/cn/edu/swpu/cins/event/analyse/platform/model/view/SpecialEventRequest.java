package cn.edu.swpu.cins.event.analyse.platform.model.view;

import java.util.List;

public class SpecialEventRequest {

    private int more;
    private List<Integer> ids;

    public SpecialEventRequest(int more, List<Integer> ids) {
        this.more = more;
        this.ids = ids;
    }

    public SpecialEventRequest() {
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
