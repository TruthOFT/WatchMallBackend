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
@TableName("support_ticket")
public class SupportTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String userName;

    private String userAccount;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String subject;

    private String latestMessage;

    private String status;

    private String source;

    private Date lastMessageTime;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
