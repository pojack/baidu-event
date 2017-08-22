package cn.edu.swpu.cins.event.analyse.platform.utility.redis;

/**
 * Created by nowcoder on 2016/7/30.
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";

    public static String getSpecialEventSetKey(int topicId){
        return "SPECIAL_EVENT_SET" + SPLIT + topicId;
    }
}
