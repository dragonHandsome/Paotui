package org.dragon.paotui.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.dragon.paotui.mapper.TaskCertificateMapper;
import org.dragon.paotui.payload.TaskCancelQuery;
import org.dragon.paotui.pojo.TaskCertificate;
import org.dragon.paotui.pojo.WechatTaskCancel;
import org.dragon.paotui.service.TaskService;
import org.dragon.paotui.service.WechatTaskCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class WechatTaskCancelServiceImpl extends AbstractService<WechatTaskCancel> implements WechatTaskCancelService{
    @Autowired
    TaskService taskService;
    @Autowired
    TaskCertificateMapper certificateMapper;
    @Override
    @Transactional
    public PageInfo<WechatTaskCancel> getCancelApplyRecord(TaskCancelQuery query) {
        Condition condition = createCondition(query);
        List<WechatTaskCancel> wechatTaskCancels = findByCondition(condition);
        wechatTaskCancels.forEach(wechatTaskCancel -> {
            wechatTaskCancel.setTask(
                    taskService.getTaskDetail(wechatTaskCancel.getTaskId())
            );
        });
        PageInfo<WechatTaskCancel> of = PageInfo.of(wechatTaskCancels);
        return of;
    }

    @Override
    public TaskCertificate getCertificationByTaskId(Long taskId) {
        TaskCertificate condition = TaskCertificate.builder()
                .taskId(taskId).build();
        return certificateMapper.selectOne(condition);
    }

    private Condition createCondition(TaskCancelQuery query) {
        Condition condition = new Condition(WechatTaskCancel.class);
        Example.Criteria criteria = condition.createCriteria();
        if(query.getLimit() == null || query.getLimit() >= 50) {
            query.setLimit(10);
        }
        if(query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        PageHelper.startPage(query.getPage(), query.getLimit());
        switch (query.getSort()) {
            case "-id": {
                condition.orderBy("id").desc();
            } break;
        }
        if(query.getStatus() != null)
        {
            criteria.andEqualTo("status", query.getStatus());
        }
        if(query.getTaskId() != null) {
            criteria.andEqualTo("taskId", query.getTaskId());
        }
        if(query.getMinDate() != null) {
            criteria.andGreaterThanOrEqualTo("createTime", query.getMinDate());
        }
        if(query.getMaxDate() != null) {
            criteria.andLessThanOrEqualTo("createTime", query.getMaxDate());
        }
        return condition;
    }
}
