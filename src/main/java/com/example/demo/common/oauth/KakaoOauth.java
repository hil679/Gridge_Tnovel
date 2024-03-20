package com.example.demo.common.oauth;

import com.example.demo.src.user.model.KakaoOauthToken;
import com.example.demo.src.user.model.KakaoUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth{
    @Value("${spring.OAuth2.kakao.url}")
    private String KAKAO_SNS_URL;
    @Value("${spring.OAuth2.kakao.client-id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${spring.OAuth2.kakao.client-secret}")
    private String KAKAO_SNS_CLIENT_SECRET;
    @Value("${spring.OAuth2.kakao.callback-login-url}")
    private String KAKAO_SNS_CALLBACK_LOGIN_URL;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri", KAKAO_SNS_CALLBACK_LOGIN_URL);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL = KAKAO_SNS_URL + "?" + parameterString;

        log.info("redirectURL = ", redirectURL);

        return redirectURL;
    }


    public ResponseEntity<String> requestAccessToken(String code) {
        String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", "application/json");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("client_secret", KAKAO_SNS_CLIENT_SECRET);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_LOGIN_URL);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, headers);

        ResponseEntity<String> responseEntity=restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL,
                request,String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK){
            return responseEntity;
        }
        return null;
    }

    public KakaoOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        log.info("response.getBody() = {}", response.getBody());

        KakaoOauthToken kakaoOAuthToken= objectMapper.readValue(response.getBody(),KakaoOauthToken.class);
        return kakaoOAuthToken;

    }

    public ResponseEntity<String> requestUserInfo(KakaoOauthToken oAuthToken) {
        String KAKAO_USERINFO_REQUEST_URL="https://kapi.kakao.com/v2/user/me";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+oAuthToken.getAccess_token());

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);

        log.info("response.getBody() = {}", response.getBody());

        return response;
    }

    public KakaoUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException{
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(userInfoRes.getBody());

        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
        String nickname = properties.getAsJsonObject().get("nickname").getAsString();

        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
        String email = kakaoAccount.getAsJsonObject().get("email").getAsString();

        return new KakaoUser(nickname, email);
    }
}
