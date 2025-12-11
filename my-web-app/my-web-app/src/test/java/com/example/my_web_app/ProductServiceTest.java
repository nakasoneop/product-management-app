package com.example.my_web_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.repository.ProductRepository;
import com.example.my_web_app.service.ProductService;

import static org.junit.jupiter.api.Assertions.*; // アサーション（検証）メソッドをインポート
import static org.mockito.Mockito.*; // Mockitoのメソッドをインポート

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Mockitoを使用するための設定
public class ProductServiceTest {

    @Mock // 💡 Repositoryはデータベース接続を含むため、モック（偽物）に置き換える
    private ProductRepository productRepository;

    @InjectMocks // 💡 テスト対象のクラス。@Mockで作成したインスタンスがここに自動注入される
    private ProductService productService;

//	private Product testProduct;

//	@BeforeEach // 💡 各 @Test メソッドの直前に実行される
//    void setup() {
//        // テスト用の共通のProductオブジェクトを初期化
//        testProduct = new Product("共通テスト商品", 5000);
//        testProduct.setId(1L);
//
//        // 💡 共通のモックの振る舞いを設定
//        //     ここでは、save() が呼ばれたら testProduct を返すように設定
//        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
//    }
//
//	@Test
//    void saveProduct_正常な価格_保存成功2() {
//        // GIVEN/WHEN/THEN の実行
//        Product savedProduct = productService.saveProduct(testProduct); // testProductを使用
//
//        // THEN: 検証
//        assertNotNull(savedProduct);
//        assertEquals(5000, savedProduct.getPrice());
//        verify(productRepository, times(1)).save(testProduct);
//    }

    @Test // 💡 これがテストメソッドであるという目印
    void saveProduct_正常な価格_保存成功() {
        // GIVEN（前提条件）: テスト用の商品オブジェクトを作成
        Product inputProduct = new Product("テスト商品", 1000, 10, null);

        // Mockitoで「productRepository.save()が呼ばれたら、このオブジェクトを返す」という動作を設定
        when(productRepository.save(any(Product.class))).thenReturn(inputProduct);

        // WHEN（実行）: テスト対象のメソッドを実行
        Product savedProduct = productService.addProduct(inputProduct);

        // THEN（検証）: 結果が期待通りかチェック
        assertNotNull(savedProduct); // 戻り値がnullでないこと
        assertEquals(1000, savedProduct.getPrice()); // 価格が1000であること

        // 💡 データベース操作が1回実行されたか検証
        verify(productRepository, times(1)).save(inputProduct);
    }

    @Test
    void saveProduct_不正な価格_例外発生() {
        // GIVEN: 不正な価格の商品
        Product inputProduct = new Product("テスト商品", -500, 10, null);

        // WHEN & THEN: 期待される例外（IllegalArgumentException）が発生することを検証
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(inputProduct);
        });

        // 💡 saveメソッドが一度も呼ばれていないことを検証
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void saveProduct_境界値ゼロ_保存成功() {
        // GIVEN: 価格が0の商品
        Product inputProduct = new Product("無料商品", 0, 10, null);
        when(productRepository.save(any(Product.class))).thenReturn(inputProduct);

        // WHEN: 実行
        Product savedProduct = productService.addProduct(inputProduct);

        // THEN: 成功することを確認
        assertNotNull(savedProduct);
        assertEquals(0, savedProduct.getPrice());
        verify(productRepository, times(1)).save(inputProduct);
    }

    @Test
    void saveProduct_不正な境界値マイナス1_例外発生() {
        // GIVEN: 価格が-1の商品
        Product inputProduct = new Product("不正商品", -1, 10, null);

        // WHEN & THEN: IllegalArgumentExceptionが発生することを検証
        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(inputProduct);
        });

        // DBアクセスがされていないことを確認
        verify(productRepository, never()).save(any(Product.class));
    }

 // ProductServiceTest.java の既存コードに追加

    @Test
    void findProductsByName_正常な検索_リストが返される() {
        // GIVEN (前提条件)
        String searchName = "ノートPC";

        // 1. モックが返す、期待される結果リストを作成
        Product p1 = new Product("ノートPC Pro", 150000, 10, searchName);
        Product p2 = new Product("ノートPC Light", 98000, 10, searchName);
        List<Product> expectedList = List.of(p1, p2);

        // 2. Repositoryのモックの振る舞いを設定 (スタブ)
        // 💡 productRepository.findByName("ノートPC") が呼ばれたら、
        //    上で作った expectedList を返すように設定
        when(productRepository.findByName(searchName)).thenReturn(expectedList);

        // WHEN (実行)
        // テスト対象のメソッドを実行
        List<Product> actualList = productService.findProductsByName(searchName);

        // THEN (検証)
        // 1. 戻り値が期待通りのリストと一致しているか
        assertNotNull(actualList); // nullではないこと
        assertEquals(2, actualList.size()); // サイズが2であること
        assertEquals(expectedList, actualList); // リストの中身が完全に一致すること

        // 2. サービス層が正しくリポジトリを呼び出したか検証
        verify(productRepository, times(1)).findByName(searchName);
        verifyNoMoreInteractions(productRepository); // 💡 その他のRepositoryメソッドが呼ばれていないことの確認
    }

//    @Test
//    void buyProduct_在庫がある場合_購入成功() {
//        // GIVEN (前提条件)
//        // 1. 在庫が10個ある商品ID
//        Long productId = 1L;
//        // 2. 1個購入する
//        int quantityToBuy = 1;
//
//        // 💡 Repositoryのモックの振る舞いを設定
//        // findById(1L) が呼ばれたら、在庫10の商品を返すように設定
//        Product productWithStock = new Product("在庫あり商品", 1000);
//        productWithStock.setId(productId);
//        productWithStock.setStock(10);
//
//        // Mockitoで findById が呼ばれたときの動作を設定
//        when(productRepository.findById(productId)).thenReturn(Optional.of(productWithStock));
//
//        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
//            // saveの引数（保存されるProduct）を取得し、それをそのまま戻り値として返す
//            return invocation.getArgument(0);
//        });
//
//        // WHEN (実行)
//        // 💡 buyProduct(ID, 数量) というメソッドを実行する
//        Product result = productService.orderProduct(productId, quantityToBuy);
//
//        // THEN (検証)
//        // 1. 購入後の在庫が9になっていること
//        assertEquals(9, result.getStock());
//
//        // 2. save() メソッドが1回呼ばれていること
//        verify(productRepository, times(1)).save(any(Product.class));
//        verify(productRepository, times(1)).save(productWithStock);
//        verify(productRepository, times(1)).save(argThat(p -> p.getStock() == 9));
//    }

//    @Test
//    void buyProduct_在庫不足の場合_例外発生() {
//        // GIVEN (前提条件)
//        Long productId = 2L;
//        int quantityToBuy = 2; // 買いたい数量
//
//        // 1. 在庫が1個しかない商品を作成
//        Product productLowStock = new Product("在庫不足商品", 2000);
//        productLowStock.setId(productId);
//        productLowStock.setStock(1); // 💡 在庫を1に設定
//
//        // 2. Mockitoで findById(2L) が呼ばれたら、在庫1の商品を返すように設定
//        when(productRepository.findById(productId)).thenReturn(Optional.of(productLowStock));
//
//        // WHEN & THEN (実行と検証)
//        // 💡 buyProduct(ID, 数量) を実行し、例外が発生することを期待
//        assertThrows(IllegalArgumentException.class, () -> {
//            productService.orderProduct(productId, quantityToBuy);
//        });
//
//        // 3. 検証: 在庫不足で例外を投げた場合、データベースへの保存処理（save）は実行されないこと
//        verify(productRepository, never()).save(any(Product.class));
//    }
}