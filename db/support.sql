create table if not exists support_ticket
(
    id bigint not null comment '主键',
    userId bigint not null comment '用户 id',
    userName varchar(128) null comment '用户昵称',
    userAccount varchar(128) null comment '用户账号',
    contactName varchar(128) null comment '联系人',
    contactPhone varchar(64) null comment '联系电话',
    contactEmail varchar(128) null comment '联系邮箱',
    subject varchar(128) not null comment '反馈主题',
    latestMessage varchar(1000) null comment '最新消息摘要',
    status varchar(32) not null default 'pending' comment '状态 pending/replied/closed',
    source varchar(32) not null default 'manual' comment '来源 manual/ai',
    lastMessageTime datetime null comment '最后消息时间',
    createTime datetime null default CURRENT_TIMESTAMP,
    updateTime datetime null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    isDelete tinyint(1) null default 0,
    primary key (id),
    index idx_support_ticket_userId (userId),
    index idx_support_ticket_status (status),
    index idx_support_ticket_lastMessageTime (lastMessageTime)
) comment '用户支持工单';

create table if not exists support_message
(
    id bigint not null comment '主键',
    ticketId bigint not null comment '工单 id',
    senderId bigint null comment '发送人 id',
    senderRole varchar(32) not null comment '发送人角色 user/admin/ai',
    senderName varchar(128) null comment '发送人名称',
    messageType varchar(32) not null default 'text' comment '消息类型',
    content text not null comment '消息内容',
    isAi tinyint(1) not null default 0 comment '是否 ai 消息',
    createTime datetime null default CURRENT_TIMESTAMP,
    updateTime datetime null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    isDelete tinyint(1) null default 0,
    primary key (id),
    index idx_support_message_ticketId (ticketId),
    index idx_support_message_senderRole (senderRole)
) comment '支持聊天消息';
