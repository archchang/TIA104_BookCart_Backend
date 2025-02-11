package com.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.shop.Cart;

@Service
public class CartServiceImpl implements CartService {

	@Override
	public Map<String, Object> addToCart(Cart cart, HttpSession session) {
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
        session.setAttribute("shoppingcart", buylist);
        return calculateCartStatus(buylist);
	}
	
	@Override
	public Map<String, Object> increaseQuantity(int index, HttpSession session) {
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            Cart cart = buylist.get(index);
            cart.setQuantity(cart.getQuantity() + 1);
            return calculateCartStatus(buylist);
        }
        return calculateCartStatus(new Vector<Cart>());
	}
	
	@Override
	public Map<String, Object> decreaseQuantity(int index, HttpSession session) {
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            Cart cart = buylist.get(index);
            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
                return calculateCartStatus(buylist);
            }
        }
        return calculateCartStatus(new Vector<Cart>());
	}
	
	@Override
	public Map<String, Object> deleteFromCart(int index, HttpSession session) {
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist != null && index >= 0 && index < buylist.size()) {
            buylist.remove(index);
            return calculateCartStatus(buylist);
        }
        return calculateCartStatus(new Vector<Cart>());
	}
	
	@Override
	public Map<String, Object> getCart(HttpSession session) {
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist == null) {
            return calculateCartStatus(new Vector<Cart>());
        }
        return calculateCartStatus(buylist);
	}
	
	@Override
	public Map<String, Object> checkout(HttpSession session) {
		@SuppressWarnings("unchecked")
        List<Cart> buylist = (List<Cart>) session.getAttribute("shoppingcart");
        
        if (buylist == null || buylist.isEmpty()) {
            return calculateCartStatus(new Vector<Cart>());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("amount", calculateTotalAmount(buylist));
        response.put("items", buylist);
        
        return response;
	}
	
	@Override
	public Map<String, Object> clearCart(HttpSession session) {
		session.removeAttribute("shoppingcart");
		return calculateCartStatus(new Vector<Cart>());
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
}