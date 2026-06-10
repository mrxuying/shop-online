package com.shop.online.common.constant;

/**
 * 应用级常量
 */
public final class AppConstants {

    private AppConstants() {
    }

    // ==================== 分页 ====================
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 地址 ====================
    public static final int MAX_ADDRESS_COUNT = 20;

    // ==================== 购物车 ====================
    public static final int MAX_CART_ITEM_COUNT = 99;
    public static final String CART_REDIS_KEY_PREFIX = "cart:user:";

    // ==================== 订单 ====================
    public static final int ORDER_TIMEOUT_MINUTES = 30;
    public static final String ORDER_TIMEOUT_KEY_PREFIX = "order:timeout:";

    // ==================== 验证码 ====================
    public static final int VERIFY_CODE_EXPIRE_SECONDS = 300;
    public static final String VERIFY_CODE_KEY_PREFIX = "verify:code:";

    // ==================== Token ====================
    public static final String TOKEN_BLACKLIST_KEY_PREFIX = "token:blacklist:";
    public static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:token:";

    // ==================== 商品缓存 ====================
    public static final String PRODUCT_CACHE_KEY = "cache:product:";
    public static final String CATEGORY_CACHE_KEY = "cache:category:tree";
    public static final long PRODUCT_CACHE_TTL_MINUTES = 30;
}
