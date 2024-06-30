package com.trl.apiinterfaceclientsdk;

import com.trl.apiinterfaceclientsdk.client.InterfaceClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("api.client")
@Data
@ComponentScan
public class ApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public InterfaceClient interfaceClient(){

      return new InterfaceClient(accessKey,secretKey);

    }
}
