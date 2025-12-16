package com.example.my_web_app;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.example.my_web_app.controller.ProductController;
import com.example.my_web_app.exception.DuplicateProductNameException;
import com.example.my_web_app.mapper.ProductMapper;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.model.ProductRequest;
import com.example.my_web_app.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(ProductController.class) // 💡 Controllerテスト専用のアノテーション
public class ProductControllerTest {
    @Autowired //本物のBeanが注入される
    private MockMvc mockMvc; // コントローラーへのHTTPリクエストをシミュレートするオブジェクト
    @Autowired
    private ObjectMapper objectMapper; // JavaオブジェクトとJSONを変換するユーティリティ
    @MockBean // モック化されたBeanが注入される
    private ProductMapper productMapper;
    @MockBean
    private ProductService productService; // サービス層をモック化して、例外をシミュレートする

    @Test
    void 商品名が重複した場合に400BadRequestとエラーメッセージが返されること() throws Exception {
    	// テストデータの準備
        String duplicateName = "重複商品名";
        ProductRequest request = new ProductRequest(duplicateName, 1000, 10, null);
        String requestJson = objectMapper.writeValueAsString(request);
        Long productId = 100L;
        String errorMessage = "商品名 '" + duplicateName + "' は既に使用されています。";

        when(productMapper.toEntity(any(ProductRequest.class)))
        .thenReturn(new Product());

        when(productService.updateProductWithImage(
        		any(Long.class),
        		any(Product.class),
        		isNull()
        	))
            .thenThrow(new DuplicateProductNameException(errorMessage));

        //ブラウザや外部クライアントがファイルとJSONデータを同時にアップロードする際に使用する multipart/form-data 形式のHTTPリクエスト
        // MockMvcRequestBuilders.multipart を使用してリクエストを構築する
        mockMvc.perform(
        	//multipart/form-data 形式のリクエストを作成するために使用
            MockMvcRequestBuilders.multipart("/api/products/{id}/update", productId)
            	//リクエストにPartを追加する
                .file(new MockMultipartFile(
                    "productDetails", // @RequestPart("productDetails") の名前と一致させる。Partの名前
                    null, // ファイル名（不要なのでnull）
                    MediaType.APPLICATION_JSON_VALUE, // パートのContent-TypeをJSONに設定
                    requestJson.getBytes(StandardCharsets.UTF_8) // JSON文字列をバイト配列に変換して渡す
                ))
                //multipartを使用した場合、デフォルトはPOSTだが、明示的に再設定する
                .with(mockRequest -> {
                	mockRequest.setMethod("POST"); // PUTやPOSTを明示的に設定
                    return mockRequest;
                })
            )

                // 4. 検証（Assertions）
                .andExpect(status().isBadRequest()) // HTTPステータスが400であること
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) // 応答のContent-Typeがプレーンテキスト（またはJSON）
                .andExpect(content().encoding("UTF-8"))
                .andExpect(content().string(errorMessage)); // 応答ボディがカスタムエラーメッセージと一致すること
    }

    @Disabled
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