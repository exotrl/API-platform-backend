package com.yupi.springbootinit.service.impl;

import com.trl.apicommon.service.InnerUserInterfaceInfoService;
import com.yupi.springbootinit.service.UserInterfaceInfoService;

public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    private UserInterfaceInfoService userInterfaceInfoService;
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}
