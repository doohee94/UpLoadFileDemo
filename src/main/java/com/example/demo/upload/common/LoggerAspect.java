package com.example.demo.upload.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* *..controller.*.*(..)) || execution(* *..repository.*.*(..)) || execution(* *..service.*.*(..))" )
    public Object lofPring(ProceedingJoinPoint joinPoint) throws Throwable{

        String type="";
        String name = joinPoint.getSignature().getDeclaringTypeName();
        if(name.indexOf("Controller")>-1){
            type="Controller \t";
        }else if(name.indexOf("Service")>-1){
            type="Service \t";
        }else if(name.indexOf("Repository")>-1){
            type="Repository \t";
        }

        log.debug("|>>>>>>>>>>>"+type+name+"."+joinPoint.getSignature().getName()+"()<<<<<<<<<<<<<<|");
        return joinPoint.proceed();
    }
}
