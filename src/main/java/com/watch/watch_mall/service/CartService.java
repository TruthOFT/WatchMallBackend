package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.cart.AddCartRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemCheckedRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemQuantityRequest;
import com.watch.watch_mall.model.entity.Cart;
import com.watch.watch_mall.model.vo.CartVO;

public interface CartService extends IService<Cart> {

    boolean addCart(Long userId, AddCartRequest addCartRequest);

    CartVO getMyCart(Long userId);

    boolean updateCartItemQuantity(Long userId, UpdateCartItemQuantityRequest request);

    boolean updateCartItemChecked(Long userId, UpdateCartItemCheckedRequest request);

    boolean deleteCartItem(Long userId, Long cartItemId);
}
