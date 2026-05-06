package com.watch.watch_mall.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Request for updating current user's profile.
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * Display name.
     */
    private String username;

    /**
     * Email address.
     */
    private String email;

    /**
     * Phone number.
     */
    private String phone;

    /**
     * Gender: 0 female, 1 male.
     */
    private Integer gender;

    private static final long serialVersionUID = 1L;
}
