package com.example.demo.common;

import lombok.Getter;

public class Constant {
    public enum SocialLoginType{
        NONE,
        GOOGLE,
        KAKAO,
        NAVER
    }

    @Getter
    public enum BirthDayLimit{
        YEAR_MIN(2016),
        YEAR_MAX(2021),
        MONTH_MIN(1),
        MONTH_MAX(12),
        DAY_MIN(1),
        DAY_MAX(31),
        FEB_DAY_MAX(29);

        private int value;

        private BirthDayLimit(int value) {
            this.value = value;
        }
    }
}

