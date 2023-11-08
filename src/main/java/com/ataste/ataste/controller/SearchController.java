package com.ataste.ataste.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Value("${kakao.url}")
    private String kakaoApiUrl;

    @Value("${kakao.id}")
    private String kakaoApiKey;

    @GetMapping("/{location}")
    public ResponseEntity<String> search(@PathVariable("location") String location,
                                         @RequestParam(name = "category", required = false) String category) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String apiUrl = kakaoApiUrl + "?query=" + location + "&category_group_code=FD6&category_group_code=CE7";


        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        return responseEntity;
    }
}

