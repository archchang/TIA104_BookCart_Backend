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
	
	private final CartService cartService;
	
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
    		@RequestBody Cart cart, HttpSession session) {
        return ResponseEntity.ok(cartService.addToCart(cart, session));
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
        return ResponseEntity.ok(cartService.increaseQuantity(index, session));
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
        return ResponseEntity.ok(cartService.decreaseQuantity(index, session));
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
        return ResponseEntity.ok(cartService.deleteFromCart(index, session));
    }
    
    /**
     * 取得當前購物車內容
     * @param session HTTP Session
     * @return 購物車內容和狀態
     */
	
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        return ResponseEntity.ok(cartService.getCart(session));
    }

    /**
     * 取得購物車結帳金額
     * @param session HTTP Session
     * @return 購物車結帳資訊
     */
	
    @GetMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(HttpSession session) {
        return ResponseEntity.ok(cartService.checkout(session));
    }

    
    /**
     * 清空購物車
     * @param session HTTP Session
     * @return 清空後的購物車狀態
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        
        return ResponseEntity.ok(cartService.clearCart(session));
    }
}