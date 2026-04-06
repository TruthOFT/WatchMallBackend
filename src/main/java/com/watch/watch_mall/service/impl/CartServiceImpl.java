package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.ThrowUtils;
import com.watch.watch_mall.mapper.CartItemMapper;
import com.watch.watch_mall.mapper.CartMapper;
import com.watch.watch_mall.model.dto.cart.AddCartRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemCheckedRequest;
import com.watch.watch_mall.model.dto.cart.UpdateCartItemQuantityRequest;
import com.watch.watch_mall.model.entity.Cart;
import com.watch.watch_mall.model.entity.CartItem;
import com.watch.watch_mall.model.entity.Product;
import com.watch.watch_mall.model.entity.ProductSkus;
import com.watch.watch_mall.model.vo.CartItemRowVO;
import com.watch.watch_mall.model.vo.CartItemVO;
import com.watch.watch_mall.model.vo.CartVO;
import com.watch.watch_mall.service.CartService;
import com.watch.watch_mall.service.ProductService;
import com.watch.watch_mall.service.ProductSkusService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Resource
    private CartItemMapper cartItemMapper;

    @Resource
    private ProductService productService;

    @Resource
    private ProductSkusService productSkusService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCart(Long userId, AddCartRequest addCartRequest) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(addCartRequest == null
                        || addCartRequest.getSkuId() == null
                        || addCartRequest.getSkuId() <= 0
                        || addCartRequest.getQuantity() == null
                        || addCartRequest.getQuantity() <= 0,
                ErrorCode.PARAMS_ERROR);

        ProductSkus sku = productSkusService.getById(addCartRequest.getSkuId());
        ThrowUtils.throwIf(sku == null || sku.getProductId() == null, ErrorCode.NOT_FOUND_ERROR, "sku not found");

        Product product = productService.getById(sku.getProductId());
        ThrowUtils.throwIf(product == null || !Objects.equals(product.getStatus(), 1),
                ErrorCode.NOT_FOUND_ERROR, "product not available");

        int requestQuantity = addCartRequest.getQuantity();
        int stock = sku.getStock() == null ? 0 : sku.getStock();
        ThrowUtils.throwIf(requestQuantity > stock, ErrorCode.OPERATION_ERROR, "insufficient stock");

        Cart cart = getOrCreateCart(userId);
        CartItem existingItem = cartItemMapper.selectAnyByCartIdAndSkuId(cart.getId(), sku.getId());

        if (existingItem != null) {
            if (Objects.equals(existingItem.getIsDelete(), 1)) {
                boolean restored = cartItemMapper.restoreCartItem(
                        existingItem.getId(),
                        product.getId(),
                        userId,
                        requestQuantity,
                        1,
                        sku.getPrice()
                ) > 0;
                ThrowUtils.throwIf(!restored, ErrorCode.OPERATION_ERROR, "cart restore failed");
                return true;
            }
            long targetQuantity = (long) existingItem.getQuantity() + requestQuantity;
            ThrowUtils.throwIf(targetQuantity > stock, ErrorCode.OPERATION_ERROR, "insufficient stock");
            existingItem.setQuantity((int) targetQuantity);
            existingItem.setChecked(1);
            existingItem.setPrice(sku.getPrice());
            boolean updated = cartItemMapper.updateById(existingItem) > 0;
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "cart update failed");
            return true;
        }

        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setUserId(userId);
        cartItem.setProductId(product.getId());
        cartItem.setSkuId(sku.getId());
        cartItem.setQuantity(requestQuantity);
        cartItem.setChecked(1);
        cartItem.setPrice(sku.getPrice());
        try {
            boolean saved = cartItemMapper.insert(cartItem) > 0;
            ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "cart add failed");
        } catch (DuplicateKeyException duplicateKeyException) {
            CartItem duplicatedItem = cartItemMapper.selectAnyByCartIdAndSkuId(cart.getId(), sku.getId());
            ThrowUtils.throwIf(duplicatedItem == null, ErrorCode.OPERATION_ERROR, "cart add failed");
            if (Objects.equals(duplicatedItem.getIsDelete(), 1)) {
                boolean restored = cartItemMapper.restoreCartItem(
                        duplicatedItem.getId(),
                        product.getId(),
                        userId,
                        requestQuantity,
                        1,
                        sku.getPrice()
                ) > 0;
                ThrowUtils.throwIf(!restored, ErrorCode.OPERATION_ERROR, "cart restore failed");
                return true;
            }
            long targetQuantity = (long) duplicatedItem.getQuantity() + requestQuantity;
            ThrowUtils.throwIf(targetQuantity > stock, ErrorCode.OPERATION_ERROR, "insufficient stock");
            duplicatedItem.setQuantity((int) targetQuantity);
            duplicatedItem.setChecked(1);
            duplicatedItem.setPrice(sku.getPrice());
            boolean updated = cartItemMapper.updateById(duplicatedItem) > 0;
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "cart update failed");
        }
        return true;
    }

    @Override
    public CartVO getMyCart(Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        Cart cart = this.getOne(Wrappers.lambdaQuery(Cart.class)
                .eq(Cart::getUserId, userId)
                .last("limit 1"));

        List<CartItemRowVO> rows = cartItemMapper.getMyCartItems(userId);
        if (rows == null || rows.isEmpty()) {
            CartVO emptyCart = new CartVO();
            emptyCart.setCartId(cart == null ? null : cart.getId());
            emptyCart.setItemList(Collections.emptyList());
            emptyCart.setCheckedAmount(BigDecimal.ZERO);
            emptyCart.setCheckedCount(0);
            emptyCart.setTotalCount(0);
            return emptyCart;
        }

        List<CartItemVO> itemList = rows.stream().map(row -> {
            CartItemVO itemVO = new CartItemVO();
            BeanUtils.copyProperties(row, itemVO);
            return itemVO;
        }).toList();

        CartVO cartVO = new CartVO();
        cartVO.setCartId(rows.get(0).getCartId());
        cartVO.setItemList(itemList);
        cartVO.setCheckedAmount(calculateCheckedAmount(itemList));
        cartVO.setCheckedCount(sumQuantity(itemList, true));
        cartVO.setTotalCount(sumQuantity(itemList, false));
        return cartVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCartItemQuantity(Long userId, UpdateCartItemQuantityRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(request == null
                        || request.getId() == null
                        || request.getId() <= 0
                        || request.getQuantity() == null
                        || request.getQuantity() <= 0,
                ErrorCode.PARAMS_ERROR);

        CartItem cartItem = getOwnedCartItem(userId, request.getId());
        ProductSkus sku = productSkusService.getById(cartItem.getSkuId());
        ThrowUtils.throwIf(sku == null, ErrorCode.NOT_FOUND_ERROR, "sku not found");
        int stock = sku.getStock() == null ? 0 : sku.getStock();
        ThrowUtils.throwIf(request.getQuantity() > stock, ErrorCode.OPERATION_ERROR, "insufficient stock");

        cartItem.setQuantity(request.getQuantity());
        boolean updated = cartItemMapper.updateById(cartItem) > 0;
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "cart quantity update failed");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCartItemChecked(Long userId, UpdateCartItemCheckedRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(request == null
                        || request.getId() == null
                        || request.getId() <= 0
                        || request.getChecked() == null
                        || (request.getChecked() != 0 && request.getChecked() != 1),
                ErrorCode.PARAMS_ERROR);

        CartItem cartItem = getOwnedCartItem(userId, request.getId());
        cartItem.setChecked(request.getChecked());
        boolean updated = cartItemMapper.updateById(cartItem) > 0;
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "cart checked update failed");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCartItem(Long userId, Long cartItemId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(cartItemId == null || cartItemId <= 0, ErrorCode.PARAMS_ERROR);
        getOwnedCartItem(userId, cartItemId);
        boolean removed = cartItemMapper.deleteById(cartItemId) > 0;
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "cart item delete failed");
        return true;
    }

    private Cart getOrCreateCart(Long userId) {
        Cart cart = this.getOne(Wrappers.lambdaQuery(Cart.class)
                .eq(Cart::getUserId, userId)
                .last("limit 1"));
        if (cart != null) {
            return cart;
        }

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        boolean saved = this.save(newCart);
        ThrowUtils.throwIf(!saved || newCart.getId() == null, ErrorCode.OPERATION_ERROR, "cart create failed");
        return newCart;
    }

    private CartItem getOwnedCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemMapper.selectOne(Wrappers.lambdaQuery(CartItem.class)
                .eq(CartItem::getId, cartItemId)
                .eq(CartItem::getUserId, userId)
                .last("limit 1"));
        ThrowUtils.throwIf(cartItem == null, ErrorCode.NOT_FOUND_ERROR, "cart item not found");
        return cartItem;
    }

    private BigDecimal calculateCheckedAmount(List<CartItemVO> itemList) {
        BigDecimal amount = BigDecimal.ZERO;
        for (CartItemVO item : itemList) {
            if (!Objects.equals(item.getChecked(), 1)) {
                continue;
            }
            BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            amount = amount.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return amount;
    }

    private Integer sumQuantity(List<CartItemVO> itemList, boolean onlyChecked) {
        int count = 0;
        for (CartItemVO item : itemList) {
            if (onlyChecked && !Objects.equals(item.getChecked(), 1)) {
                continue;
            }
            count += item.getQuantity() == null ? 0 : item.getQuantity();
        }
        return count;
    }
}
