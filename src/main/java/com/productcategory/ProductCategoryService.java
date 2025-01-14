package com.productcategory;

import java.util.List;
import com.productcategory.ProductCategory;
import com.product.Product;

public interface ProductCategoryService {
    // 新增類別
    ProductCategory addCategory(ProductCategory productCategory);
    
    // 修改類別
    ProductCategory updateCategory(ProductCategory productCategory);
    
    // 刪除類別
    void deleteCategoryById(Integer categoryNo);
    
    // 查詢單一類別
    ProductCategory getCategoryById(Integer categoryNo);
    
    // 查詢所有類別
    List<ProductCategory> getAllCategories();
    
}