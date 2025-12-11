package com.example.my_web_app.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.my_web_app.ProductNotFoundException;
import com.example.my_web_app.model.Order;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.repository.OrderRepository;
import com.example.my_web_app.repository.ProductRepository;

@Service // 💡 Springにサービスコンポーネントとして認識させる
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

 //購入ロジック
    @Transactional
    public Order orderProduct(Order order) {
        //  IDで商品を取得
    	Long produtId = order.getProductId();
    	int quantity = order.getQuantity();
    	Product product = productRepository.findById(produtId)
        		.orElseThrow(() -> new ProductNotFoundException("商品ID: " + produtId + " が見つかりません。"));

    	// 在庫チェック
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("在庫が不足しています。");
        }

     // 在庫を減らす
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }
}