package com.example.my_web_app;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping; // 共通パス設定
import org.springframework.web.bind.annotation.RequestParam;

@RestController
// 共通パスを /api/users に設定
@RequestMapping("/api/users")
public class UserController {

    // アクセスURL: /api/users/{id}
    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") long userId) {
        // ... ユーザー情報取得ロジック ...
        String response = userId + "番のユーザー情報を取得しました。";
        return response;
    }

    @GetMapping("/search")
    public String searchProducts(
        @RequestParam("keyword") String keyword, // ❷ keywordパラメータを受け取り
        @RequestParam(value = "page", defaultValue = "1") int page // ❸ pageパラメータを受け取り（値がない場合はデフォルトで1を使用）
    ) {
        // 例: 検索ロジックを記述

        String response = "\"" + keyword + "\"を検索しました。";
        response += "現在のページは " + page + " です。";

        return response;
    }
}