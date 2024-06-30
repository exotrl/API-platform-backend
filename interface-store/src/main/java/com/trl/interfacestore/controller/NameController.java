package com.trl.interfacestore.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.trl.apiinterfaceclientsdk.model.User;
import com.trl.apiinterfaceclientsdk.utils.SignUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name){
        return "get: your name is"+ name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return "post11111: your name is"+ name;
    }

    @PostMapping("/json")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String body = request.getHeader("body");
        String sign = request.getHeader("sign");

        //todo 实际应该是去数据库中查看是否分配给用户  同时拿到secretKey
        if(!accessKey.equals("trl")){
            throw new RuntimeException("no access!!!!!!!!!!!!");
        }
        // todo  这里应该在数据库中看是否存在  如果存在  说明之前用过  就抛出异常
        // 这里只是示例代码
        if (Long.parseLong(nonce) > 10000)
        {
            throw new RuntimeException("no access!!!!!!!!!!!!");
        }
        // todo timestamp和当前时间不能超过五分钟  校验timestamp
        String serverSgin = SignUtil.genSign(body, "abcdefg");
        if(!sign.equals(serverSgin)){
            throw new RuntimeException("no access!!!!!!!!!!!!");
        }
        return "post222222 your name is"+ user.getUsername();
    }

}
