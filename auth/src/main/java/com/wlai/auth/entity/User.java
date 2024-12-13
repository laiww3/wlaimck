package com.wlai.auth.entity;

import com.wlai.common.entity.BaseEntity;
import lombok.Data;

/**
 * @Author：laiwenwen
 * @Date：2024/12/13 20:26
 */

@Data
public class User extends BaseEntity {

    private long id;

    private String userName;

    private String password;

    private String avatar;
}
