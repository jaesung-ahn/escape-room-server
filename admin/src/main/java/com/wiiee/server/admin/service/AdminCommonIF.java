package com.wiiee.server.admin.service;


import com.wiiee.server.admin.domain.AdminVO;
import com.wiiee.server.admin.domain.LoginDTO;

public interface AdminCommonIF {

    // 로그인 처리
    AdminVO login(LoginDTO loginDTO) throws Exception;

}
