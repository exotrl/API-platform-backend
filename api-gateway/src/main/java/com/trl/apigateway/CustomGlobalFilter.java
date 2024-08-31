package com.trl.apigateway;

import com.trl.apiinterfaceclientsdk.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    public static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("唯一请求标识" + request.getId());
        log.info("唯一请求标识" + request.getPath().value());
        log.info("唯一请求标识" + request.getMethod());
        log.info("唯一请求标识" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("唯一请求标识" + sourceAddress);
        log.info("唯一请求标识" + request.getRemoteAddress());

        // 2. 黑白名单
           //拿到响应体
        ServerHttpResponse response = exchange.getResponse();
        if(!IP_WHITE_LIST.contains(sourceAddress)) {
            // 设置响应码为403
            response.setStatusCode(HttpStatus.FORBIDDEN);
            // 返回处理完成的响应
            return response.setComplete();
        }

        // 3. 用户鉴权（sk，ak）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String body = headers.getFirst("body");
        String sign = headers.getFirst("sign");

        //todo 实际应该是去数据库中查看是否分配给用户  同时拿到secretKey
        if(!"trl".equals(accessKey)){
            return handleNoAuth(response);
        }
        // todo  这里应该在数据库中看是否存在  如果存在  说明之前用过  就抛出异常
        if (Long.parseLong(nonce) > 10000L)
        {
            return handleNoAuth(response);
        }
        // timestamp和当前时间不能超过五分钟  校验timestamp
        Long currentTime = System.currentTimeMillis() / 1000;
         final Long FIVE_MINUTES = 60 * 5L;
        if (currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
            return handleNoAuth(response);
        }

        // 生成sk 并比较
        String serverSgin = SignUtil.genSign(body, "abcdefg");
        if(!sign.equals(serverSgin)){
            return handleNoAuth(response);
        }

        // 4. 判断请求接口地址是否存在
        // todo 从数据库中查询模拟接口是否存在以及 请求方法是否匹配

        // 5.请求转发  调用模拟接口
        Mono<Void> filter = chain.filter(exchange);

        // 6. 响应日志
        log.info("响应" + response.getStatusCode());

        // 7.todo 调用成功接口调用次数+1
        if (response.getStatusCode() == HttpStatus.OK) {

        } else {
            // 8. 调用失败  返回一个规范的错误码
            handleInvokeError(response);

        }
        return filter;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        // 设置响应码为403
        response.setStatusCode(HttpStatus.FORBIDDEN);
        // 返回处理完成的响应
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        // 设置响应码为403
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 返回处理完成的响应
        return response.setComplete();
    }
}