package com.tianling.config;

import com.tianling.handler.auth.converter.AuthenticationConverter;
import com.tianling.handler.auth.exception.AuthEntryPointException;
import com.tianling.handler.auth.failure.JsonServerAuthenticationFailureHandler;
import com.tianling.handler.auth.filters.TokenAuthenticationFilter;
import com.tianling.handler.auth.filters.ValidatorFilter;
import com.tianling.handler.auth.logout.JsonServerLogoutSuccessHandler;
import com.tianling.handler.auth.manager.AuthenticationManager;
import com.tianling.handler.auth.manager.AuthorizeConfigManager;
import com.tianling.handler.auth.manager.TelPhoneAuthenticationManager;
import com.tianling.handler.auth.success.JsonServerAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import javax.annotation.Resource;

/**
 * @author TianLing
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Resource
    private AuthenticationConverter authenticationConverter;

    @Resource
    private AuthorizeConfigManager authorizeConfigManager;

    @Resource
    private AuthEntryPointException serverAuthenticationEntryPoint;

    @Resource
    private JsonServerAuthenticationSuccessHandler jsonServerAuthenticationSuccessHandler;

    @Resource
    private JsonServerAuthenticationFailureHandler jsonServerAuthenticationFailureHandler;

    @Resource
    private JsonServerLogoutSuccessHandler jsonServerLogoutSuccessHandler;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TelPhoneAuthenticationManager telPhoneAuthenticationManager;

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Resource
    private ValidatorFilter validatorFilter;

    @Value("#{'${webfilter.path}'.split(',')}")
    String[] AUTH_WHITELIST;


    @Bean
    @Order(0)
    public SecurityWebFilterChain userSpringSecurityFilterChain(ServerHttpSecurity http) {
        ServerHttpSecurity httpSecurity = http
        .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/login"))
                .formLogin().loginPage("/login").authenticationManager(authenticationManager).and();
        SecurityWebFilterChain chain = commonChain(httpSecurity).build();
        commonConvert(chain);
        return chain;
    }

    @Bean
    @Order(1)
    public SecurityWebFilterChain authSpringSecurityFilterChain(ServerHttpSecurity http) {
        ServerHttpSecurity httpSecurity = http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**"))
                .formLogin().requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/phone")).authenticationManager(telPhoneAuthenticationManager).and();
        SecurityWebFilterChain chain = commonChain(httpSecurity).build();
        commonConvert(chain);
        return chain;
    }

    public SecurityWebFilterChain adminSpringSecurityFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain chain = http
//                .addFilterAfter(validatorFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(tokenAuthenticationFilter, SecurityWebFiltersOrder.FIRST)
                .formLogin()
                .loginPage("/login")
                // 登录成功handler
                .authenticationSuccessHandler(jsonServerAuthenticationSuccessHandler)
                // 登陆失败handler
                .authenticationFailureHandler(jsonServerAuthenticationFailureHandler)
                // 无访问权限handler
                .authenticationEntryPoint(serverAuthenticationEntryPoint)

                .and()

                .logout()
                // 登出成功handler
                .logoutSuccessHandler(jsonServerLogoutSuccessHandler)
                .and()
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                // 白名单放行
                .pathMatchers(AUTH_WHITELIST)
                .permitAll()
                // 访问权限控制
                .pathMatchers("/admin/**")
                .hasAnyRole("admin")
                .anyExchange()
                .access(authorizeConfigManager)
                .and()
                .build();
        // 设置自定义登录参数转换器
        chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof AuthenticationWebFilter)
                .subscribe(webFilter -> {
                    AuthenticationWebFilter filter = (AuthenticationWebFilter) webFilter;
                    filter.setServerAuthenticationConverter(authenticationConverter);
                });
        return chain;
    }

    public ServerHttpSecurity commonChain(ServerHttpSecurity http){
        return   http
                .addFilterAfter(validatorFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(tokenAuthenticationFilter, SecurityWebFiltersOrder.FIRST)
                .formLogin()
                // 登录成功handler
                .authenticationSuccessHandler(jsonServerAuthenticationSuccessHandler)
                // 登陆失败handler
                .authenticationFailureHandler(jsonServerAuthenticationFailureHandler)
                // 无访问权限handler
                .authenticationEntryPoint(serverAuthenticationEntryPoint)
                .and()
                .logout()
                // 登出成功handler
                .logoutSuccessHandler(jsonServerLogoutSuccessHandler)
                .and()
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                // 白名单放行
                .pathMatchers(AUTH_WHITELIST)
                .permitAll()
                // 访问权限控制
                .pathMatchers("/admin/**")
                .hasAnyRole("admin")
                .anyExchange()
                .access(authorizeConfigManager)
                .and();

    }

    public  void commonConvert(SecurityWebFilterChain chain){
         chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof AuthenticationWebFilter)
                .subscribe(webFilter -> {
                    AuthenticationWebFilter filter = (AuthenticationWebFilter) webFilter;
                    filter.setServerAuthenticationConverter(authenticationConverter);
                });
    }


    /**
     * BCrypt密码编码
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 跨域配置
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        return httpServletRequest -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.addAllowedHeader("*");
            cfg.addAllowedMethod("*");
            cfg.addAllowedOrigin("*");
            cfg.setAllowCredentials(true);
            cfg.checkOrigin("*");
            return cfg;
        };
    }


}
