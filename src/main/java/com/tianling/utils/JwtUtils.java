package com.tianling.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/1/27 18:18
 */
public class JwtUtils {

    public static final long EXPIRATION = 1000 * 24 * 60 * 60 * 7;

    private static final String ROLE_CLAIMS = "role";

    private static final String AUTHORITY_CLAIMS = "authority";

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private static String SECRET_BASE64_KEY = "LX2lnm1cyaHUuPHWxb02Txzl5yTx2lqfRthIFrHO+zQ=" ;

    /**
     * 生成签名的时候使用的秘钥.
     * 注意：切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = java.util.Base64.getDecoder().decode(SECRET_BASE64_KEY);
        return new SecretKeySpec(encodedKey, SignatureAlgorithm.HS256.getJcaName());
    }


    /**
     * 生成token
     * @param username
     * @param authority
     * @return
     */
    public static String createToken(String username,String role,String authority) {
        Map<String,Object> map = new HashMap<>();
        map.put(AUTHORITY_CLAIMS, authority);
        map.put(ROLE_CLAIMS, role);

        log.info(role);
        String token = Jwts
                .builder()
                .setClaims(map)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(generalKey()).compact();
        return token;
    }

    public static Claims checkJWT(String token) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(generalKey()).build()
                .parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * 获取用户名
     * @param token
     * @return
     */
    public static String getUsername(String token){
        Claims claims = JwtUtils.checkJWT(token);
        return claims.getSubject();
    }

    public static String getUsername( Claims claims){
        return claims.getSubject();
    }

    /**
     * 获取用户角色
     * @param token
     * @return
     */
    public static String getUserRole(String token){
        Claims claims = JwtUtils.checkJWT(token);
        return claims.get(ROLE_CLAIMS).toString();
    }

    public static String getUserRole( Claims claims){
        return claims.get(ROLE_CLAIMS).toString();
    }

    /**
     * 获取用户角色
     * @param token
     * @return
     */
    public static String getUserAuthority(String token){
        Claims claims = JwtUtils.checkJWT(token);
        return claims.get(AUTHORITY_CLAIMS).toString();
    }

    public static String getUserAuthority( Claims claims){
        return claims.get(AUTHORITY_CLAIMS).toString();
    }

    /**
     * 是否过期
     * @param token
     * @return
     */
    public static boolean isExpiration(String token){
        Claims claims = JwtUtils.checkJWT(token);
        return claims.getExpiration().before(new Date());
    }



}
