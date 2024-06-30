package com.trl.apiinterfaceclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class SignUtil {
    /**
     * 生成签名
     * @param body
     * @param secretKey
     * @return
     */
    public static String genSign(String body, String secretKey){
        String content = body+ "." +secretKey;
        String md5Hex1 = DigestUtil.md5Hex(content);
        return md5Hex1;
    }
}
