package com.productcategory;

import java.util.List;
import org.springframework.stereotype.Service;
import com.productcategory.ProductCategory;
import com.productcategory.ProductCategoryDao;
import com.productcategory.ProductCategoryService;


@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    
    private final ProductCategoryDao productCategoryDao;
    
    public ProductCategoryServiceImpl(ProductCategoryDao productCategoryDao) {
        this.productCategoryDao = productCategoryDao;
    }

    @Override
    public ProductCategory addCategory(ProductCategory productCategory) {
        return productCategoryDao.insert(productCategory);
    }

    @Override
    public ProductCategory updateCategory(ProductCategory productCategory) {
        return productCategoryDao.update(productCategory);
    }

    @Override
    public void deleteCategoryById(Integer categoryNo) {
        productCategoryDao.deleteById(categoryNo);
    }

    @Override
    public ProductCategory getCategoryById(Integer categoryNo) {
        return productCategoryDao.findById(categoryNo);
    }

    @Override
    public List<ProductCategory> getAllCategories() {
        return productCategoryDao.findAll();
    }

    
}