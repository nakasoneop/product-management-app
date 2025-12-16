package com.example.my_web_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.my_web_app.model.Product;
import com.example.my_web_app.model.ProductRequest;

@Mapper(componentModel = "spring") // SpringでBeanとして利用できるように指定
public interface ProductMapper {

    // MapStructがコンパイル時に、このメソッドの実装を自動生成する
    @Mapping(target = "id", ignore = true) // EntityのIDは無視し、自動生成させる
    Product toEntity(ProductRequest dto);

    // 逆に、EntityをResponseDTOに変換するメソッドも定義できる
//    ProductResponse toResponseDto(Product entity);
}
