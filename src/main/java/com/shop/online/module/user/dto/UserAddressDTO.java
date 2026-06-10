package com.shop.online.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 收货地址请求
 */
@Data
@Schema(description = "收货地址请求")
public class UserAddressDTO {

    @NotBlank(message = "收件人姓名不能为空")
    @Schema(description = "收件人姓名", example = "张三")
    private String receiverName;

    @NotBlank(message = "收件人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收件人电话", example = "13800138000")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份", example = "浙江省")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市", example = "杭州市")
    private String city;

    @NotBlank(message = "区/县不能为空")
    @Schema(description = "区/县", example = "西湖区")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", example = "文三路123号")
    private String detailAddress;

    @Schema(description = "是否默认地址", example = "1")
    private Integer isDefault;
}
