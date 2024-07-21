package com.trl.apicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trl.apicommon.model.entity.UserInterfaceInfo;


/**
* @author tianrunlei
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-07-05 22:04:03
*/
public interface InnerUserInterfaceInfoService{

    /**
     * 统计接口调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
