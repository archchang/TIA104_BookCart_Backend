package com.orders;

import com.orders.Orders;
import com.shop.Cart;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface OrdersDao {
	// 新增訂單
    Orders save(Orders order);
    
    // 取得所有訂單
    List<Orders> findAll();
    
    // 依會員編號查詢訂單
    List<Orders> findByMemberNo(Integer memberNo);
    
    // 依訂單編號查詢訂單明細
    List<Map<String, Object>> findDetailsByOrderNo(Integer ordersNo);
    
    void saveOrderDetails(Integer ordersNo, Cart cart);  // 儲存訂單明細
    
    String findMemberEmail(Integer memberNo);  // 查詢會員email
    
    // 新增：複合條件查詢訂單
    List<Orders> searchOrders(Integer ordersNo, Integer memberNo, Date startDate, Date endDate);
}