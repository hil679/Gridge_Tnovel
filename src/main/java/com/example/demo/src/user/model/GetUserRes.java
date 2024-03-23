package com.example.demo.src.user.model;


import com.example.demo.src.user.entity.User;
import com.example.demo.utils.AES256;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRes {
    private Long id;
    private String email;
    private String name;
    private String idNickname;
    private String phoneNumber;

    public GetUserRes(User user) throws Exception {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = decryptUserInfo(user.getName());
        this.idNickname = user.getIdNickname();
        this.phoneNumber = decryptUserInfo(user.getPhoneNumber());
    }

    private String decryptUserInfo(String userInfo) throws Exception {
        return new AES256().decrypt(userInfo);
    }
}
