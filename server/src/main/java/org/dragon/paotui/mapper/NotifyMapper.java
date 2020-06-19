package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.Notify;

@Mapper
public interface NotifyMapper extends MyMapper<Notify> {
    @Update("<script>" +
            "update notify set is_read = true where id in\n" +
            "        <foreach collection=\"ids\" item=\"id\" open=\"(\" close=\")\" separator=\",\">\n" +
            "            #{id}\n" +
            "        </foreach>"+
            "</script>")
    void readNotifies(@Param("ids") Long[] ids);
}
