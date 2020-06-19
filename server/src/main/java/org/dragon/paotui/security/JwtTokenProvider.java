package org.dragon.paotui.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.dragon.paotui.payload.JwtTakenData;
import org.dragon.paotui.utils.JSONUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
@Log
public class JwtTokenProvider {

    private String secret;//密钥

    private Integer expirationInMs;//多久过期 单位ms


    public String generateToken(String identification){
        return generateToken(identification, expirationInMs);
    }
    public String generateToken(String identification, Integer expirationInMs){
        if(!StringUtils.hasText(identification)) throwError("jwt存入数据为空");
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        return Jwts.builder()
                .setSubject(identification)
                .setIssuedAt(now)//生效时间
                .setExpiration(expiryDate)//过期时间
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())//加密方式
                .compact();
    }

    private void throwError(String msg) {
        log.warning(msg);
        throw new RuntimeException(msg);
    }

    //获取jwt中间部分信息 最终获取用户username
    public String getIdentificationFromJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        if(!StringUtils.hasText(username)) throwError("jwt解析数据为空");
        return username;
    }
    //解析验证token是否有效
    public Boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return false;
    }
}
