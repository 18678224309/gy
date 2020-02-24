package com.jtfu.aspect;

import com.jtfu.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class ServerAspect {

    @Pointcut("execution(* com.jtfu.controller..*.*(..)) && !execution(* com.jtfu.controller.UserController.*(..))")
    public void PointCut(){

    }

    @Around("PointCut()")
    public Object  Before(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest retValue = attributes.getRequest();
        HttpServletResponse response=attributes.getResponse();
        MethodSignature signature= (MethodSignature) pjp.getSignature();
        System.out.println(signature.getMethod().getName());
        User user = (User) retValue.getSession().getAttribute("userInfo");
        System.err.println(user+"=====================");
        if (user == null) {
            response.sendRedirect("/index.html");
            return null;
        }
        return pjp.proceed();
    }
}
