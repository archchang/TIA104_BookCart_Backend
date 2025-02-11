package com.shop;


import com.shop.Cart;
import javax.servlet.http.HttpSession;
import java.util.Map;

public interface CartService {
    /**
     * 新增商品到購物車
     */
    Map<String, Object> addToCart(Cart cart, HttpSession session);
    
    /**
     * 增加購物車商品數量
     */
    Map<String, Object> increaseQuantity(int index, HttpSession session);
    
    /**
     * 減少購物車商品數量
     */
    Map<String, Object> decreaseQuantity(int index, HttpSession session);
    
    /**
     * 刪除購物車商品
     */
    Map<String, Object> deleteFromCart(int index, HttpSession session);
    
    /**
     * 取得購物車內容
     */
    Map<String, Object> getCart(HttpSession session);
    
    /**
     * 結帳金額計算
     */
    Map<String, Object> checkout(HttpSession session);
    
    /**
     * 清空購物車
     */
    Map<String, Object> clearCart(HttpSession session);
}