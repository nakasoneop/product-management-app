package com.example.my_web_app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.my_web_app.exception.DuplicateProductNameException;
import com.example.my_web_app.exception.ProductNotFoundException;
import com.example.my_web_app.model.Product;
import com.example.my_web_app.repository.ProductRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service // サービス層
public class ProductService {

	//インスタンスを作成
    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    // 商品を追加
    public Product addProduct(Product product) {
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("価格は0以上にする必要があります。");
        }
        return productRepository.save(product);
    }

    //全件取得
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //IDで検索
    public Optional<Product> findProductsById(Long id) {
    	return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst(); //最初に見つかった1件をOptionalとして返す
    }

    // 名前で検索
    public List<Product> findProductsByName(String name) {
        return productRepository.findByName(name);
    }

     //商品の更新
	public Product updateProductWithImage(Long id, Product productDetails, MultipartFile imageFile) throws IOException {
	    //IDから商品を検索
		Product product = productRepository.findById(id)
	        .orElseThrow(() -> new ProductNotFoundException("商品ID: " + id + " が見つかりません。"));

	    // ファイルの保存処理
	    if (imageFile != null && !imageFile.isEmpty()) {
	        // ファイル名を作成（例: id_オリジナルファイル名）d
	        String filename = id + "_" + imageFile.getOriginalFilename();
	        Path uploadPath = Paths.get(UPLOAD_DIR);

	        // ディレクトリが存在しない場合は作成
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        // ファイルを保存
	        Path filePath = uploadPath.resolve(filename);
	        //上書きモード
	        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        // 💡 データベースには、ブラウザからアクセス可能なURLパスを保存
	        product.setImageUrl("/images/" + filename);
	    }
	    // 💡 ファイルが提供されなかった場合、imageUrlは更新しない（既存の値を保持）

	    // 他のデータの上書き
	    product.setName(productDetails.getName());
	    product.setPrice(productDetails.getPrice());
	    product.setStock(productDetails.getStock());
	    product.setDescription(productDetails.getDescription());
	    // imageUrl は上記で更新済み

	    // 更新後の商品名と、更新対象の商品ID（自身）を使って重複チェックを実行
	    Optional<Product> duplicate = productRepository.findByNameAndIdNot(
	            product.getName(), // 新しく設定された名前
	            id // 更新対象の商品ID
	        );

	    // もし自分以外のIDを持つ商品が、同じ名前で存在したら例外をスロー
	    if (duplicate.isPresent()) {
	        throw new DuplicateProductNameException("商品名 '" + product.getName() + "' は既に使用されています。");
	    }

	     // 4. DBへ保存
	    return productRepository.save(product);
	}

	/**
	 * ファイル保存と imageUrl の更新のみを行う（新規登録時に使用）
	 */
	public Product updateImageOnly(Long id, MultipartFile imageFile) throws IOException {
		//IDで商品検索
	    Product product = productRepository.findById(id)
	        .orElseThrow(() -> new ProductNotFoundException("商品ID: " + id + " が見つかりません。"));

	    if (imageFile != null && !imageFile.isEmpty()) {

	        // ファイル名を作成（例: id_オリジナルファイル名）
	        String filename = id + "_" + imageFile.getOriginalFilename();
	        Path uploadPath = Paths.get(UPLOAD_DIR);

	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        // ファイルを保存
	        Path filePath = uploadPath.resolve(filename);
	        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        // データベースには、ブラウザからアクセス可能なURLパスを保存
	        product.setImageUrl("/images/" + filename);
	    }

	    // 商品情報全体ではなく、imageUrlのみが更新される
	    return productRepository.save(product);
	}

     //商品の削除
    public void deleteProduct(Long id) {
        // 1. IDで商品を見つける
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("商品ID: " + id + " が見つかりません。"));

        // 2. DBから削除する
        productRepository.delete(product);

        // 💡 削除されたことを示すため void (何も返さない)
    }
}