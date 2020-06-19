package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.RankList;

import java.util.List;

@Mapper
public interface RankMapper extends MyMapper<RankList> {
    @Select("SELECT to_user_id AS user_id, motto,\n" +
            "\tusername,\n" +
            "\tnick_name,\n" +
            "\tCASE  \n" +
            "\tWHEN nick_name IS NULL THEN username\n" +
            "\tELSE nick_name\n" +
            "\tEND 'name',\n" +
            "\t\n" +
            "\tSUM(star) AS score,\n" +
            "\t\n" +
            "\tCOUNT(1) AS 'count',\n" +
            "\t\n" +
            "\tCASE \n" +
            "\tWHEN background_image IS NULL THEN '/uploads/default'\n" +
            "\tELSE background_image END 'background',\n" +
            "\t\n" +
            "\tCASE \n" +
            "\tWHEN avatar IS NULL THEN avatar_url\n" +
            "\tELSE avatar\n" +
            "\tEND 'avatar'\n" +
            "\t\n" +
            "FROM wechat_task a,wechat_user b,wechat_user_detail c\n" +
            "WHERE\n" +
            "a.to_user_id = b.id AND b.id = c.user_id\n" +
            "AND\n" +
            "STATUS = 'COMPLETE'\n" +
            "GROUP BY to_user_id \n" +
            "ORDER BY score DESC \n" +
            "LIMIT #{num}")
    List<RankList> getRankList(@Param("num") Integer num);
}
