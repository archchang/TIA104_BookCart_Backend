package com.shop;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import io.swagger.annotations.*;
import javax.servlet.http.HttpSession;
import java.util.Vector;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.shop.Cart;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    /**
     * 新增商品到購物車
     * @param cart 購物車項目
     * @param session HTTP Session
     * @return 購物車狀態
     */
	
	
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
    	
    		@RequestBody Cart cart, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist == null) {
            buylist = new Vector<Cart>();
            buylist.add(cart);
        } else {
            if (buylist.contains(cart)) {
                Cart existingCart = buylist.get(buylist.indexOf(cart));
                existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            } else {
                buylist.add(cart);
            }
        }
        
        // 計算購物車狀態
        Map<String, Object> status = calculateCartStatus(buylist);
        session.setAttribute("shoppingcart", buylist);
        
        return ResponseEntity.ok(status);
    }

    /**
     * 增加購物車中特定商品的數量
     * @param index 商品索引
     * @param session HTTP Session
     * @return 更新後的購物車狀態
     */
	
    @PutMapping("/quantity/increase/{index}")
    public ResponseEntity<Map<String, Object>> increaseQuantity(
    		
    		@PathVariable int index, HttpSession session) {
        
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            Cart cart = buylist.get(index);
            cart.setQuantity(cart.getQuantity() + 1);
            return ResponseEntity.ok(calculateCartStatus(buylist));
        }
        
        return ResponseEntity.badRequest().build();
    }

    /**
     * 減少購物車中特定商品的數量
     * @param index 商品索引
     * @param session HTTP Session
     * @return 更新後的購物車狀態
     */
	
	
    @PutMapping("/quantity/decrease/{index}")
    public ResponseEntity<Map<String, Object>> decreaseQuantity(
    		
    		@PathVariable int index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            Cart cart = buylist.get(index);
            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
                return ResponseEntity.ok(calculateCartStatus(buylist));
            }
        }
        
        return ResponseEntity.badRequest().build();
    }

    /**
     * 刪除購物車中的特定商品
     * @param index 商品索引
     * @param session HTTP Session
     * @return 更新後的購物車狀態
     */
	
	
    @DeleteMapping("/{index}")
    public ResponseEntity<Map<String, Object>> deleteFromCart(
    		
    		@PathVariable int index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            buylist.remove(index);
            return ResponseEntity.ok(calculateCartStatus(buylist));
        }
        
        return ResponseEntity.badRequest().build();
    }

    /**
     * 取得購物車結帳金額
     * @param session HTTP Session
     * @return 購物車結帳資訊
     */
	
    @GetMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist == null || buylist.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        int totalAmount = calculateTotalAmount(buylist);
        Map<String, Object> response = new HashMap<>();
        response.put("amount", totalAmount);
        response.put("items", buylist);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 取得當前購物車內容
     * @param session HTTP Session
     * @return 購物車內容和狀態
     */
	
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist == null) {
            return ResponseEntity.ok(calculateCartStatus(new Vector<Cart>()));
        }
        
        return ResponseEntity.ok(calculateCartStatus(buylist));
    }

    /**
     * 計算購物車狀態（商品種類數和總件數）
     * @param buylist 購物車清單
     * @return 購物車狀態Map
     */
    private Map<String, Object> calculateCartStatus(List<Cart> buylist) {
        Map<String, Object> status = new HashMap<>();
        int kinds = buylist.size();
        int pcs = 0;
        
        for (Cart cart : buylist) {
            pcs += cart.getQuantity();
        }
        
        status.put("kinds", kinds);
        status.put("pcs", pcs);
        status.put("items", buylist);
        
        return status;
    }

    /**
     * 計算購物車總金額
     * @param buylist 購物車清單
     * @return 總金額
     */
    private int calculateTotalAmount(List<Cart> buylist) {
        int totalAmount = 0;
        for (Cart item : buylist) {
            totalAmount += item.getProduct_price() * item.getQuantity();
        }
        return totalAmount;
    }
    
    /**
     * 清空購物車
     * @param session HTTP Session
     * @return 清空後的購物車狀態
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        session.removeAttribute("shoppingcart");
        return ResponseEntity.ok(calculateCartStatus(new Vector<Cart>()));
    }
}