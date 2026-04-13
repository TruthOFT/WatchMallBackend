package com.watch.watch_mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("support_message")
public class SupportMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long ticketId;

    private Long senderId;

    private String senderRole;

    private String senderName;

    private String messageType;

    private String content;

    private Integer isAi;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
