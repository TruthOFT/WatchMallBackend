package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class SupportAdminPageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
}
