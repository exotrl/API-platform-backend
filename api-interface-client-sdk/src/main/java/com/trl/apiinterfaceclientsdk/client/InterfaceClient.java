package com.trl.apiinterfaceclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.trl.apiinterfaceclientsdk.model.User;
import com.trl.apiinterfaceclientsdk.utils.*;
import java.util.HashMap;
import java.util.Map;

import static com.trl.apiinterfaceclientsdk.utils.SignUtil.genSign;


/**
 * 调用第三方接口的客户端
 */
public class InterfaceClient {

    private String accessKey;
    private String secretKey;

    public InterfaceClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result3= HttpUtil.get("http://localhost:8111/api/user/", paramMap);
        return  result3;
    }

    public String getNameByPost(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result3= HttpUtil.post("http://localhost:8111/api/user/", paramMap);
        return  result3;
    }

    public Map<String, String> getHeader(String body){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
//        hashMap.put("secretKey", secretKeyÏ);  不能发送
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }



    public String getUsernameByPost(User user){

        String json = JSONUtil.toJsonStr(user);
        HttpResponse re = HttpRequest.post("http://localhost:8111/api/user/json")
                .addHeaders(getHeader(json))
                .body(json)
                .execute();
        int status = re.getStatus();
        System.out.println(status);
        String body = re.body();
        return body;
    }

}
