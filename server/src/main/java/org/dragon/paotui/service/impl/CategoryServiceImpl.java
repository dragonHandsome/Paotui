package org.dragon.paotui.service.impl;

import org.dragon.paotui.mapper.CategoryMapper;
import org.dragon.paotui.pojo.WechatCategory;
import org.dragon.paotui.pojo.WechatOption;
import org.dragon.paotui.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends AbstractService<WechatCategory> implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    @Cacheable("categories")
    public List<WechatCategory> selectAll() {
        List<WechatCategory> wechatCategories = categoryMapper.selectAll();
        return wechatCategories;
    }

    @Override
    @Cacheable(value = "wechatOption",key = "#p0")
    public List<WechatOption> findOptionsByCategoryId(Long categoryId) {
        return categoryMapper.findOptionsByCategoryId(categoryId);
    }

    @Override
    @Cacheable(value = "category", key = "#p0")
    public WechatCategory findById(Long id) {
        return super.findById(id);
    }
}
