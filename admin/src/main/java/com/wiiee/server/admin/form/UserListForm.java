package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.user.MemberType;
import com.wiiee.server.common.domain.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserListForm extends DefaultForm {

    private String email;
    private String nickname;
    private MemberType memberType;
    private UserStatus userStatus;
}
