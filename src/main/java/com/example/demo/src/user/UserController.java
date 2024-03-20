package com.example.demo.src.user;


import com.example.demo.common.Constant.SocialLoginType;
import com.example.demo.common.oauth.OAuthService;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import com.example.demo.common.exceptions.BaseException;
import com.example.demo.common.response.BaseResponse;
import com.example.demo.src.user.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


import static com.example.demo.common.Constant.BirthDayLimit.FEB_DAY_MAX;
import static com.example.demo.common.Constant.BirthDayLimit.YEAR_MIN;
import static com.example.demo.common.response.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;
import static com.example.demo.src.user.model.PatchUserReq.PatchUserAgreementReq;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {


    private final UserService userService;

    private final OAuthService oAuthService;

    private final JwtService jwtService;


    /**
     * 회원가입 API
     * [POST] /app/users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        int MAX_LENGTH = 20;
        int MIN_LENGTH_PW = 6;
        String email = postUserReq.getEmail();
        String password = postUserReq.getPassword();
        String phoneNumebr = postUserReq.getPhoneNumber();
        String name = postUserReq.getName();
        String idNickname = postUserReq.getIdNickname();

        if(email == null){
            return new BaseResponse<>(USERS_EMPTY_EMAIL);
        } else if (!isRegexEmail(email)){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //phonenumber validation
        if(phoneNumebr == null){
            return new BaseResponse<>(USERS_EMPTY_PHONE_NUMBER);
        } else if (isNotRegexPhoneNumber(phoneNumebr)) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        } else if (phoneNumebr.length() > MAX_LENGTH){
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_PHONE_NUMBER);
        }

        //name validation
        if(name == null){
            return new BaseResponse<>(USERS_EMPTY_NAME);
        } else if(name.length() > MAX_LENGTH) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NAME);
        }

        //id(nickname) validation
        if(idNickname == null){
            return new BaseResponse<>(USERS_EMPTY_ID_NICKNAME);
        } else if(idNickname.length() > MAX_LENGTH) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_ID_NICKNAME);
        } else if(isNotRegexIdNickname(idNickname)) {
            return new BaseResponse<>(POST_USERS_INVALID_ID_NICKNAME);
        }

        //pw validation
        if(password == null){
            return new BaseResponse<>(USERS_EMPTY_PASSWORD);
        } else if(password.length() > MAX_LENGTH && password.length() < MIN_LENGTH_PW) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        // check duplicate
        if (userService.checkUserByEmail(email)) {
            return new BaseResponse<>(DUPLICATED_EMAIL);
        } else if (userService.checkUserByPhoneNumber(phoneNumebr)) {
            return new BaseResponse<>(DUPLICATED_PHONE_NUMBER);
        } else if (userService.checkUserByIdNickname(idNickname)) {
            return new BaseResponse<>(DUPLICATED_ID_NICKNAME);
        }

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return new BaseResponse<>(postUserRes);
    }

    /**
    * 실시간으로 전화번호 타당성 검사 위한 api
     */
    @PostMapping("validate/phone-number/{phoneNumber}")
    public BaseResponse<String> validatePhoneNumber(@PathVariable String phoneNumber){
        if (userService.checkUserByPhoneNumber(phoneNumber)) {
            return new BaseResponse<>(DUPLICATED_PHONE_NUMBER);
        } else if (isNotRegexPhoneNumber(phoneNumber)) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }
        return new BaseResponse<>(VALID_PHONE_NUMBER);
    }

    /**
     * 실시간으로 사용자 이름 타당성 검사 위한 api
     */
    @PostMapping("validate/id-nickname")
    public BaseResponse<String> validateIdNickname(@RequestParam String idNickname){
        System.out.println(idNickname);
        if (userService.checkUserByIdNickname(idNickname)) {
            return new BaseResponse<>(DUPLICATED_ID_NICKNAME);
        } else if (isNotRegexIdNickname(idNickname)) {
            return new BaseResponse<>(POST_USERS_INVALID_ID_NICKNAME);
        }
        return new BaseResponse<>(VALID_ID_NICKNAME);
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /app/users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        if(Email == null){
            List<GetUserRes> getUsersRes = userService.getUsers();
            return new BaseResponse<>(getUsersRes);
        }
        // Get Users
        List<GetUserRes> getUsersRes = userService.getUsersByEmail(Email);
        return new BaseResponse<>(getUsersRes);
    }

    /**
     * 회원 1명 조회 API
     * [GET] /app/users/:userId
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userId}") // (GET) 127.0.0.1:9000/app/users/:userId
    public BaseResponse<GetUserRes> getUser(@PathVariable("userId") Long userId) {
        GetUserRes getUserRes = userService.getUser(userId);
        return new BaseResponse<>(getUserRes);
    }



    /**
     * 유저정보변경 API
     * [PATCH] /app/users/:userId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") Long userId, @RequestBody PatchUserReq.PatchUserNameReq patchUserNameReq){

        Long jwtUserId = jwtService.getUserId();

        userService.modifyUserName(userId, patchUserNameReq);

        String result = "수정 완료!!";
        return new BaseResponse<>(result);

    }

    /**
     * password변경 API
     * [PATCH] /app/users/pw/:userId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/pw/{userId}")
    public BaseResponse<String> modifyPassword(@PathVariable("userId") Long userId, @RequestBody PatchUserReq.PatchUserPasswordReq patchUserPasswordReq){
        //기존 pw 입력하고 맞는지 체크

        //새 pw 받기
        String result = "수정 완료!!";
        return new BaseResponse<>(result);
    }

    /**
     * 유저정보삭제 API
     * [DELETE] /app/users/:userId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{userId}")
    public BaseResponse<String> deleteUser(@PathVariable("userId") Long userId){
        Long jwtUserId = jwtService.getUserId();

        userService.deleteUser(userId);

        String result = "삭제 완료!!";
        return new BaseResponse<>(result);
    }

    /**
     * 로그인 API
     * [POST] /app/users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
        // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
        //id, pw validation
        PostLoginRes postLoginRes = userService.logIn(postLoginReq);
        return new BaseResponse<>(postLoginRes);
    }


    /**
     * 유저 소셜 가입, 로그인 인증으로 리다이렉트 해주는 url
     * [GET] /app/users/auth/:socialLoginType/login
     * @return void
     */
    @GetMapping("/auth/{socialLoginType}/login")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String SocialLoginPath) throws IOException {
        SocialLoginType socialLoginType= SocialLoginType.valueOf(SocialLoginPath.toUpperCase());
        oAuthService.accessRequest(socialLoginType);
    }


    /**
     * Social Login API Server 요청에 의한 callback 을 처리
     * @param socialLoginPath (GOOGLE, FACEBOOK, NAVER, KAKAO)
     * @param code API Server 로부터 넘어오는 code
     * @return SNS Login 요청 결과로 받은 Json 형태의 java 객체 (access_token, jwt_token, user_num 등)
     */
    @ResponseBody
    @GetMapping(value = "/auth/{socialLoginType}/login/callback")
    public BaseResponse<GetSocialOAuthRes> socialLoginCallback(
            @PathVariable(name = "socialLoginType") String socialLoginPath,
            @RequestParam(name = "code") String code) throws IOException, BaseException{
        log.info(">> 소셜 로그인 API 서버로부터 받은 code : {}", code);
        SocialLoginType socialLoginType = SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        GetSocialOAuthRes getSocialOAuthRes = oAuthService.oAuthLoginOrJoin(socialLoginType,code);
        return new BaseResponse<>(getSocialOAuthRes);
    }

    /**
     * 비밀번호 문제 시 문자 인증, 로그인 코드(새 password)재전송
     *
     */
    @ResponseBody
    @GetMapping(value = "/find/pw")
    public String getTempLoginPassword(String phoneNumber) {

        return "TempAccessPassword";
    }

    /**
     * 동의 받기
     */
    @ResponseBody
    @PatchMapping(value = "/agree/{userId}")
    public BaseResponse<String> setAgreement(@PathVariable Long userId, @RequestBody PatchUserReq.PatchUserAgreementReq patchUserAgreementReq) {
        if(userService.isNotExistUser(userId)){
            return new BaseResponse<>(NOT_FIND_USER);
        }
        userService.setAgreement(userId, patchUserAgreementReq);

        if(patchUserAgreementReq.isAgreement() == false) {
            userService.deleteUser(userId); // 동의하지 않으면 이용 불가
            return new BaseResponse<>(DISAGREEMENT_SUCCESS);
        }
        return new BaseResponse<>(UPDATE_AGREEMENT_SUCCESS);
    }

    /**
     * 생일 입력
     */
    @ResponseBody
    @PatchMapping(value = "/birthday/{userId}")
    public BaseResponse<String> setBirthday(@PathVariable("userId") Long userId, @RequestBody PatchUserBirthdayReq patchUserBirthdayReq) {
        if(userService.isNotExistActiveUser(userId)){
            return new BaseResponse<>(NOT_FIND_USER);
        }
        String year = patchUserBirthdayReq.getYear();
        String month = patchUserBirthdayReq.getMonth();
        String day = patchUserBirthdayReq.getDay();
        if(invalidateBirthday(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day))) {
            return new BaseResponse<>(INVALID_BIRTHDAY);
        }

        return userService.setBirthday(userId, year, month, day);
    }

    private boolean invalidateBirthday(Integer year, Integer month, Integer day){
        int feb = 2;

        if(YEAR_MIN.getValue() <= year)
            return true;
        if(month == feb && day > FEB_DAY_MAX.getValue())
            return true;
        return false;
    }
}
