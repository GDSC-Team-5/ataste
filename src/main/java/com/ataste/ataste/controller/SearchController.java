package com.ataste.ataste.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SearchController {
    @Value("${kakao.url}")
    private String kakaoApiUrl;

    @Value("${kakao.id}")
    private String kakaoApiKey;

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam("query") String query) {
        // Create a RestTemplate for making HTTP requests
        RestTemplate restTemplate = new RestTemplate();

        // Set up request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Build the URL for the Kakao API request
        String apiUrl = kakaoApiUrl + "?query=" + query + "&category_group_code=FD6&category_group_code=CE7";

        // Send the Kakao API request with headers and handle the response
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        // Return the API response and status code to the client
        return responseEntity;
    }
}
