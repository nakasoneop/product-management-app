package com.example.my_web_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// JSR 380 (Bean Validation) のアノテーションをインポート
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data // Getter, Setter, toString, equals, hashCodeを自動生成
@NoArgsConstructor // 引数なしコンストラクタを自動生成
@AllArgsConstructor // 全引数コンストラクタを自動生成
public class ProductRequest {

    // 【商品名】必須チェックとサイズ制限
    @NotBlank(message = "商品名は必須項目です")
    @Size(max = 100, message = "商品名は100文字以内で入力してください")
    private String name;

    // 【価格】必須チェックと最小値制限
    @NotNull(message = "価格は必須項目です")
    @Min(value = 1, message = "価格は1以上である必要があります")
    private Integer price;

    // 【在庫数】必須チェックと最小値制限
    @NotNull(message = "在庫数は必須項目です")
    @Min(value = 0, message = "在庫数は0以上である必要があります")
    private Integer stock;

    //【商品説明】最大値制限
    @Size(max = 500, message = "商品説明は500文字以内で入力してください")
    private String description;

    // (画像ファイルはMultipartFileとしてController側で直接受け取るため、DTOには含めません)
}