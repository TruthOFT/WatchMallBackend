package com.watch.watch_mall.model.dto.user;

import com.watch.watch_mall.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserAdminQueryRequest extends PageRequest implements Serializable {

    private String keyword;

    private String userRole;

    private static final long serialVersionUID = 1L;
}
