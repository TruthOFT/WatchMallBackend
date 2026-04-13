package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.model.dto.support.SupportAdminQueryRequest;
import com.watch.watch_mall.model.dto.support.SupportAiChatRequest;
import com.watch.watch_mall.model.dto.support.SupportMessageSendRequest;
import com.watch.watch_mall.model.dto.support.SupportTicketCreateRequest;
import com.watch.watch_mall.model.dto.support.SupportTicketStatusUpdateRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.SupportAdminPageVO;
import com.watch.watch_mall.model.vo.SupportAiChatVO;
import com.watch.watch_mall.model.vo.SupportMessageVO;
import com.watch.watch_mall.model.vo.SupportTicketDetailVO;
import com.watch.watch_mall.model.vo.SupportTicketVO;

import java.util.List;

public interface SupportService {

    SupportTicketVO createTicket(User loginUser, SupportTicketCreateRequest request);

    List<SupportTicketVO> listMyTickets(User loginUser);

    SupportTicketDetailVO getUserTicketDetail(User loginUser, Long ticketId);

    List<SupportMessageVO> listUserMessages(User loginUser, Long ticketId);

    SupportMessageVO sendUserMessage(User loginUser, SupportMessageSendRequest request);

    SupportAiChatVO chatWithAi(User loginUser, SupportAiChatRequest request);

    Page<SupportAdminPageVO> pageAdminTickets(SupportAdminQueryRequest request);

    SupportTicketDetailVO getAdminTicketDetail(Long ticketId);

    SupportMessageVO replyByAdmin(User adminUser, SupportMessageSendRequest request);

    boolean updateTicketStatusByAdmin(SupportTicketStatusUpdateRequest request);
}
