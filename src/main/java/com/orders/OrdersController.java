package com.orders;

import com.orders.Orders;
import com.orders.OrdersService;
import com.orders.ExportService;
import com.shop.Cart;

import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;  // 添加這行
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    
    private final OrdersService ordersService;
    private final ExportService exportService;
    
    public OrdersController(OrdersService ordersService, ExportService exportService) {
        this.ordersService = ordersService;
        this.exportService = exportService;
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
    
    /**
     * 複合條件查詢訂單
     * @param ordersNo   (選填)訂單編號
     * @param memberNo   (選填)會員編號
     * @param startDate  (選填)起始日期 (格式：yyyy-MM-dd)
     * @param endDate    (選填)結束日期 (格式：yyyy-MM-dd)
     * @return 符合條件的訂單列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Orders>> searchOrders(
        @RequestParam(required = false) Integer ordersNo,
        @RequestParam(required = false) Integer memberNo,
        @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        // 若要用 java.sql.Date 存進 DB，可在此進行轉型
        Date sqlStartDate = (startDate == null) ? null : Date.valueOf(startDate);
        Date sqlEndDate = (endDate == null) ? null : Date.valueOf(endDate);

        List<Orders> result = ordersService.searchOrders(
            ordersNo, 
            memberNo, 
            sqlStartDate, 
            sqlEndDate
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel() {
        try {
            ByteArrayInputStream in = exportService.exportToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + 
                URLEncoder.encode(exportService.getExcelFileName(), StandardCharsets.UTF_8.toString()));
            headers.add("Content-Type", exportService.getExcelContentType());
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportPdf() {
        try {
            ByteArrayInputStream in = exportService.exportToPdf();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + 
                URLEncoder.encode(exportService.getPdfFileName(), StandardCharsets.UTF_8.toString()));
            headers.add("Content-Type", exportService.getPdfContentType());
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}