package com.watch.watch_mall.mapper;

import com.watch.watch_mall.model.entity.AttributeValues;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watch.watch_mall.model.vo.AttributesVO;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Ginger
* @description 针对表【attribute_values】的数据库操作Mapper
* @createDate 2026-02-20 13:04:58
* @Entity com.watch.watch_mall.model.entity.AttributeValues
*/
public interface AttributeValuesMapper extends BaseMapper<AttributeValues> {
    @Select("""
SELECT
  a.id   AS atrId,
  a.name AS atrName,
  JSON_ARRAYAGG(JSON_OBJECT('atrvId', av.id, 'atrvValue', av.value)) AS atrValue
FROM attributes a
JOIN attribute_values av ON av.attributeId = a.id
GROUP BY a.id, a.name
ORDER BY a.id
""")
    @Results({
            @Result(column = "atrId", property = "atrId"),
            @Result(column = "atrName", property = "atrName"),
            @Result(
                    column = "atrValue",
                    property = "atrValue",
                    typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class
            )
    })
    List<AttributesVO> listAttrValue();
}




