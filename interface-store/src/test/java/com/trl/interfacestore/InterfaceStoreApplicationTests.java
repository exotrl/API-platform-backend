package com.trl.interfacestore;

import com.trl.apiinterfaceclientsdk.client.InterfaceClient;
import com.trl.apiinterfaceclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class InterfaceStoreApplicationTests {

    @Resource
    private InterfaceClient interfaceClient;

    @Test
    void contextLoads(){
        String trl = interfaceClient.getNameByGet("trl");
        User user = new User();
        user.setUsername("trl3333");
        String usernameByPost = interfaceClient.getUsernameByPost(user);
        System.out.println(trl);
        System.out.println(usernameByPost);
    }

}
