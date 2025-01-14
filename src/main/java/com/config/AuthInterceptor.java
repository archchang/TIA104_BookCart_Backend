package com.config;

import com.dto.MemberDTO;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession();
        MemberDTO member = (MemberDTO) session.getAttribute("loggedInMember");

        // 檢查需要會員權限的頁面
        if ((path.contains("/checkout.html") || path.contains("/myorders.html")) && member == null) {
            response.sendRedirect("/login.html");
            return false;
        }

        // 檢查需要管理者權限的頁面
        if (path.contains("/admin/") && (member == null || member.getMemberStatus() != 2)) {
            response.sendRedirect("/login.html");
            return false;
        }

        return true;
    }
}