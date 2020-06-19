package org.dragon.paotui.service;

import com.github.pagehelper.PageInfo;
import org.dragon.paotui.payload.TaskCancelQuery;
import org.dragon.paotui.pojo.TaskCertificate;
import org.dragon.paotui.pojo.WechatTaskCancel;

import java.util.List;

public interface WechatTaskCancelService extends BaseService<WechatTaskCancel>{
    PageInfo<WechatTaskCancel> getCancelApplyRecord(TaskCancelQuery query);

    TaskCertificate getCertificationByTaskId(Long taskId);
}
