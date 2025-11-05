package com.library.app.service;

import com.library.app.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;;
@Service
public class JWTService {
    private Key sercetKey;
    JWTService(){
        String secret = "ThisWebsiteDesignIsverysecureanditsisbestlibaryappavalableinthemarketplaceofthiswebsite";

        try {
            sercetKey = Keys.hmacShaKeyFor(secret.getBytes("UTF-8"));
            System.out.println(Arrays.toString(sercetKey.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public String getToken(UserModel userModel) {
        Map<String ,Object> claims = new HashMap<>();

        return  Jwts.builder()
                .claims(claims)
                .subject(userModel.getName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 60*60*60*60*60))
                .signWith(sercetKey)
                .compact();
    }
    public String getUsername(String token){
        return extractClaim(token , Claims :: getSubject);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {

        Claims claims = extractAllClaims(token);
        if (claims!=null) return claimResolver.apply(claims);
        else return null;
    }
    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parser()
                    .verifyWith((SecretKey) sercetKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e){
            return null;
        }

    }
    public boolean validationToken(String token, String username, UserDetails userDetails) {
        try{
            return (username.equals(userDetails.getUsername()) && !isTokenExpi(token));
        }
        catch (Exception e){
            return false;
        }

    }

    private boolean isTokenExpi(String token) {
        return extractExpirdate(token).before(new Date());
    }

    private Date extractExpirdate(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
}
