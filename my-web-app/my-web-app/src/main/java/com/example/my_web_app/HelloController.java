package com.example.my_web_app;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // 共通パス設定

@RestController
// 共通パスを /api/general に設定
@RequestMapping("/api/general")
public class HelloController {

    // アクセスURL: /api/general/hello
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Spring Boot Web App!";
    }
}