package com.example.greenalpinepeaks.aspect;

import com.example.greenalpinepeaks.exception.ServiceExecutionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(public * com.example.greenalpinepeaks.service.*.*(..))")
    public void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) {

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "Executing {}.{}() with arguments: {}",
                className,
                methodName,
                args != null && args.length > 0
                    ? Arrays.toString(args)
                    : "[]"
            );
        }

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;

            if (executionTime > 1000) {
                LOG.warn(
                    "SLOW QUERY: {}.{}() executed in {} ms",
                    className,
                    methodName,
                    executionTime
                );
            } else {
                LOG.info(
                    "{}.{}() executed in {} ms",
                    className,
                    methodName,
                    executionTime
                );
            }

            return result;

        } catch (ResponseStatusException exception) {

            LOG.warn(
                "ResponseStatusException in {}.{}(): {}",
                className,
                methodName,
                exception.getMessage()
            );

            throw exception;

        } catch (Throwable exception) {

            LOG.error(
                "Exception in {}.{}() with arguments: {}",
                className,
                methodName,
                args != null && args.length > 0
                    ? Arrays.toString(args)
                    : "[]",
                exception
            );

            throw new ServiceExecutionException(
                String.format(
                    "Exception in %s.%s(): %s",
                    className,
                    methodName,
                    exception.getMessage()
                ),
                exception
            );
        }
    }
}