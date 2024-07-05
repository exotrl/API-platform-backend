package com.yupi.springbootinit.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @author Runlei Tian
 */
@Data
public class UserInterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    /**
     * 用户请求参数
     */
    private String userRequestParams;


    private static final long serialVersionUID = 1L;
}