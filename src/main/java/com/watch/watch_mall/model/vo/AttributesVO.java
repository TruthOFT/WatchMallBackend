package com.watch.watch_mall.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributesVO implements Serializable {

    private Long atrId;

    private String atrName;
    @TableField(value = "atrValue", typeHandler = JacksonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private List<AttributeValueVO> atrValue;

    @Data
    public static class AttributeValueVO implements Serializable {
        private Long atrvId;

        private String atrvValue;
    }
}