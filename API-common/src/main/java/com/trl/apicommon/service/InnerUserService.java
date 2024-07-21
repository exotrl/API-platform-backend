package com.trl.apicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trl.apicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author Runlei Tian
 */
public interface InnerUserService{
    /**
     * 从数据库中查是否已经分配给用户sk   ak
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
