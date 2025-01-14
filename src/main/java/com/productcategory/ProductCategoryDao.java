package com.productcategory;

import java.util.List;
import com.productcategory.ProductCategory;
import com.product.Product;

public interface ProductCategoryDao {
	// 新增商品類別
    ProductCategory insert(ProductCategory productCategory);
    
    // 修改商品類別
    ProductCategory update(ProductCategory productCategory);
    
    // 刪除商品類別
    void deleteById(Integer categoryNo);
    
    // 查詢單一商品類別
    ProductCategory findById(Integer categoryNo);
    
    // 查詢所有商品類別
    List<ProductCategory> findAll();
}