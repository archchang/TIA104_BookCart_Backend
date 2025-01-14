package com.product;

import com.product.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    // 商品資料列映射
    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product product = new Product();
        product.setProduct_no(rs.getInt("product_no"));
        product.setCategory_no(rs.getInt("category_no"));
        product.setProduct_name(rs.getString("product_name"));
        product.setProduct_price(rs.getInt("product_price"));
        product.setProduct_introduce(rs.getString("product_introduce"));
        product.setProduct_stock(rs.getInt("product_stock"));
        product.setProduct_status(rs.getInt("product_status"));
        product.setProduct_picture(rs.getBytes("product_picture"));
        return product;
    };
    
    public ProductDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insert(Product product) {
        String sql = "INSERT INTO product (category_no, product_name, product_price, " +
                    "product_introduce, product_stock, product_status, product_picture) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"product_no"});
            ps.setInt(1, product.getCategory_no());
            ps.setString(2, product.getProduct_name());
            ps.setInt(3, product.getProduct_price());
            ps.setString(4, product.getProduct_introduce());
            ps.setInt(5, product.getProduct_stock());
            ps.setInt(6, product.getProduct_status());
            ps.setBytes(7, product.getProduct_picture());
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().intValue();
    }

    @Override
    public int update(Product product) {
        String sql = "UPDATE product SET category_no=?, product_name=?, product_price=?, " +
                    "product_introduce=?, product_stock=?, product_status=?, product_picture=? " +
                    "WHERE product_no=?";
                    
        return jdbcTemplate.update(sql,
            product.getCategory_no(),
            product.getProduct_name(),
            product.getProduct_price(),
            product.getProduct_introduce(),
            product.getProduct_stock(),
            product.getProduct_status(),
            product.getProduct_picture(),
            product.getProduct_no()
        );
    }

    @Override
    public int delete(Integer product_no) {
        String sql = "DELETE FROM product WHERE product_no = ?";
        return jdbcTemplate.update(sql, product_no);
    }

    @Override
    public Product findById(Integer product_no) {
        String sql = "SELECT * FROM product WHERE product_no = ?";
        List<Product> products = jdbcTemplate.query(sql, productRowMapper, product_no);
        return products.isEmpty() ? null : products.get(0);
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM product";
        return jdbcTemplate.query(sql, productRowMapper);
    }

    @Override
    public List<Product> findByCategory(String categoryName) {
        String sql = "SELECT p.* FROM product p " +
                    "JOIN product_category pc ON p.category_no = pc.category_no " +
                    "WHERE pc.category_name = ?";
        return jdbcTemplate.query(sql, productRowMapper, categoryName);
    }

    @Override
    public List<Product> findByProductNameLike(String productName) {
        String sql = "SELECT * FROM product WHERE product_name LIKE ?";
        return jdbcTemplate.query(sql, productRowMapper, "%" + productName + "%");
    }
}