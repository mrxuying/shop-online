package com.shop.online.module.user;

import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.infrastructure.security.JwtUtils;
import com.shop.online.module.user.converter.UserConverter;
import com.shop.online.module.user.dto.*;
import com.shop.online.module.user.entity.User;
import com.shop.online.module.user.entity.UserAddress;
import com.shop.online.module.user.mapper.UserAddressMapper;
import com.shop.online.module.user.mapper.UserMapper;
import com.shop.online.module.user.service.impl.UserServiceImpl;
import com.shop.online.module.user.vo.UserAddressVO;
import com.shop.online.module.user.vo.UserLoginVO;
import com.shop.online.module.user.vo.UserProfileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserAddressMapper addressMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserConverter userConverter;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserAddress testAddress;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encoded_password");
        testUser.setNickname("测试用户");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setGender(1);
        testUser.setCreateTime(LocalDateTime.now());

        testAddress = new UserAddress();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setReceiverName("张三");
        testAddress.setReceiverPhone("13800138000");
        testAddress.setProvince("浙江省");
        testAddress.setCity("杭州市");
        testAddress.setDistrict("西湖区");
        testAddress.setDetailAddress("文三路123号");
        testAddress.setIsDefault(1);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== 注册 ====================

    @Test
    @DisplayName("用户注册 — 成功")
    void shouldRegisterSuccessfully() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setPhone("13900139000");

        when(userMapper.selectCount(any(User.class))).thenReturn(0L);
        when(passwordEncoder.encode("123456")).thenReturn("encoded_password");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> userService.register(dto));

        // Then
        verify(userMapper, times(2)).selectCount(any(User.class));
        verify(passwordEncoder).encode("123456");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册 — 用户名已存在")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setPhone("13900139000");

        when(userMapper.selectCount(any(User.class))).thenReturn(1L);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(dto));
        assertEquals(ResultCode.USERNAME_EXIST.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("用户注册 — 手机号已存在")
    void shouldThrowExceptionWhenPhoneExists() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("123456");
        dto.setPhone("13800138000");

        when(userMapper.selectCount(any(User.class)))
                .thenReturn(0L)
                .thenReturn(1L);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(dto));
        assertEquals(ResultCode.PHONE_EXIST.getCode(), ex.getCode());
    }

    // ==================== 登录 ====================

    @Test
    @DisplayName("用户登录 — 成功")
    void shouldLoginSuccessfully() {
        // Given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");

        when(userMapper.selectByUsernameOrPhone("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("123456", testUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateAccessToken(1L, "testuser")).thenReturn("access_token");
        when(jwtUtils.generateRefreshToken(1L, "testuser")).thenReturn("refresh_token");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        UserLoginVO result = userService.login(dto);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("access_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
        verify(userMapper).selectByUsernameOrPhone("testuser");
        verify(passwordEncoder).matches("123456", testUser.getPassword());
        verify(jwtUtils).generateAccessToken(1L, "testuser");
        verify(jwtUtils).generateRefreshToken(1L, "testuser");
    }

    @Test
    @DisplayName("用户登录 — 用户名或密码错误")
    void shouldThrowExceptionWhenCredentialsWrong() {
        // Given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrong_password");

        when(userMapper.selectByUsernameOrPhone("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong_password", testUser.getPassword())).thenReturn(false);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(dto));
        assertEquals(ResultCode.USERNAME_OR_PASSWORD_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("用户登录 — 用户不存在")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername("nonexist");
        dto.setPassword("123456");

        when(userMapper.selectByUsernameOrPhone("nonexist")).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(dto));
        assertEquals(ResultCode.USERNAME_OR_PASSWORD_ERROR.getCode(), ex.getCode());
    }

    // ==================== 个人信息 ====================

    @Test
    @DisplayName("获取个人信息 — 成功")
    void shouldGetProfile() {
        // Given
        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(1L);
        profileVO.setUsername("testuser");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userConverter.toProfileVO(testUser)).thenReturn(profileVO);

        // When
        UserProfileVO result = userService.getProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取个人信息 — 用户不存在")
    void shouldThrowExceptionWhenProfileNotFound() {
        // Given
        when(userMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.getProfile(999L));
    }

    // ==================== 地址管理 ====================

    @Test
    @DisplayName("获取地址列表 — 成功")
    void shouldListAddresses() {
        // Given
        List<UserAddress> addresses = Arrays.asList(testAddress);
        UserAddressVO addressVO = new UserAddressVO();
        addressVO.setId(1L);
        addressVO.setReceiverName("张三");

        when(addressMapper.selectList(any(UserAddress.class))).thenReturn(addresses);
        when(userConverter.toAddressVOList(addresses)).thenReturn(Arrays.asList(addressVO));

        // When
        List<UserAddressVO> result = userService.listAddresses(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getReceiverName());
        verify(addressMapper).selectList(any(UserAddress.class));
    }

    @Test
    @DisplayName("新增地址 — 成功")
    void shouldCreateAddress() {
        // Given
        UserAddressDTO dto = new UserAddressDTO();
        dto.setReceiverName("李四");
        dto.setReceiverPhone("13700137000");
        dto.setProvince("北京市");
        dto.setCity("北京市");
        dto.setDistrict("朝阳区");
        dto.setDetailAddress("望京SOHO");
        dto.setIsDefault(0);

        when(addressMapper.selectCount(any(UserAddress.class))).thenReturn(0L);
        doAnswer(invocation -> {
            UserAddress address = invocation.getArgument(0);
            address.setId(2L);
            return 1;
        }).when(addressMapper).insert(any(UserAddress.class));

        // When
        Long addressId = userService.createAddress(1L, dto);

        // Then
        assertNotNull(addressId);
        assertEquals(2L, addressId);
        verify(addressMapper).selectCount(any(UserAddress.class));
        verify(addressMapper).insert(any(UserAddress.class));
    }

    @Test
    @DisplayName("删除地址 — 成功")
    void shouldDeleteAddress() {
        // Given
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.deleteById(1L)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> userService.deleteAddress(1L, 1L));

        // Then
        verify(addressMapper).selectById(1L);
        verify(addressMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除地址 — 地址不属于当前用户")
    void shouldThrowExceptionWhenAddressNotBelongToUser() {
        // Given
        UserAddress otherUserAddress = new UserAddress();
        otherUserAddress.setId(1L);
        otherUserAddress.setUserId(2L);

        when(addressMapper.selectById(1L)).thenReturn(otherUserAddress);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.deleteAddress(1L, 1L));
    }
}
