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
public class KakaoUser {
    private String name;
    private String email;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password("NONE")
                .name(this.name)
                .isOAuth(true)
                .loginType(Constant.SocialLoginType.KAKAO)
                .build();
    }
}
