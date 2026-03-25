package com.watch.watch_mall.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AttributeAdminDetailVO implements Serializable {

    private Long id;

    private String name;

    private Long categoryId;

    @TableField(value = "valueList", typeHandler = JacksonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private List<String> valueList;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
