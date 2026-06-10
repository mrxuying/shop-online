package com.shop.online.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址返回
 */
@Data
@Schema(description = "收货地址")
public class UserAddressVO {

    @Schema(description = "地址ID", example = "1")
    private Long id;

    @Schema(description = "收件人姓名", example = "张三")
    private String receiverName;

    @Schema(description = "收件人电话", example = "138****8000")
    private String receiverPhone;

    @Schema(description = "省份", example = "浙江省")
    private String province;

    @Schema(description = "城市", example = "杭州市")
    private String city;

    @Schema(description = "区/县", example = "西湖区")
    private String district;

    @Schema(description = "详细地址", example = "文三路123号")
    private String detailAddress;

    @Schema(description = "是否默认地址", example = "1")
    private Integer isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
