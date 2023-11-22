package com.ataste.ataste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OauthController {

    @GetMapping("/kakao/oauthLogin")
    public String kakaoOAuthLogin() {
        return "oauthLogin";
    }
}
