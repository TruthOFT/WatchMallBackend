package com.watch.watch_mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.entity.UserRecommendation;
import org.apache.ibatis.annotations.Delete;

public interface UserRecommendationMapper extends BaseMapper<UserRecommendation> {

    @Delete("delete from user_recommendation")
    int deleteAllRows();
}
