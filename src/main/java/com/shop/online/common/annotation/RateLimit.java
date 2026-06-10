package com.shop.online.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解 — 基于 Redis 滑动窗口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 key 前缀
     */
    String key() default "rate:limit:";

    /**
     * 时间窗口（秒）
     */
    int time() default 1;

    /**
     * 窗口内最大请求数
     */
    int count() default 10;

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}
