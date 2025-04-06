package com.jwt_authentication.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger=LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        Authorization =Bearer wghieurfmnd
        String requestHeader=request.getHeader("Authorization");
        logger.info("Header :{}",requestHeader);

        String userName=null;
        String token=null;

        if(requestHeader!=null && requestHeader.startsWith("Bearer")){
            token=requestHeader.substring(7);
            try{
                userName=this.jwtHelper.getUserNameFromToken(token);

            }catch (IllegalArgumentException e){
                logger.info("Illegal Argument while fetching the token! ");
                e.printStackTrace();
            }catch (ExpiredJwtException e){
                logger.info("token time is expired");
                e.printStackTrace();
            }catch (MalformedJwtException e){
                logger.info("Some changes has done in the token !! Invalid token");
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            logger.info("Validation failed!");
        }

        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(userName);
            Boolean validToken=this.jwtHelper.validateToken(token,userDetails);
            if(validToken){
                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                logger.info("validations fails!");;
            }
        }
        filterChain.doFilter(request,response);
    }
}
