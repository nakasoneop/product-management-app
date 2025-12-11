package com.example.my_web_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
//import lombok.Getter;
//import lombok.Setter;
import lombok.Setter;

@Entity // データベースのテーブルに対応するクラスであることを示す
@Getter
@Setter
public class Product {

    @Id // 主キー（テーブルの一意な識別子）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDを自動生成する設定
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String description;
    private String imageUrl;

    // コンストラクタ（Eclipseで自動生成できます: Source -> Generate Constructor...）
    public Product() {}

    public Product(String name, int price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }
}