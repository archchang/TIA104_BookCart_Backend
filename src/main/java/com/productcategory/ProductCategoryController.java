package com.productcategory;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.productcategory.ProductCategory;
import com.productcategory.ProductCategoryService;


@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    
    private final ProductCategoryService productCategoryService;
    
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
    
    // 新增類別
    @PostMapping
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory created = productCategoryService.addCategory(productCategory);
        return ResponseEntity.ok(created);
    }
    
    // 修改類別
    @PutMapping("/{categoryNo}")
    public ResponseEntity<ProductCategory> updateCategory(
            @PathVariable Integer categoryNo,
            @RequestBody ProductCategory productCategory) {
        productCategory.setCategory_no(categoryNo);
        ProductCategory updated = productCategoryService.updateCategory(productCategory);
        return ResponseEntity.ok(updated);
    }
    
    // 刪除類別
    @DeleteMapping("/{categoryNo}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryNo) {
        productCategoryService.deleteCategoryById(categoryNo);
        return ResponseEntity.ok().build();
    }
    
    // 查詢單一類別
    @GetMapping("/{categoryNo}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Integer categoryNo) {
        ProductCategory category = productCategoryService.getCategoryById(categoryNo);
        return ResponseEntity.ok(category);
    }
    
    // 查詢所有類別
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = productCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    
}