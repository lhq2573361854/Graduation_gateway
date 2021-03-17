package com.tianling.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author TianLing
 */
@Configuration
public class GateWayBasicConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

//    @Primary
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
//                                                             ServerCodecConfigurer serverCodecConfigurer) {
//        GatewayExceptionHandler gatewayExceptionHandler = new GatewayExceptionHandler();
//        gatewayExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
//        gatewayExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
//        gatewayExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
//        return gatewayExceptionHandler;
//    }

}