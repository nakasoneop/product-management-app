package com.example.my_web_app.exception;

// RuntimeExceptionを継承し、非チェック例外とする
public class DuplicateProductNameException extends RuntimeException {

    // コンストラクタ
    public DuplicateProductNameException(String message) {
        super(message);
    }
}