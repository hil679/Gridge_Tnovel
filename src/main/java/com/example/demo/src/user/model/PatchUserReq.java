package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class PatchUserReq {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchUserNameReq{
        private String name;
    }
    public static class PatchUserPasswordReq {
        private String existPassword;
        private String newPassword;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchUserAgreementReq {
        private boolean agreement;
    }
}
