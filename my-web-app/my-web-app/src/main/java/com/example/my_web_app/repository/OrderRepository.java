package com.example.my_web_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_web_app.model.Order;

// JpaRepositoryを継承するだけで、基本的なDB操作メソッドが自動で使えるようになる
// <扱うエンティティの型, エンティティのIDの型>
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 独自の検索メソッドを定義したい場合はここに記述する
}