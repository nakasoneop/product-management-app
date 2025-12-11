package com.example.my_web_app;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.my_web_app.model.Product;
import com.example.my_web_app.repository.ProductRepository;

@DataJpaTest
@ActiveProfiles("test") //application-test.propertiesから読み込む
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void findByNameContaining_部分一致検索が機能すること() {
        // GIVEN: データをDBに保存 (H2DB)
    	repository.save(new Product("ノートPC Pro", 10, 10, null)); // IDは自動採番される
        repository.save(new Product("デスクトップPC", 5, 10, null));

        // WHEN: 検索を実行
        List<Product> result = repository.findByNameContaining("ノートPC"); // "ノートPC"を含むものを検索

        // THEN: 検索結果が正しいこと (DBから取得したデータを検証)
        assertThat(result).hasSize(1); // 💡 "ノートPC Pro" だけがヒットすることを期待
        assertThat(result.get(0).getName()).isEqualTo("ノートPC Pro");
    }
}