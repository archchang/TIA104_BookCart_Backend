package com.orders;

import com.orders.Orders;
import com.orders.OrdersService;
import com.shop.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    
    private final OrdersService ordersService;
    
    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }
    
    // 新增訂單
    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody Map<String, Object> request) {
    	try {
            Orders order = new Orders();
            order.setMember_no((Integer) request.get("memberNo"));
            order.setOrders_receiver((String) request.get("ordersReceiver"));
            order.setReceiver_phone((String) request.get("receiverPhone"));
            order.setReceiver_zip((String) request.get("receiverZip"));
            order.setReceiver_address((String) request.get("receiverAddress"));
            order.setOrders_total((Integer) request.get("ordersTotal"));
            order.setOrders_date(java.sql.Date.valueOf((String) request.get("ordersDate")));
            
            // 正確處理 cartlist 的轉換
            List<Cart> cartList = new ArrayList<>();
            if (request.containsKey("cartlist")) {
                List<Map<String, Object>> cartMapList = (List<Map<String, Object>>) request.get("cartlist");
                for (Map<String, Object> cartMap : cartMapList) {
                    Cart cart = new Cart();
                    cart.setProduct_no((Integer) cartMap.get("product_no"));
                    cart.setProduct_name((String) cartMap.get("product_name"));
                    cart.setProduct_price((Integer) cartMap.get("product_price"));
                    cart.setQuantity((Integer) cartMap.get("quantity"));
                    cartList.add(cart);
                }
            }
            
            return ResponseEntity.ok(ordersService.createOrder(order, cartList));
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("訂單建立失敗", e);
        }
    }
    
    // 查詢訂單明細
    @GetMapping("/{ordersNo}/details")
    public ResponseEntity<List<Map<String, Object>>> getOrderDetails(@PathVariable Integer ordersNo) {
        return ResponseEntity.ok(ordersService.getOrderDetails(ordersNo));
    }
    
    // 查詢所有訂單
    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }
    
    // 查詢特定會員的所有訂單
    @GetMapping("/member/{memberNo}")
    public ResponseEntity<List<Orders>> getMemberOrders(@PathVariable Integer memberNo) {
        return ResponseEntity.ok(ordersService.getMemberOrders(memberNo));
    }
}