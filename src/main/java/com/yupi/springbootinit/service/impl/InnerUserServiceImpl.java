package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.trl.apicommon.model.entity.User;

import com.trl.apicommon.service.InnerUserService;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserMapper;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;
    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("accessKey", accessKey);
        //return userMapper.selectOne(userQueryWrapper);
        return new User();
    }
}
