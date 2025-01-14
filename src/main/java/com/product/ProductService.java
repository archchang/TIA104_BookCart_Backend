package com.product;

import com.dto.ProductDTO;
import com.product.Product;
import java.util.List;

public interface ProductService {
	// 新增商品
    Product addProduct(Product product);
    
    // 更新商品
    Product updateProduct(Product product);
    
    // 刪除商品
    boolean deleteProduct(Integer product_no);
    
    // 查詢單一商品
    Product getProduct(Integer product_no);
    
    // 查詢所有商品，回傳DTO列表
    List<ProductDTO> getAllProducts();
    
    // 依分類查詢商品，回傳DTO列表
    List<ProductDTO> getProductsByCategory(String categoryName);
    
    // 依商品名稱查詢商品，回傳DTO列表
    List<ProductDTO> searchProductsByName(String productName);
    
    // 商品實體轉換為DTO
    ProductDTO convertToDTO(Product product);
}