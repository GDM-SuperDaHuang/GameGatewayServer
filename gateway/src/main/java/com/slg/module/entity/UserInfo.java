package com.slg.module.entity;

import jakarta.persistence.*;

@Entity
@Table
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint(20) not null comment '用户唯一标识'")
    private Long userId;

    @Column(columnDefinition = "varchar(50) unique comment '用户名'")
    private String username;

    @Column(columnDefinition = "varchar(50) unique comment '用户密码'")
    private String password;

    @Column(columnDefinition = "varchar(50) unique comment '用户邮箱'")
    private String email;

    @Column(columnDefinition = "varchar(50) unique comment '第三方ID'")
    private long thirdPartyId;

    @Column(columnDefinition = "varchar(50) unique comment '第三方类型'")
    private long ThirdPartyType;

    @Column(columnDefinition = "varchar(50) unique comment '会话管理'")
    private String token;

    @Column(columnDefinition = "bigint(20) not null comment '创建时间'")
    private long createdTime;

    @Column(columnDefinition = "bigint(20) not null comment '最后登录时间戳'")
    private long lastLogin;

    @Column(columnDefinition = "bigint(20) not null comment '最后登出时间戳'")
    private long lastLoginOut;

    @Column(columnDefinition = "bigint(20) not null comment '账号状态'")
    private int status;

    @Column(nullable = false, unique = true)
    private String ip;





}

