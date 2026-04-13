package com.watch.watch_mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.mapper.SupportMessageMapper;
import com.watch.watch_mall.mapper.SupportTicketMapper;
import com.watch.watch_mall.model.dto.support.SupportAdminQueryRequest;
import com.watch.watch_mall.model.dto.support.SupportAiChatRequest;
import com.watch.watch_mall.model.dto.support.SupportMessageSendRequest;
import com.watch.watch_mall.model.dto.support.SupportTicketCreateRequest;
import com.watch.watch_mall.model.dto.support.SupportTicketStatusUpdateRequest;
import com.watch.watch_mall.model.entity.SupportMessage;
import com.watch.watch_mall.model.entity.SupportTicket;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.SupportAdminPageVO;
import com.watch.watch_mall.model.vo.SupportAiChatVO;
import com.watch.watch_mall.model.vo.SupportMessageVO;
import com.watch.watch_mall.model.vo.SupportTicketDetailVO;
import com.watch.watch_mall.model.vo.SupportTicketVO;
import com.watch.watch_mall.service.SupportAiService;
import com.watch.watch_mall.service.SupportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SupportServiceImpl implements SupportService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_REPLIED = "replied";
    private static final String STATUS_CLOSED = "closed";
    private static final String SOURCE_MANUAL = "manual";
    private static final String SOURCE_AI = "ai";
    private static final String ROLE_USER = "user";
    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_AI = "ai";

    private final SupportTicketMapper supportTicketMapper;
    private final SupportMessageMapper supportMessageMapper;
    private final SupportAiService supportAiService;

    public SupportServiceImpl(SupportTicketMapper supportTicketMapper,
                              SupportMessageMapper supportMessageMapper,
                              SupportAiService supportAiService) {
        this.supportTicketMapper = supportTicketMapper;
        this.supportMessageMapper = supportMessageMapper;
        this.supportAiService = supportAiService;
    }

    @Override
    public SupportTicketVO createTicket(User loginUser, SupportTicketCreateRequest request) {
        validateLoginUser(loginUser);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String subject = StringUtils.trimToEmpty(request.getSubject());
        String content = StringUtils.trimToEmpty(request.getContent());
        if (StringUtils.isBlank(subject) || StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "反馈主题和内容不能为空");
        }
        SupportTicket supportTicket = new SupportTicket();
        supportTicket.setUserId(loginUser.getId());
        supportTicket.setUserName(resolveUserName(loginUser));
        supportTicket.setUserAccount(loginUser.getUserAccount());
        supportTicket.setContactName(StringUtils.defaultIfBlank(StringUtils.trimToNull(request.getContactName()), resolveUserName(loginUser)));
        supportTicket.setContactPhone(StringUtils.defaultIfBlank(StringUtils.trimToNull(request.getContactPhone()), loginUser.getPhone()));
        supportTicket.setContactEmail(StringUtils.defaultIfBlank(StringUtils.trimToNull(request.getContactEmail()), loginUser.getEmail()));
        supportTicket.setSubject(subject);
        supportTicket.setLatestMessage(content);
        supportTicket.setStatus(STATUS_PENDING);
        supportTicket.setSource(SOURCE_MANUAL);
        supportTicket.setLastMessageTime(new Date());
        supportTicketMapper.insert(supportTicket);
        saveMessage(supportTicket.getId(), loginUser.getId(), ROLE_USER, resolveUserName(loginUser), content, 0);
        return toTicketVO(supportTicket);
    }

    @Override
    public List<SupportTicketVO> listMyTickets(User loginUser) {
        validateLoginUser(loginUser);
        return supportTicketMapper.selectList(new LambdaQueryWrapper<SupportTicket>()
                        .eq(SupportTicket::getUserId, loginUser.getId())
                        .eq(SupportTicket::getIsDelete, 0)
                        .orderByDesc(SupportTicket::getLastMessageTime)
                        .orderByDesc(SupportTicket::getCreateTime))
                .stream()
                .map(this::toTicketVO)
                .toList();
    }

    @Override
    public SupportTicketDetailVO getUserTicketDetail(User loginUser, Long ticketId) {
        SupportTicket supportTicket = getOwnedTicket(loginUser, ticketId);
        return toTicketDetailVO(supportTicket);
    }

    @Override
    public List<SupportMessageVO> listUserMessages(User loginUser, Long ticketId) {
        SupportTicket supportTicket = getOwnedTicket(loginUser, ticketId);
        return listMessageVO(supportTicket.getId());
    }

    @Override
    public SupportMessageVO sendUserMessage(User loginUser, SupportMessageSendRequest request) {
        validateLoginUser(loginUser);
        if (request == null || request.getTicketId() == null || request.getTicketId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "工单不存在");
        }
        String content = StringUtils.trimToEmpty(request.getContent());
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        }
        SupportTicket supportTicket = getOwnedTicket(loginUser, request.getTicketId());
        SupportMessage supportMessage = saveMessage(supportTicket.getId(), loginUser.getId(), ROLE_USER, resolveUserName(loginUser), content, 0);
        updateTicketAfterMessage(supportTicket, content, STATUS_PENDING);
        return toMessageVO(supportMessage);
    }

    @Override
    public SupportAiChatVO chatWithAi(User loginUser, SupportAiChatRequest request) {
        validateLoginUser(loginUser);
        if (request == null || StringUtils.isBlank(request.getMessage())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        }
        SupportTicket supportTicket;
        if (request.getTicketId() == null || request.getTicketId() <= 0) {
            SupportTicketCreateRequest createRequest = new SupportTicketCreateRequest();
            createRequest.setSubject("AI 客服咨询");
            createRequest.setContent(request.getMessage());
            createRequest.setContactName(resolveUserName(loginUser));
            createRequest.setContactPhone(loginUser.getPhone());
            createRequest.setContactEmail(loginUser.getEmail());
            SupportTicketVO supportTicketVO = createTicket(loginUser, createRequest);
            supportTicket = supportTicketMapper.selectById(supportTicketVO.getId());
            supportTicket.setSource(SOURCE_AI);
            supportTicketMapper.updateById(supportTicket);
        } else {
            supportTicket = getOwnedTicket(loginUser, request.getTicketId());
            SupportMessageSendRequest sendRequest = new SupportMessageSendRequest();
            sendRequest.setTicketId(request.getTicketId());
            sendRequest.setContent(request.getMessage());
            sendUserMessage(loginUser, sendRequest);
        }
        String reply = supportAiService.chat(supportTicket.getId(), request.getMessage());
        saveMessage(supportTicket.getId(), 0L, ROLE_AI, "AI 客服", reply, 1);
        updateTicketAfterMessage(supportTicket, reply, STATUS_REPLIED);
        SupportAiChatVO supportAiChatVO = new SupportAiChatVO();
        supportAiChatVO.setTicketId(supportTicket.getId());
        supportAiChatVO.setReply(reply);
        return supportAiChatVO;
    }

    @Override
    public Page<SupportAdminPageVO> pageAdminTickets(SupportAdminQueryRequest request) {
        SupportAdminQueryRequest queryRequest = request == null ? new SupportAdminQueryRequest() : request;
        String keyword = StringUtils.trimToNull(queryRequest.getKeyword());
        String status = StringUtils.trimToNull(queryRequest.getStatus());
        String source = StringUtils.trimToNull(queryRequest.getSource());
        Page<SupportTicket> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
        Page<SupportTicket> ticketPage = supportTicketMapper.selectPage(page, new LambdaQueryWrapper<SupportTicket>()
                .and(keyword != null, wrapper -> wrapper.like(SupportTicket::getSubject, keyword)
                        .or().like(SupportTicket::getContactName, keyword)
                        .or().like(SupportTicket::getContactEmail, keyword)
                        .or().like(SupportTicket::getUserAccount, keyword))
                .eq(status != null, SupportTicket::getStatus, status)
                .eq(source != null, SupportTicket::getSource, source)
                .eq(SupportTicket::getIsDelete, 0)
                .orderByDesc(SupportTicket::getLastMessageTime)
                .orderByDesc(SupportTicket::getCreateTime));
        Page<SupportAdminPageVO> resultPage = new Page<>(ticketPage.getCurrent(), ticketPage.getSize(), ticketPage.getTotal());
        resultPage.setRecords(ticketPage.getRecords().stream().map(this::toAdminPageVO).toList());
        return resultPage;
    }

    @Override
    public SupportTicketDetailVO getAdminTicketDetail(Long ticketId) {
        SupportTicket supportTicket = getExistingTicket(ticketId);
        return toTicketDetailVO(supportTicket);
    }

    @Override
    public SupportMessageVO replyByAdmin(User adminUser, SupportMessageSendRequest request) {
        validateAdminUser(adminUser);
        if (request == null || request.getTicketId() == null || request.getTicketId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "工单不存在");
        }
        String content = StringUtils.trimToEmpty(request.getContent());
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容不能为空");
        }
        SupportTicket supportTicket = getExistingTicket(request.getTicketId());
        SupportMessage supportMessage = saveMessage(supportTicket.getId(), adminUser.getId(), ROLE_ADMIN, resolveUserName(adminUser), content, 0);
        updateTicketAfterMessage(supportTicket, content, STATUS_REPLIED);
        return toMessageVO(supportMessage);
    }

    @Override
    public boolean updateTicketStatusByAdmin(SupportTicketStatusUpdateRequest request) {
        if (request == null || request.getTicketId() == null || request.getTicketId() <= 0 || StringUtils.isBlank(request.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String status = StringUtils.trim(request.getStatus());
        if (!List.of(STATUS_PENDING, STATUS_REPLIED, STATUS_CLOSED).contains(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "工单状态不合法");
        }
        SupportTicket supportTicket = getExistingTicket(request.getTicketId());
        supportTicket.setStatus(status);
        return supportTicketMapper.updateById(supportTicket) > 0;
    }

    private SupportTicket getOwnedTicket(User loginUser, Long ticketId) {
        validateLoginUser(loginUser);
        SupportTicket supportTicket = getExistingTicket(ticketId);
        if (!supportTicket.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return supportTicket;
    }

    private SupportTicket getExistingTicket(Long ticketId) {
        if (ticketId == null || ticketId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SupportTicket supportTicket = supportTicketMapper.selectOne(new LambdaQueryWrapper<SupportTicket>()
                .eq(SupportTicket::getId, ticketId)
                .eq(SupportTicket::getIsDelete, 0)
                .last("limit 1"));
        if (supportTicket == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "工单不存在");
        }
        return supportTicket;
    }

    private void validateLoginUser(User loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
    }

    private void validateAdminUser(User adminUser) {
        validateLoginUser(adminUser);
        if (!ROLE_ADMIN.equalsIgnoreCase(StringUtils.trimToEmpty(adminUser.getUserRole()))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    private String resolveUserName(User user) {
        return StringUtils.defaultIfBlank(StringUtils.trimToNull(user.getUsername()), StringUtils.defaultIfBlank(user.getUserAccount(), "用户"));
    }

    private SupportMessage saveMessage(Long ticketId, Long senderId, String senderRole, String senderName, String content, int isAi) {
        SupportMessage supportMessage = new SupportMessage();
        supportMessage.setTicketId(ticketId);
        supportMessage.setSenderId(senderId);
        supportMessage.setSenderRole(senderRole);
        supportMessage.setSenderName(senderName);
        supportMessage.setMessageType("text");
        supportMessage.setContent(content);
        supportMessage.setIsAi(isAi);
        supportMessageMapper.insert(supportMessage);
        return supportMessage;
    }

    private void updateTicketAfterMessage(SupportTicket supportTicket, String latestMessage, String status) {
        supportTicket.setLatestMessage(latestMessage);
        supportTicket.setLastMessageTime(new Date());
        supportTicket.setStatus(status);
        supportTicketMapper.updateById(supportTicket);
    }

    private List<SupportMessageVO> listMessageVO(Long ticketId) {
        return supportMessageMapper.selectList(new LambdaQueryWrapper<SupportMessage>()
                        .eq(SupportMessage::getTicketId, ticketId)
                        .eq(SupportMessage::getIsDelete, 0)
                        .orderByAsc(SupportMessage::getCreateTime)
                        .orderByAsc(SupportMessage::getId))
                .stream()
                .map(this::toMessageVO)
                .toList();
    }

    private SupportTicketDetailVO toTicketDetailVO(SupportTicket supportTicket) {
        SupportTicketDetailVO detailVO = new SupportTicketDetailVO();
        detailVO.setTicket(toTicketVO(supportTicket));
        detailVO.setMessageList(listMessageVO(supportTicket.getId()));
        return detailVO;
    }

    private SupportTicketVO toTicketVO(SupportTicket supportTicket) {
        SupportTicketVO supportTicketVO = new SupportTicketVO();
        BeanUtils.copyProperties(supportTicket, supportTicketVO);
        return supportTicketVO;
    }

    private SupportMessageVO toMessageVO(SupportMessage supportMessage) {
        SupportMessageVO supportMessageVO = new SupportMessageVO();
        BeanUtils.copyProperties(supportMessage, supportMessageVO);
        return supportMessageVO;
    }

    private SupportAdminPageVO toAdminPageVO(SupportTicket supportTicket) {
        SupportAdminPageVO supportAdminPageVO = new SupportAdminPageVO();
        BeanUtils.copyProperties(supportTicket, supportAdminPageVO);
        return supportAdminPageVO;
    }
}
