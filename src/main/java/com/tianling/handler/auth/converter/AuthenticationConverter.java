package com.tianling.handler.auth.converter;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/1/28 23:11
 */
import com.tianling.handler.auth.token.AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationConverter extends ServerFormLoginAuthenticationConverter {

    private String usernameParameter = "username";

    private String passwordParameter = "password";

    private String userPhone = "userPhone";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData()
                .map(data -> {
                    String username = data.getFirst(this.usernameParameter);
                    String password = data.getFirst(this.passwordParameter);
                    String userPhone = data.getFirst(this.userPhone);
                    return new AuthenticationToken(username, password,userPhone);
                });
    }

}