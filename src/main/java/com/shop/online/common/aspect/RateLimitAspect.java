package com.shop.online.common.aspect;

import com.shop.online.common.annotation.RateLimit;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.infrastructure.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * 接口限流切面 — Redis 简单窗口计数
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getMethod().getName();

        // 构建限流 key：前缀 + 方法 + 用户ID
        Long userId = UserContext.getCurrentUserId();
        String limitKey = rateLimit.key() + methodName;
        if (userId != null) {
            limitKey += ":" + userId;
        }

        // 获取当前计数
        Integer current = (Integer) redisTemplate.opsForValue().get(limitKey);
        if (current == null) {
            redisTemplate.opsForValue().set(limitKey, 1, Duration.ofSeconds(rateLimit.time()));
        } else if (current < rateLimit.count()) {
            Long remaining = redisTemplate.getExpire(limitKey);
            redisTemplate.opsForValue().increment(limitKey);
            if (remaining != null && remaining < 0) {
                redisTemplate.expire(limitKey, Duration.ofSeconds(rateLimit.time()));
            }
        } else {
            log.warn("接口限流触发: {}, current={}, limit={}", methodName, current, rateLimit.count());
            throw new BusinessException(ResultCode.RATE_LIMIT_EXCEEDED.getCode(), rateLimit.message());
        }

        return joinPoint.proceed();
    }
}
