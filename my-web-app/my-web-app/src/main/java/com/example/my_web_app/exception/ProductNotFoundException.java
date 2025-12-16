package com.example.my_web_app.exception;

//ProductNotFoundException.java
public class ProductNotFoundException extends RuntimeException {
 public ProductNotFoundException(String message) {
     super(message);
 }
}