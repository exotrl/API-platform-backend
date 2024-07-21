package com.trl.apicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.trl.apicommon.model.entity.InterfaceInfo;


/**
* @author tianrunlei
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2024-06-23 17:42:15
*/
public interface InnerInterfaceInfoService{
    /**
     * 从数据库中查询模拟接口是否存在
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
