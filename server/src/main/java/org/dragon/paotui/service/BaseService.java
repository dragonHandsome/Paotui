package org.dragon.paotui.service;


import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

public interface BaseService<T> {
    void save(T model);//持久化

    void save(List<T> models);//批量持久化

    T insertAutoPrimary(T model);//返回类id中有生成值

    void deleteById(Long id);//通过主鍵刪除

    void deleteByIds(String ids);//批量刪除 “1,2,3,4”

    void deleteByEntity(T condition);

    void update(T model);//更新

    T findById(Long id);//通过ID查找

    T findBy(String fieldName, Object value) throws TooManyResultsException; //通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束

    List<T> findByIds(String ids);//通过多个ID查找//eg：ids -> “1,2,3,4”

    List<T> findByCondition(Condition condition);//根据条件查找

    List<T> findAll();//获取所有

    //左边更新的数据 右边更新的条件
    void updateByEntity(T model, T condition);

    void updateByProperty(T model, String prop, Object value);

    T findOneByEntity(T condition);

    List<T> findAllByProperty(String prop, Object value);

    List<T> findByEntity(T condition);

    public List<T> findAllByPropertyOrderBy(String prop, Object value, String orderBy);

    public List<T> findAllByPropertyOrderBy(String prop, Object value, String orderBy, boolean isDesc);
}