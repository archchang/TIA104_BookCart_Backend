package com.orders;

import com.orders.Orders;
import com.shop.Cart;

import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface OrdersService {
	// 創建新訂單
    Orders createOrder(Orders order, List<Cart> cartList);
    
    // 取得所有訂單
    List<Orders> getAllOrders();
    
    // 取得會員訂單
    List<Orders> getMemberOrders(Integer memberNo);
    
    // 取得訂單明細
    List<Map<String, Object>> getOrderDetails(Integer ordersNo);
    
 // 新增：複合條件查詢訂單
    List<Orders> searchOrders(Integer ordersNo, Integer memberNo, Date startDate, Date endDate);
    
 // 導出報表相關方法
    ByteArrayInputStream exportToExcel();
    ByteArrayInputStream exportToPdf();
}