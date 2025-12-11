package com.example.my_web_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity // データベースのテーブルに対応するクラスであることを示す
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id // 主キー（テーブルの一意な識別子）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDを自動生成する設定
    private Long id;
    private Long productId;
    private String productName;
    private int unitPrice;
    private int quantity;
    private int total;

    // コンストラクタ（Eclipseで自動生成できます: Source -> Generate Constructor...）
	public Order(Long id, Long productId, String productName, int unitPrice, int quantity, int total) {
		super();
		this.id = id;
		this.productId = productId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.total = total;
	}
}