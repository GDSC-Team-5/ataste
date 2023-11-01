package com.ataste.ataste.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class KakaoApiService {

    @Value("${kakao.id}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

    public KakaoApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAllResultsFromKakao(String[] queries) {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
        int page = 1;
        int size = 15;
        StringBuilder resultBuilder = new StringBuilder();

        for (String query : queries) {
            page = 1;
            while (true) {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "KakaoAK " + kakaoApiKey);

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("query", query);
                params.add("page", String.valueOf(page));
                params.add("size", String.valueOf(size));

                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
                String responseJson = response.getBody();

                resultBuilder.append(responseJson);

                page++;

                // 카카오 API의 페이지 수 제한에 도달하면 종료
                if (page > 45) {
                    break;
                }
            }
        }

        return resultBuilder.toString();
    }
}
