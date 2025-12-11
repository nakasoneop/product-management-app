package com.example.my_web_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.repository.ProductRepository;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest // アプリケーションコンテキスト全体を起動
@AutoConfigureMockMvc // MockMvcを有効化
@Transactional // テスト後のDB変更をロールバック
@ActiveProfiles("test") // H2DB設定を読み込む
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストを模擬的に送るためのオブジェクト

    @Autowired
    private ProductRepository productRepository; // 実際のDBアクセスを確認するために使用

    @Test
    void createProduct_API経由で商品が正常に作成されること() throws Exception {
        // GIVEN (前提条件): 送信するリクエストボディのJSONデータ
        String requestJson = "{\"name\": \"新製品テストPC\", \"price\": 120000, \"stock\": 10}";

        // WHEN (実行): MockMvcを使ってPOSTリクエストを模擬的に実行
        mockMvc.perform(post("/api/products") // 💡 ControllerのPOSTパス
                .contentType("application/json") // リクエストのContent-Typeを設定
                .content(requestJson))         // 送信するJSONを設定

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isCreated()) // 💡 HTTPステータスコードが 201 Created であること
                .andExpect(jsonPath("$.name").value("新製品テストPC")); // 💡 戻り値のJSONのnameフィールドが正しいこと

        // THEN (検証 2): データベースにデータが永続化されたことを検証
        // 💡 実際の Repository を使って DB からデータを検索する
        List<Product> products = productRepository.findByName("新製品テストPC");

        // 💡 findByNameが曖昧検索でない場合でも、ここでは完全一致で保存されているかを確認
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isEqualTo(120000);
    }

    @Test
    void purchaseProduct_正常な購入リクエストで在庫が正しく減少すること() throws Exception {
        // GIVEN: 在庫が3個の商品をDBに用意する
        Product initialProduct = new Product("人気商品A", 5000, 10, null);
        // 💡 実際のRepositoryを使ってH2DBに保存
        Product savedProduct = productRepository.save(initialProduct);

        Long productId = savedProduct.getId();
        int purchaseQuantity = 4; // 4個購入する

        // WHEN (実行): 4個購入するリクエストを模擬的に実行
        mockMvc.perform(post("/api/products/purchase") // 💡 APIのエンドポイント (Controllerに定義されているはず)
                .param("productId", productId.toString()) // パラメータでIDを指定
                .param("quantity", String.valueOf(purchaseQuantity))) // パラメータで数量を指定

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isNoContent()); // 💡 HTTPステータスコードが 204 であること

        Product finalProduct = productRepository.findById(productId).orElseThrow();
        // 💡 在庫が 10 - 4 = 6 になっていることを確認
        assertThat(finalProduct.getStock()).isEqualTo(6);
    }

    @Test
    void purchaseProduct_在庫不足の場合_BadRequestが返されること() throws Exception {
        // GIVEN: 在庫が3個の商品をDBに用意する
        Product initialProduct = new Product("限定品", 10000, 3, null);
        // 💡 実際のRepositoryを使ってH2DBに保存
        Product savedProduct = productRepository.save(initialProduct);

        Long productId = savedProduct.getId();
        int purchaseQuantity = 5; // 在庫3に対して5個を購入

        // WHEN: 在庫以上の数量（5個）を購入するリクエストを模擬的に実行
        mockMvc.perform(post("/api/products/purchase") // 💡 APIのエンドポイント (Controllerに定義されているはず)
                .param("productId", productId.toString()) // パラメータでIDを指定
                .param("quantity", String.valueOf(purchaseQuantity))) // パラメータで数量を指定

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isBadRequest()) // 💡 HTTPステータスコードが 400 であること
                .andExpect(jsonPath("$.message").value("在庫が不足しています。")); // 💡 Controllerが返すエラーメッセージを検証

        // THEN (検証 2): DBの在庫数が変更されていないことを検証
        // 💡 ロールバックとは別に、購入処理が失敗しDBが変更されていないことを確認
        Product finalProduct = productRepository.findById(productId).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(3); // 💡 在庫数が元の3のままであること
    }

    @Test
    void purchaseProduct_存在しないIDの場合_NotFoundが返されること() throws Exception {
    	Long nonExistentId = 9999L;
        int purchaseQuantity = 1;

        mockMvc.perform(post("/api/products/purchase") // 💡 APIのエンドポイント (Controllerに定義されているはず)
                .param("productId", nonExistentId.toString()) // パラメータでIDを指定
                .param("quantity", String.valueOf(purchaseQuantity))) // パラメータで数量を指定

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isNotFound()) // 💡 HTTPステータスコードが 400 であること

	     // 💡 Controllerの@ExceptionHandlerが返すJSONエラーメッセージを検証
	     // （ProductServiceが投げるProductNotFoundExceptionのメッセージと一致するはず）
                .andExpect(jsonPath("$.message").value("商品ID: 9999 が見つかりません。"));
    }

    @Test
    void purchaseProduct_在庫数が1で数量1の場合_在庫がゼロになること() throws Exception {
        // GIVEN 1: 境界値となる在庫数1の商品をDBに用意
        Product initialProduct = new Product("ラスト1点", 1000, 1, null);
        Product savedProduct = productRepository.save(initialProduct);

        Long productId = savedProduct.getId();
        int purchaseQuantity = 1; // 💡 境界値：購入数量1

        // WHEN: 1個購入リクエストを実行
        mockMvc.perform(post("/api/products/purchase")
                .param("productId", productId.toString())
                .param("quantity", String.valueOf(purchaseQuantity)))

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isNoContent()); // 200 OK (または 204 No Content)

        // THEN (検証 2): DBの在庫数が0になったことを検証
        Product finalProduct = productRepository.findById(productId).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(0); // 💡 在庫が0であることを確認
    }

    @Test
    void purchaseProduct_在庫数が1で数量2の場合_在庫不足で400が返されること() throws Exception {
        // GIVEN 1: 境界値となる在庫数1の商品をDBに用意
        Product initialProduct = new Product("在庫切れ間近", 1000, 1, null);
        Product savedProduct = productRepository.save(initialProduct);

        Long productId = savedProduct.getId();
        int purchaseQuantity = 2; // 💡 境界値の隣：購入数量2

        // WHEN: 2個購入リクエストを実行
        mockMvc.perform(post("/api/products/purchase")
                .param("productId", productId.toString())
                .param("quantity", String.valueOf(purchaseQuantity)))

        // THEN (検証 1): APIの応答を検証
                .andExpect(status().isBadRequest()) // 💡 400 Bad Request であること
                .andExpect(jsonPath("$.message").value("在庫が不足しています。"));

        // THEN (検証 2): DBの在庫数が変わっていないことを検証
        // 💡 異常終了のため、トランザクションがロールバックされることを確認
        Product finalProduct = productRepository.findById(productId).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(1); // 💡 在庫数が元の1のままであること
    }
}