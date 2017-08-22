package cn.edu.swpu.cins.event.analyse.platform.model.view;

/**
 * Created by lp-deepin on 17-5-20.
 */
public class ChartPoint {
    private String x;
    private int y;

    public ChartPoint(String x, int y) {
        this.x = x;
        this.y = y;
    }

    public ChartPoint() {   }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
