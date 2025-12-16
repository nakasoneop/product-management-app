package com.example.my_web_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.my_web_app.model.Product;

// JpaRepositoryを継承するだけで、基本的なDB操作メソッドが自動で使えるようになる
// <扱うエンティティの型, エンティティのIDの型>
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 独自の検索メソッドを定義したい場合はここに記述する
    List<Product> findByName(String name);
    List<Product> findByNameContaining(String name);
    Optional<Product> findByNameAndIdNot(String name, Long id);
}