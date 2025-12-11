package com.example.my_web_app;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

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

    @Test
    void testFindByNameAndIdNot_自己の名前で重複しないこと() {
        // 準備（Product AをDBに投入）
        Product productA = new Product("テスト商品A", 1000, 5, null);
        Product savedA = repository.save(productA);

        // 実行
        // Product Aの名前（"テスト商品A"）で、Product AのIDを除外して検索
        Optional<Product> found = repository.findByNameAndIdNot(
            "テスト商品A",
            savedA.getId() // <-- 自分自身のIDを除外
        );

        // 検証
        // DB内にIDが savedA.getId() 以外の「テスト商品A」は存在しないため、結果は空になる
        assertThat(found).isNotPresent();
    }

    @Test
    void testFindByNameAndIdNot_他の商品名と重複する場合() {
        // 準備（Product AとProduct BをDBに投入）
        Product productA = new Product("テスト商品A", 1000, 5, null);
        Product savedA = repository.save(productA);

        Product productB = new Product("テスト商品B", 2000, 10, null);
        Product savedB = repository.save(productB);

        // 実行
        // Product A（savedA.getId()）が、Product Bの名前を使おうとしてチェック
        Optional<Product> found = repository.findByNameAndIdNot(
            "テスト商品B",
            savedA.getId() // <-- Product AのIDを除外
        );

        // 検証
        // 除外ID以外で "テスト商品B" が存在するため、重複として検出される
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedB.getId()); // 検出されたのはProduct B
    }
}