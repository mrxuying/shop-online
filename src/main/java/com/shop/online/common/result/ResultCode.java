package com.shop.online.common.result;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
public enum ResultCode {

    // ==================== 通用状态码 ====================
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ==================== 用户模块 1000~1999 ====================
    USER_NOT_FOUND(1000, "用户不存在"),
    USERNAME_EXIST(1001, "用户名已存在"),
    PHONE_EXIST(1002, "手机号已注册"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1005, "用户名或密码错误"),
    CODE_EXPIRED(1006, "验证码已过期"),
    CODE_ERROR(1007, "验证码错误"),
    OLD_PASSWORD_ERROR(1008, "原密码错误"),
    ADDRESS_NOT_FOUND(1009, "收货地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(1010, "收货地址已达上限(20个)"),
    TOKEN_EXPIRED(1011, "Token已过期，请重新登录"),
    TOKEN_INVALID(1012, "Token无效"),

    // ==================== 商品模块 2000~2999 ====================
    PRODUCT_NOT_FOUND(2000, "商品不存在"),
    CATEGORY_NOT_FOUND(2001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(2002, "分类下有子分类，无法删除"),
    CATEGORY_HAS_PRODUCTS(2003, "分类下有商品，无法删除"),
    SKU_NOT_FOUND(2004, "商品规格不存在"),
    STOCK_NOT_ENOUGH(2005, "商品库存不足"),
    PRODUCT_OFF_SHELF(2006, "商品已下架"),

    // ==================== 购物车模块 3000~3999 ====================
    CART_ITEM_EXIST(3000, "购物车中已存在该商品"),
    CART_ITEM_NOT_FOUND(3001, "购物车商品不存在"),
    CART_ITEM_LIMIT_EXCEEDED(3002, "购物车商品数量已达上限"),

    // ==================== 订单模块 4000~4999 ====================
    ORDER_NOT_FOUND(4000, "订单不存在"),
    ORDER_STATUS_ERROR(4001, "订单状态不允许此操作"),
    ORDER_CANNOT_CANCEL(4002, "订单无法取消"),
    ORDER_CANNOT_CONFIRM(4003, "订单无法确认收货"),
    ORDER_CREATE_FAILED(4004, "订单创建失败"),
    ORDER_ITEM_EMPTY(4005, "订单商品不能为空"),

    // ==================== 支付模块 5000~5999 ====================
    PAY_FAILED(5000, "支付失败"),
    PAY_ORDER_NOT_FOUND(5001, "支付订单不存在"),
    PAY_AMOUNT_ERROR(5002, "支付金额不匹配"),
    REFUND_FAILED(5003, "退款失败"),
    REFUND_AMOUNT_ERROR(5004, "退款金额不合法"),

    // ==================== 管理端 6000~6999 ====================
    ADMIN_NOT_FOUND(6000, "管理员不存在"),
    ADMIN_PASSWORD_ERROR(6001, "管理员密码错误"),
    ADMIN_DISABLED(6002, "管理员账号已被禁用"),

    // ==================== 系统通用 9000~9999 ====================
    SYSTEM_ERROR(9000, "系统繁忙，请稍后再试"),
    SERVICE_UNAVAILABLE(9001, "服务暂不可用"),
    RATE_LIMIT_EXCEEDED(9002, "请求过于频繁，请稍后再试");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
