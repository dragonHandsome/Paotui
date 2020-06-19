package org.dragon.paotui.service;

import org.dragon.paotui.pojo.WechatCategory;
import org.dragon.paotui.pojo.WechatOption;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface CategoryService extends BaseService<WechatCategory>{

    List<WechatCategory> selectAll();

    List<WechatOption> findOptionsByCategoryId(Long categoryId);

}
