package org.dragon.paotui.service.impl;

import org.apache.ibatis.exceptions.TooManyResultsException;
import org.dragon.MyMapper;
import org.dragon.paotui.service.BaseService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractService<T> implements BaseService<T> {
    @Autowired
    private MyMapper<T> mapper;

    private Class<T> clazz;

    public AbstractService(){
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public void save(T model) {
        mapper.insertSelective(model);
    }
    public T insertAutoPrimary(T model){
        mapper.insertUseGeneratedKeys(model);
        return model;
    }

    @Override
    public void save(List<T> models) {
        mapper.insertList(models);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteByIds(String ids) {
        mapper.deleteByIds(ids);
    }

    @Override
    public void deleteByEntity(T condition) {
        final Example example = generateExample(condition);
        mapper.deleteByExample(example);
    }

    @Override
    public void update(T model) {
        mapper.updateByPrimaryKeySelective(model);
    }
    //
    public void updateByEntity(T model,T condition){
        final Example example = generateExample(condition);
        mapper.updateByExampleSelective(model, example);
    }
    Example generateExample(T condition){
        final Example example = new Example(clazz);
        final Example.Criteria criteria = example.createCriteria();
        final Field[] declaredFields = condition.getClass().getDeclaredFields();
        boolean isLegal = false;
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                Object value = declaredField.get(condition);
                if(value != null) {
                    isLegal = true;
                    criteria.andEqualTo(declaredField.getName(), value);
                }
            } catch (IllegalAccessException e) {
                MyLogUtil.error(e.getMessage());
            }
        }
        if(!isLegal) MyLogUtil.error("condition中没有属性有值");
        return example;
    }
    Example generateExample(String prop, Object value){
        final Example example = new Example(clazz);
        final Example.Criteria criteria = example.createCriteria();
        try {
            final Field declaredField = clazz.getDeclaredField(prop);
            criteria.andEqualTo(declaredField.getName(), value);
        } catch (Exception e) {
            MyLogUtil.error(e.getMessage());
        }
        return example;
    }
    public void updateByProperty(T model, String prop, Object value){
        final Example example = generateExample(prop, value);
        mapper.updateByExampleSelective(model, example);
    }
    @Override
    public T findById(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T findBy(String fieldName, Object value) throws TooManyResultsException {
        T model = null;
        try {
            model = clazz.getConstructor().newInstance();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return mapper.selectOne(model);
    }

    @Override
    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> findByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    public T findOneByEntity(T condition){
        return mapper.selectOne(condition);
    }

    public List<T> findAllByProperty(String prop, Object value){
        final Example example = generateExample(prop, value);
        return mapper.selectByExample(example);
    }
    //默认降序
    public List<T> findAllByPropertyOrderBy(String prop, Object value,String orderBy){
        return findAllByPropertyOrderBy(prop, value, orderBy, true);
    }
    public List<T> findAllByPropertyOrderBy(String prop, Object value,String orderBy, boolean isDesc){
        final Example example = generateExample(prop, value);
        if(isDesc) example.orderBy(orderBy).desc();
        else example.orderBy(orderBy).asc();
        return mapper.selectByExample(example);
    }

    public List<T> findByEntity(T condition){
        return mapper.select(condition);
    }
}
