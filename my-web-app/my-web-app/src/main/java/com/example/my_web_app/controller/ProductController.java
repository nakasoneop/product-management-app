package com.example.my_web_app.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.my_web_app.exception.ProductNotFoundException;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.service.ProductService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController // コントローラー層
@RequestMapping("/api/products") // このコントローラーのベースパス
//5173にはアクセスを許可
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    // インスタンス作成
    @Autowired
    private ProductService productService;

    /**
     * 【GET】商品の一覧取得 または 名前による検索
     * URL例: GET http://localhost:8080/api/products?name=ノートPC
     * URL例: GET http://localhost:8080/api/products
     */
    //RequestParamパターン（URLに埋め込む）
    @GetMapping
    public List<Product> getProducts(
        @RequestParam(value = "name", required = false) String name) {

        // nameパラメータが指定されていない、または空の場合は全件検索
        if (name == null || name.isEmpty()) {
            return productService.findAll();
        } else {
            // nameパラメータがある場合は、Serviceに名前検索を依頼
            return productService.findProductsByName(name);
        }
    }

    //PathVariableパターン
    //URL例: GET http://localhost:8080/api/products/1
    @GetMapping("/{id}")
    public Optional<Product> getProductsById(@PathVariable Long id) {
            return productService.findProductsById(id);
    }

    //商品登録
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
    	return productService.addProduct(product);
    }

    /**
     * 画像ファイルを受け取り、更新するAPI
     * 新規登録後の画像アップロードに使用
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<Product> uploadImage(
            @PathVariable Long id,
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            Product updatedProduct = productService.updateImageOnly(id, imageFile);
            return ResponseEntity.ok(updatedProduct);

        } catch (IOException e) {
            // ファイル保存時のエラー
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ProductNotFoundException e) {
            // 商品IDが見つからないエラー
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 商品の更新 (PUT)
     * 指定された ID のリソース全体を新しいデータで置き換える。
     */
    @PostMapping("/{id}/update")
    public ResponseEntity<Product> updateProduct(
    		@PathVariable Long id,
    		@RequestPart("productDetails") Product productDetails,
    		@RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

    	try {
            Product updatedProduct = productService.updateProductWithImage(id, productDetails, imageFile);
            return ResponseEntity.ok(updatedProduct);

        } catch (IOException e) {
        	// ファイル保存時のエラー
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 商品の削除 (DELETE)
     * 指定された ID のリソースを削除する。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        // 204 No Content を返す (削除成功したが、返すボディがない場合)
        return ResponseEntity.noContent().build(); //204の場合は.build()が必要
    }

     //例外処理
     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
         // 400 Bad Request を返す
         // 💡 エラーメッセージをJSON形式でクライアントに返すためのMapを作成
         Map<String, String> errorDetails = new HashMap<>();
         errorDetails.put("message", e.getMessage());

         // 400 Bad Request とエラーメッセージを返す
         return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
     }

     @ExceptionHandler(ProductNotFoundException.class)
     public ResponseEntity<Map<String, String>> handleNotFoundException(ProductNotFoundException e) {
         Map<String, String> errorDetails = new HashMap<>();
         errorDetails.put("message", e.getMessage());

         // 💡 404 NOT_FOUND ステータスコードを返す
         return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // 404
     }
}