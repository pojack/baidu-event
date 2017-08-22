package cn.edu.swpu.cins.event.analyse.platform.dao;

import cn.edu.swpu.cins.event.analyse.platform.model.persistence.Topic;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TopicDao {
    static String TABLE_NAME = " special_topic ";
    static String SELECT_FIELD = " id,name,region,rules ";
    static String INSERT_FIELD = " name,region,rules ";

    int insertTopic(Topic topic);

    List<Topic> selectAll();

    int deleteByIds(List<Integer> ids);

    List<Topic> selectByIds(List<Integer> ids);
}
