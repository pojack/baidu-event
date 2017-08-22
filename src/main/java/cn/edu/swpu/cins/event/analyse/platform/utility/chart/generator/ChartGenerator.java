package cn.edu.swpu.cins.event.analyse.platform.utility.chart.generator;

import cn.edu.swpu.cins.event.analyse.platform.enums.ChartDataTypeEnum;
import cn.edu.swpu.cins.event.analyse.platform.enums.ChartTypeEnum;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.DailyEvent;
import cn.edu.swpu.cins.event.analyse.platform.model.view.ChartPoint;
import org.jfree.chart.JFreeChart;

import java.util.List;

/**
 * Created by LLPP on 2017/7/21.
 */
public interface ChartGenerator {
    /**
     * 生成jfreechart对象
     * @param list
     * @param title
     * @param chartType
     * @return
     * @throws Exception
     */
    public JFreeChart generateChart(List<ChartPoint> list
            , String title
            , ChartTypeEnum chartType) throws Exception;

    /**
     * 得到折线图的折点数据列表
     * @param events
     * @param begin
     * @param end
     * @param dataType
     * @return
     */
    public List<ChartPoint> getChartPoints(List<DailyEvent> events, long begin, long end, ChartDataTypeEnum dataType);

    /**
     * 将jfreechart 转换为base64编码的字符串
     * @param chart
     * @return
     * @throws Exception
     */
    public String chartToBASE64(JFreeChart chart) throws Exception;
}
