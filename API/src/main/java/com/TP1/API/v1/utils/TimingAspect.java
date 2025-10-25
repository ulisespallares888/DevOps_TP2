package com.TP1.API.v1.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimingAspect {

    @Around("execution(* com.TP1.API.v1.modules.task.controller.*.*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        long inicio = System.currentTimeMillis();
        Object resultado = joinPoint.proceed();
        long fin = System.currentTimeMillis();
        long tiempo = fin - inicio;


        log.info("Método: {} - Tiempo: {} ms", joinPoint.getSignature().toShortString(), tiempo);
        return resultado;
    }

}


