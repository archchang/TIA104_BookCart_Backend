package com.productcategory;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.productcategory.ProductCategory;
import com.productcategory.ProductCategoryDao;
import com.product.Product;

@Repository
public class ProductCategoryDaoImpl implements ProductCategoryDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ProductCategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // RowMapper用於映射資料庫結果至Java物件
    private RowMapper<ProductCategory> categoryRowMapper = (rs, rowNum) -> {
        ProductCategory category = new ProductCategory();
        category.setCategory_no(rs.getInt("category_no"));
        category.setCategory_name(rs.getString("category_name"));
        category.setCategory_describe(rs.getString("category_describe"));
        return category;
    };
    

    @Override
    public ProductCategory insert(ProductCategory productCategory) {
        String sql = "INSERT INTO product_category (category_name, category_describe) VALUES (?, ?)";
        jdbcTemplate.update(sql,
            productCategory.getCategory_name(),
            productCategory.getCategory_describe()
        );
        return productCategory;
    }

    @Override
    public ProductCategory update(ProductCategory productCategory) {
        String sql = "UPDATE product_category SET category_name = ?, category_describe = ? WHERE category_no = ?";
        jdbcTemplate.update(sql,
            productCategory.getCategory_name(),
            productCategory.getCategory_describe(),
            productCategory.getCategory_no()
        );
        return productCategory;
    }

    @Override
    public void deleteById(Integer categoryNo) {
        String sql = "DELETE FROM product_category WHERE category_no = ?";
        jdbcTemplate.update(sql, categoryNo);
    }

    @Override
    public ProductCategory findById(Integer categoryNo) {
        String sql = "SELECT * FROM product_category WHERE category_no = ?";
        return jdbcTemplate.queryForObject(sql, categoryRowMapper, categoryNo);
    }

    @Override
    public List<ProductCategory> findAll() {
        String sql = "SELECT * FROM product_category";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }
    
    
}