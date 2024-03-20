package com.example.demo.src.user.entity;

import com.example.demo.common.Constant;
import com.example.demo.common.entity.BaseEntity;
import com.example.demo.src.agreement.entity.Agreement;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Getter
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "USER") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class User extends BaseEntity {

    @Id // PK를 의미하는 어노테이션
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String idNickname;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isOAuth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Constant.SocialLoginType loginType;

    @Column(nullable = false)
    private LocalDate registerDate;

    @Column
    private LocalDate birthday;

    @OneToOne
    @JoinColumn(name = "agreementId")
    private Agreement agreement;

    @Builder
    public User(Long id, String email, String password, String name, String idNickname, String phoneNumber, Constant.SocialLoginType loginType, boolean isOAuth) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.idNickname = idNickname;
        this.phoneNumber = phoneNumber;
        this.loginType = loginType;
        this.isOAuth = isOAuth;
        this.registerDate = LocalDate.now();
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateBirthDay(String birthday) {
        this.birthday = LocalDate.parse(birthday, DateTimeFormatter.ISO_DATE); //"ex) 2024-03-11"
    }
    public void updateAgreement(Agreement essentialAgreement) {
        this.agreement = essentialAgreement;
    }

    public void deleteUser() {
        this.state = State.INACTIVE;
    }

}
