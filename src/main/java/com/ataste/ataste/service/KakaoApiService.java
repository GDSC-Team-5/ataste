package com.ataste.ataste.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoApiService {

    @Value("${kakao.id}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

    public KakaoApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRestaurantsFromKakao() {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?query=음식점";

        // 헤더에 API 키 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // HTTP 요청 엔터티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        String result = response.getBody();

        return result;
    }
}
