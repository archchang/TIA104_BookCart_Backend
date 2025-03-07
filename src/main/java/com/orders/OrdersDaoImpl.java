package com.orders;

import com.orders.Orders;
import com.shop.Cart;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OrdersDaoImpl implements OrdersDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    public OrdersDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Orders save(Orders order) {
        String sql = "INSERT INTO orders (member_no, orders_status, orders_receiver, " +
                    "receiver_phone, receiver_zip, receiver_address, orders_total, orders_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        jdbcTemplate.update(sql,
            order.getMember_no(),
            order.getOrders_status(),
            order.getOrders_receiver(),
            order.getReceiver_phone(),
            order.getReceiver_zip(),
            order.getReceiver_address(),
            order.getOrders_total(),
            order.getOrders_date()
        );

        // 取得剛新增的訂單編號
        Integer newOrderId = jdbcTemplate.queryForObject(
            "SELECT LAST_INSERT_ID()", Integer.class);
        order.setOrders_no(newOrderId);
        
        return order;
    }

    @Override
    public List<Orders> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM orders",
            new BeanPropertyRowMapper<>(Orders.class)
        );
    }

    @Override
    public List<Orders> findByMemberNo(Integer memberNo) {
        return jdbcTemplate.query(
            "SELECT * FROM orders WHERE member_no = ? ORDER BY orders_no DESC",
            new BeanPropertyRowMapper<>(Orders.class),
            memberNo
        );
    }

    @Override
    public List<Map<String, Object>> findDetailsByOrderNo(Integer ordersNo) {
        String sql = "SELECT od.*, p.product_name FROM orders_details od " +
                    "JOIN product p ON od.product_no = p.product_no " +
                    "WHERE od.orders_no = ? ORDER BY od.product_no";
                    
        return jdbcTemplate.queryForList(sql, ordersNo);
    }
    
    @Override
    public void saveOrderDetails(Integer ordersNo, Cart cart) {
        String sql = "INSERT INTO orders_details (orders_no, product_no, quantity, product_price) " +
                    "VALUES (?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
            ordersNo,
            cart.getProduct_no(),
            cart.getQuantity(),
            cart.getProduct_price()
        );
    }

    @Override
    public String findMemberEmail(Integer memberNo) {
        return jdbcTemplate.queryForObject(
            "SELECT member_email FROM member WHERE member_no = ?",
            String.class,
            memberNo
        );
    }
    
    @Override
    public List<Orders> searchOrders(Integer ordersNo, Integer memberNo, Date startDate, Date endDate) {
        // 基本 SQL
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1");
        
        // 用來裝動態參數
        List<Object> params = new ArrayList<>();

        // 動態判斷各條件是否存在，若存在則拼接到 SQL
        if (ordersNo != null) {
            sql.append(" AND orders_no = ?");
            params.add(ordersNo);
        }

        if (memberNo != null) {
            sql.append(" AND member_no = ?");
            params.add(memberNo);
        }

        if (startDate != null) {
            sql.append(" AND orders_date >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append(" AND orders_date <= ?");
            params.add(endDate);
        }
        
        // 使用 BeanPropertyRowMapper 轉換資料庫回傳的欄位至 Orders 物件
        return jdbcTemplate.query(
            sql.toString(), 
            new BeanPropertyRowMapper<>(Orders.class), 
            params.toArray()
        );
    }
}