package com.watch.watch_mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ResultUtils;
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
import com.watch.watch_mall.service.SupportService;
import com.watch.watch_mall.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportController {

    @Resource
    private SupportService supportService;

    @Resource
    private UserService userService;

    @PostMapping("/ticket/create")
    public BaseResponse<SupportTicketVO> createTicket(@RequestBody SupportTicketCreateRequest createRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.createTicket(loginUser, createRequest));
    }

    @GetMapping("/ticket/my")
    public BaseResponse<List<SupportTicketVO>> listMyTickets(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.listMyTickets(loginUser));
    }

    @GetMapping("/ticket/detail")
    public BaseResponse<SupportTicketDetailVO> getMyTicketDetail(@RequestParam("ticketId") Long ticketId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.getUserTicketDetail(loginUser, ticketId));
    }

    @GetMapping("/message/list")
    public BaseResponse<List<SupportMessageVO>> listMessages(@RequestParam("ticketId") Long ticketId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.listUserMessages(loginUser, ticketId));
    }

    @PostMapping("/message/send")
    public BaseResponse<SupportMessageVO> sendMessage(@RequestBody SupportMessageSendRequest sendRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.sendUserMessage(loginUser, sendRequest));
    }

    @PostMapping("/chat/ai")
    public BaseResponse<SupportAiChatVO> chatWithAi(@RequestBody SupportAiChatRequest aiChatRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.chatWithAi(loginUser, aiChatRequest));
    }

    @PostMapping("/admin/page")
    @AuthCheck(role = "admin")
    public BaseResponse<Page<SupportAdminPageVO>> pageAdminTickets(@RequestBody(required = false) SupportAdminQueryRequest queryRequest) {
        return ResultUtils.success(supportService.pageAdminTickets(queryRequest));
    }

    @GetMapping("/admin/detail")
    @AuthCheck(role = "admin")
    public BaseResponse<SupportTicketDetailVO> getAdminTicketDetail(@RequestParam("ticketId") Long ticketId) {
        return ResultUtils.success(supportService.getAdminTicketDetail(ticketId));
    }

    @PostMapping("/admin/reply")
    @AuthCheck(role = "admin")
    public BaseResponse<SupportMessageVO> replyByAdmin(@RequestBody SupportMessageSendRequest sendRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(supportService.replyByAdmin(loginUser, sendRequest));
    }

    @PostMapping("/admin/status")
    @AuthCheck(role = "admin")
    public BaseResponse<Boolean> updateTicketStatus(@RequestBody SupportTicketStatusUpdateRequest updateRequest) {
        return ResultUtils.success(supportService.updateTicketStatusByAdmin(updateRequest));
    }
}
