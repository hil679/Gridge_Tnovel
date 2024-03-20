package com.example.demo.src.agreement.entity;

import com.example.demo.common.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "AGREEMENT")
public class Agreement extends BaseEntity { // 선택 동의 확장성 고려하여 entity 따로 분리
    @Id // PK를 의미하는 어노테이션
    @Column(name = "agreement_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;

    @Column(nullable = false)
    private boolean essentialPolicy;

    public Agreement(boolean essentialAgreement) {
        this.essentialPolicy = essentialPolicy;
    }

    public void updateEssentialPolicy(boolean essentialPolicy) {
        this.essentialPolicy = essentialPolicy;
    }
}
