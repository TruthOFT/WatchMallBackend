package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class SupportMessageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long ticketId;

    private Long senderId;

    private String senderRole;

    private String senderName;

    private String messageType;

    private String content;

    private Integer isAi;

    private Date createTime;
}
