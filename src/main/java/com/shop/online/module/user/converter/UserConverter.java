package com.shop.online.module.user.converter;

import com.shop.online.module.user.entity.User;
import com.shop.online.module.user.entity.UserAddress;
import com.shop.online.module.user.vo.UserAddressVO;
import com.shop.online.module.user.vo.UserProfileVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户模块对象转换器
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    /**
     * User → UserProfileVO (手机号脱敏)
     */
    @Mapping(target = "phone", expression = "java(maskPhone(user.getPhone()))")
    UserProfileVO toProfileVO(User user);

    /**
     * UserAddress → UserAddressVO (手机号脱敏)
     */
    @Mapping(target = "receiverPhone", expression = "java(maskPhone(address.getReceiverPhone()))")
    UserAddressVO toAddressVO(UserAddress address);

    /**
     * List<UserAddress> → List<UserAddressVO>
     */
    List<UserAddressVO> toAddressVOList(List<UserAddress> addresses);

    /**
     * 手机号脱敏: 138****8000
     */
    default String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
