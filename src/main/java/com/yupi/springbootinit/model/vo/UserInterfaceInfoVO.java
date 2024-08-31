package com.yupi.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yupi.springbootinit.model.entity.UserInterfaceInfo;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class UserInterfaceInfoVO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常   1-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer isDelete;

    /**
     * 包装类转对象
     *
     * @param userInterfaceInfoVO
     * @return
     */
    public static UserInterfaceInfo voToObj(UserInterfaceInfoVO userInterfaceInfoVO) {
        if (userInterfaceInfoVO == null) {
            return null;
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoVO, userInterfaceInfo);
        return userInterfaceInfo;
    }

    /**
     * 对象转包装类
     *
     * @param userInterfaceInfo
     * @return
     */
    public static UserInterfaceInfoVO objToVo(UserInterfaceInfo userInterfaceInfo) {
        if (userInterfaceInfo == null) {
            return null;
        }
        UserInterfaceInfoVO interfaceInfoVO = new UserInterfaceInfoVO();
        BeanUtils.copyProperties(userInterfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
}
