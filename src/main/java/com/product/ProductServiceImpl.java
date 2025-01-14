package com.product;

import com.dto.ProductDTO;
import com.product.ProductDao;
import com.product.Product;
import com.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductDao productDao;
    
    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }
    
    @Override
    public Product addProduct(Product product) {
        int id = productDao.insert(product);
        product.setProduct_no(id);
        return product;
    }
    
    @Override
    public Product updateProduct(Product product) {
        return productDao.update(product) > 0 ? product : null;
    }
    
    @Override
    public boolean deleteProduct(Integer product_no) {
        return productDao.delete(product_no) > 0;
    }
    
    @Override
    public Product getProduct(Integer product_no) {
        return productDao.findById(product_no);
    }
    
    @Override
    public List<ProductDTO> getAllProducts() {
        return productDao.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductDTO> getProductsByCategory(String categoryName) {
        return productDao.findByCategory(categoryName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductDTO> searchProductsByName(String productName) {
        return productDao.findByProductNameLike(productName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProduct_no(product.getProduct_no());
        dto.setCategory_no(product.getCategory_no());
        dto.setProduct_name(product.getProduct_name());
        dto.setProduct_price(product.getProduct_price());
        dto.setProduct_introduce(product.getProduct_introduce());
        dto.setProduct_stock(product.getProduct_stock());
        dto.setProduct_status(product.getProduct_status());
        
        // 設置圖片大小資訊
        if (product.getProduct_picture() != null) {
            dto.setImageSize((long) product.getProduct_picture().length);
        }
        
        return dto;
    }
}