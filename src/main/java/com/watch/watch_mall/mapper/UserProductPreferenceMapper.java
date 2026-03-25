package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.UserProductPreference;
import org.apache.ibatis.annotations.Delete;

public interface UserProductPreferenceMapper extends BaseMapper<UserProductPreference> {

    @Delete("delete from user_product_preference")
    int deleteAllRows();
}
