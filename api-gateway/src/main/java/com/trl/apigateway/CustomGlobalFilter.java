package com.trl.apigateway;

import com.trl.apiinterfaceclientsdk.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

//        // 5.请求转发  调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);

        // 6. 响应日志
//        log.info("响应" + response.getStatusCode());
        return handleResponse(exchange, chain);
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            // 判断状态码是否为200 OK(按道理来说,现在没有调用,是拿不到响应码的,对这个保持怀疑 沉思.jpg)
            if(statusCode == HttpStatus.OK) {
                // 创建一个装饰后的响应对象(开始穿装备，增强能力)
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                    // 就会调用writeWith方法,我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 返回一个处理后的响应体
                            // (这里就理解为它在拼接字符串,它把缓冲区的数据取出来，一点一点拼接好)
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7.todo 调用成功接口调用次数+1
                                // 读取响应体的内容并转换为字节数组
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                // 打印响应日志
                                log.info("响应结果" + data);
                                // 将处理后的内容重新包装成DataBuffer并返回
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器链,并继续处理(设置repsonse对象为装饰过的)
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 对于非200 OK的请求，直接返回，进行降级处理
            return chain.filter(exchange);
        }catch (Exception e){
            // 处理异常情况，记录错误日志
            log.error("网关响应异常" + e);
            return chain.filter(exchange);
        }
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