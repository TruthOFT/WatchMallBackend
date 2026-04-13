package com.watch.watch_mall.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class SupportTicketDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SupportTicketVO ticket;

    private List<SupportMessageVO> messageList;
}
