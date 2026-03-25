package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.UserAddressMapper;
import com.watch.watch_mall.model.dto.address.UserAddressAddRequest;
import com.watch.watch_mall.model.dto.address.UserAddressUpdateRequest;
import com.watch.watch_mall.model.entity.UserAddress;
import com.watch.watch_mall.model.vo.UserAddressVO;
import com.watch.watch_mall.service.UserAddressService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAddress(Long userId, UserAddressAddRequest request) {
        validateUserId(userId);
        validateAddRequest(request);

        long addressCount = this.count(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId));
        boolean shouldSetDefault = addressCount == 0 || Objects.equals(request.getIsDefault(), 1);
        if (shouldSetDefault) {
            userAddressMapper.clearDefaultByUserId(userId);
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setReceiverName(StringUtils.trimToNull(request.getReceiverName()));
        userAddress.setReceiverPhone(StringUtils.trimToNull(request.getReceiverPhone()));
        userAddress.setProvince(StringUtils.trimToNull(request.getProvince()));
        userAddress.setCity(StringUtils.trimToNull(request.getCity()));
        userAddress.setDistrict(StringUtils.trimToNull(request.getDistrict()));
        userAddress.setDetailAddress(StringUtils.trimToNull(request.getDetailAddress()));
        userAddress.setPostalCode(StringUtils.trimToNull(request.getPostalCode()));
        userAddress.setIsDefault(shouldSetDefault ? 1 : 0);

        boolean saved = this.save(userAddress);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "address add failed");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(Long userId, UserAddressUpdateRequest request) {
        validateUserId(userId);
        validateUpdateRequest(request);

        UserAddress existing = getOwnedAddress(userId, request.getId());
        boolean currentDefault = Objects.equals(existing.getIsDefault(), 1);
        boolean targetDefault = Objects.equals(request.getIsDefault(), 1);

        if (targetDefault) {
            userAddressMapper.clearDefaultByUserId(userId);
        }

        existing.setReceiverName(StringUtils.trimToNull(request.getReceiverName()));
        existing.setReceiverPhone(StringUtils.trimToNull(request.getReceiverPhone()));
        existing.setProvince(StringUtils.trimToNull(request.getProvince()));
        existing.setCity(StringUtils.trimToNull(request.getCity()));
        existing.setDistrict(StringUtils.trimToNull(request.getDistrict()));
        existing.setDetailAddress(StringUtils.trimToNull(request.getDetailAddress()));
        existing.setPostalCode(StringUtils.trimToNull(request.getPostalCode()));
        existing.setIsDefault(targetDefault ? 1 : 0);

        boolean updated = this.updateById(existing);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "address update failed");

        if (currentDefault && !targetDefault) {
            ensureDefaultAddress(userId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAddress(Long userId, Long addressId) {
        validateUserId(userId);
        ThrowUtils.throwIf(addressId == null || addressId <= 0, ErrorCode.PARAMS_ERROR);

        UserAddress existing = getOwnedAddress(userId, addressId);
        boolean isDefault = Objects.equals(existing.getIsDefault(), 1);
        boolean removed = this.removeById(addressId);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "address delete failed");

        if (isDefault) {
            assignNextDefaultAddress(userId);
        }
        return true;
    }

    @Override
    public List<UserAddressVO> listMyAddress(Long userId) {
        validateUserId(userId);
        return userAddressMapper.listMyAddress(userId);
    }

    @Override
    public UserAddressVO getMyAddressDetail(Long userId, Long addressId) {
        validateUserId(userId);
        ThrowUtils.throwIf(addressId == null || addressId <= 0, ErrorCode.PARAMS_ERROR);
        UserAddressVO addressVO = userAddressMapper.selectMyAddressDetail(userId, addressId);
        ThrowUtils.throwIf(addressVO == null, ErrorCode.NOT_FOUND_ERROR, "address not found");
        return addressVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long userId, Long addressId) {
        validateUserId(userId);
        ThrowUtils.throwIf(addressId == null || addressId <= 0, ErrorCode.PARAMS_ERROR);

        UserAddress existing = getOwnedAddress(userId, addressId);
        userAddressMapper.clearDefaultByUserId(userId);
        existing.setIsDefault(1);
        boolean updated = this.updateById(existing);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "set default address failed");
        return true;
    }

    private void validateAddRequest(UserAddressAddRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        validateAddressFields(request.getReceiverName(), request.getReceiverPhone(), request.getProvince(),
                request.getCity(), request.getDistrict(), request.getDetailAddress(), request.getPostalCode(), request.getIsDefault());
    }

    private void validateUpdateRequest(UserAddressUpdateRequest request) {
        ThrowUtils.throwIf(request == null || request.getId() == null || request.getId() <= 0, ErrorCode.PARAMS_ERROR);
        validateAddressFields(request.getReceiverName(), request.getReceiverPhone(), request.getProvince(),
                request.getCity(), request.getDistrict(), request.getDetailAddress(), request.getPostalCode(), request.getIsDefault());
    }

    private void validateAddressFields(String receiverName, String receiverPhone, String province,
                                       String city, String district, String detailAddress,
                                       String postalCode, Integer isDefault) {
        ThrowUtils.throwIf(StringUtils.isBlank(receiverName) || receiverName.trim().length() > 64, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(receiverPhone) || receiverPhone.trim().length() < 6
                || receiverPhone.trim().length() > 32, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(province) || province.trim().length() > 64, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(city) || city.trim().length() > 64, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(district) || district.trim().length() > 64, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(detailAddress) || detailAddress.trim().length() > 255, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isNotBlank(postalCode) && postalCode.trim().length() > 20, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(isDefault == null || (isDefault != 0 && isDefault != 1), ErrorCode.PARAMS_ERROR);
    }

    private void validateUserId(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
    }

    private UserAddress getOwnedAddress(Long userId, Long addressId) {
        UserAddress userAddress = userAddressMapper.selectOwnedAddress(userId, addressId);
        ThrowUtils.throwIf(userAddress == null, ErrorCode.NOT_FOUND_ERROR, "address not found");
        return userAddress;
    }

    private void ensureDefaultAddress(Long userId) {
        long defaultCount = this.count(Wrappers.lambdaQuery(UserAddress.class)
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1));
        if (defaultCount > 0) {
            return;
        }
        assignNextDefaultAddress(userId);
    }

    private void assignNextDefaultAddress(Long userId) {
        UserAddress nextDefault = userAddressMapper.selectNextDefaultCandidate(userId);
        if (nextDefault == null) {
            return;
        }
        nextDefault.setIsDefault(1);
        boolean updated = this.updateById(nextDefault);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "default address restore failed");
    }
}
