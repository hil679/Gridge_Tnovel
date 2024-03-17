package com.example.demo.src.user.entity;

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
    private String userId;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isOAuth;

    @Column(nullable = false)
    private String loginType;

    @Column(nullable = false)
    private LocalDate registerDate;

    @Column(nullable = false)
    private LocalDate birthday;

    @OneToOne
    @JoinColumn(name = "agreementId")
    private Agreement agreement;

    @Builder
    public User(Long id, String email, String password, String name, boolean isOAuth) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isOAuth = isOAuth;
        this.registerDate = LocalDate.now();
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateBirthDay(String birthday) {
        this.birthday = LocalDate.parse(birthday, DateTimeFormatter.ISO_DATE); //"ex) 2024-03-11"
    }

    public void deleteUser() {
        this.state = State.INACTIVE;
    }

}
