package com.example.my_web_app;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.example.my_web_app.controller.ProductController;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.service.ProductService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(ProductController.class) // 💡 Controllerテスト専用のアノテーション
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    // 💡 Service層をモック化
    @MockBean private ProductService productService;

    @Test
    void createProduct_正常なリクエストで201が返されること() throws Exception {
        // GIVEN 1: Serviceモックの振る舞いを設定
        Product savedProduct = new Product("テストPC", 100000, 10, null); // サービスが返すProduct
        given(productService.addProduct(any(Product.class))) // 💡 どんなProductを受け取っても
            .willReturn(savedProduct); // 💡 この savedProduct を返すように設定

        // WHEN & THEN: HTTPリクエストを模擬し、レスポンスを検証
        mockMvc.perform(post("/api/products") // 💡 POSTリクエストをシミュレーション
                .contentType(MediaType.APPLICATION_JSON) // 💡 送信するデータの形式を指定
                .content("{\"name\": \"テストPC\", \"price\": 100000}")) // 💡 送信するJSONボディ

                // 💡 応答の検証
                .andExpect(status().isCreated()) // 💡 HTTPステータスコードが 201 であること
                .andExpect(jsonPath("$.name").value("テストPC")); // 💡 レスポンスJSONのnameフィールドが正しいこと

        // 💡 動作の検証 (ControllerがServiceを正しく呼び出したか)
        verify(productService, times(1)).addProduct(any(Product.class));
    }
}