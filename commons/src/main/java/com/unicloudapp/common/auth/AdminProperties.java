package com.unicloudapp.common.auth;

import com.unicloudapp.common.vo.user.UserLogin;

import java.util.List;

public interface AdminProperties {

    List<UserLogin> getAdmins();
}
