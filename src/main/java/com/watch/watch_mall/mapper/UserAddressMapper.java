package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.UserAddress;
import com.watch.watch_mall.model.vo.UserAddressVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserAddressMapper extends BaseMapper<UserAddress> {

    List<UserAddressVO> listMyAddress(@Param("userId") Long userId);

    UserAddressVO selectMyAddressDetail(@Param("userId") Long userId, @Param("id") Long id);

    UserAddress selectOwnedAddress(@Param("userId") Long userId, @Param("id") Long id);

    int clearDefaultByUserId(@Param("userId") Long userId);

    UserAddress selectNextDefaultCandidate(@Param("userId") Long userId);
}
