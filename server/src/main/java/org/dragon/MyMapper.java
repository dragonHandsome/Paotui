package org.dragon;

import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T>, IdsMapper<T>, ConditionMapper<T> {


}
