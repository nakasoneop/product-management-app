package com.example.my_web_app.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.my_web_app.exception.DuplicateProductNameException;
import com.example.my_web_app.exception.ProductNotFoundException;

//例外をキャッチして、例外メッセージ＋HTTPステータスをユーザーに知らせる
@ControllerAdvice
public class GlobalExceptionHandler {

 // 商品が見つからなかった場合のハンドリング
 @ExceptionHandler(ProductNotFoundException.class)
 public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
     // HTTP 404 Not Found を返す
     return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
 }

 // 商品名重複例外のハンドリング
 @ExceptionHandler(DuplicateProductNameException.class)
 public ResponseEntity<String> handleDuplicateProductNameException(DuplicateProductNameException ex) {
     // HTTP 400 Bad Request を返す
     // クライアントの入力データ（商品名）に問題があることを示す
	 // このハンドル処理をしないと、例外のメッセージがユーザーに届かない＆500エラー（サーバーエラー）が返されてしまう
     return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
 }

 // 必要に応じて、他の例外（IOExceptionなど）もハンドリングします

}