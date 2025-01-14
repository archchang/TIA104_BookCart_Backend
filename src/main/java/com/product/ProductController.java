package com.product;

import com.dto.ProductDTO;
import com.product.Product;
import com.product.ImageService;
import com.product.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    private final ImageService imageService;
    
    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    // 新增商品（含圖片上傳）
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                product.setProduct_picture(imageService.processImageUpload(image));
            }
            Product createdProduct = productService.addProduct(product);
            return ResponseEntity.ok(productService.convertToDTO(createdProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 更新商品（含圖片）
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable("id") Integer productNo,
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            product.setProduct_no(productNo);
            if (image != null && !image.isEmpty()) {
                product.setProduct_picture(imageService.processImageUpload(image));
            }
            Product updatedProduct = productService.updateProduct(product);
            if (updatedProduct == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productService.convertToDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 取得商品圖片
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getProductImage(@PathVariable("id") Integer productNo) {
        try {
            Product product = productService.getProduct(productNo);
            if (product == null || product.getProduct_picture() == null) {
                return ResponseEntity.notFound().build();
            }
            
            String fileName = "product-" + productNo + ".jpg";  // 預設檔名
            Resource resource = imageService.getImageAsResource(
                product.getProduct_picture(),
                MediaType.IMAGE_JPEG_VALUE
            );
            
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + fileName + "\"")
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 刪除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Integer productNo) {
        boolean deleted = productService.deleteProduct(productNo);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("商品已成功刪除");
    }

    // 查詢單一商品
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Integer productNo) {
        Product product = productService.getProduct(productNo);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.convertToDTO(product));
    }

    // 查詢所有商品
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 依分類查詢商品
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable("categoryName") String categoryName) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryName);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    // 依商品名稱模糊查詢
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(value = "name", required = true) String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ProductDTO> products = productService.searchProductsByName(productName);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }
}