package com.product;

import com.product.Product;
import java.util.List;


public interface ProductDao {
	// 新增商品
    int insert(Product product);
    
    // 更新商品
    int update(Product product);
    
    // 刪除商品
    int delete(Integer product_no);
    
    // 依編號查詢商品
    Product findById(Integer product_no);
    
    // 查詢所有商品
    List<Product> findAll();
    
    // 依分類查詢商品
    List<Product> findByCategory(String categoryName);
    
    // 依商品名稱模糊查詢
    List<Product> findByProductNameLike(String productName);

}