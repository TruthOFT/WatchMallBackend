package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.CartItem;
import com.watch.watch_mall.model.vo.CartItemRowVO;
import com.watch.watch_mall.model.vo.ProductSkuAttributeRowVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CartItemMapper extends BaseMapper<CartItem> {

    List<CartItemRowVO> getMyCartItems(@Param("userId") Long userId);

    List<ProductSkuAttributeRowVO> getCartSkuAttributeRows(@Param("userId") Long userId);

    CartItem selectAnyByCartIdAndSkuId(@Param("cartId") Long cartId, @Param("skuId") Long skuId);

    int restoreCartItem(@Param("id") Long id,
                        @Param("productId") Long productId,
                        @Param("userId") Long userId,
                        @Param("quantity") Integer quantity,
                        @Param("checked") Integer checked,
                        @Param("price") BigDecimal price);
}
