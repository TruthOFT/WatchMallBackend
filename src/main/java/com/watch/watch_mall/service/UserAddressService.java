package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.address.UserAddressAddRequest;
import com.watch.watch_mall.model.dto.address.UserAddressUpdateRequest;
import com.watch.watch_mall.model.entity.UserAddress;
import com.watch.watch_mall.model.vo.UserAddressVO;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    boolean addAddress(Long userId, UserAddressAddRequest request);

    boolean updateAddress(Long userId, UserAddressUpdateRequest request);

    boolean deleteAddress(Long userId, Long addressId);

    List<UserAddressVO> listMyAddress(Long userId);

    UserAddressVO getMyAddressDetail(Long userId, Long addressId);

    boolean setDefaultAddress(Long userId, Long addressId);
}
