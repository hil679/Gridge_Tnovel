package com.example.demo.src.user.model;

import com.example.demo.common.Constant;
import com.example.demo.src.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private String email;
    private String password;
    private String phoneNumber;
    private String idNickname;
    private String name;
    private Constant.SocialLoginType loginType;

    private boolean isOAuth;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .idNickname(this.idNickname)
                .phoneNumber(this.phoneNumber)
                .loginType(this.loginType)
                .isOAuth(this.isOAuth)
                .build();
    }
}
