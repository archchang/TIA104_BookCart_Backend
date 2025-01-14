package com.orders;

import com.orders.OrdersDao;
import com.orders.Orders;
import com.shop.Cart;
import com.mail.MailService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


@Service
public class OrdersServiceImpl implements OrdersService {
    
    private final OrdersDao ordersDao;
//    private final JdbcTemplate jdbcTemplate;
    private final MailService mailService;
    private final HttpSession httpSession;
    
    public OrdersServiceImpl(OrdersDao ordersDao, 
                           //JdbcTemplate jdbcTemplate,
                           MailService mailService,
                           HttpSession httpSession) {
        this.ordersDao = ordersDao;
        //this.jdbcTemplate = jdbcTemplate;
        this.mailService = mailService;
        this.httpSession = httpSession;
    }

    @Override
    @Transactional
    public Orders createOrder(Orders order, List<Cart> cartList) {
        // 設置訂單狀態
        order.setOrders_status(1);
        
        // 儲存訂單主檔
        Orders savedOrder = ordersDao.save(order);
        
        // 儲存訂單明細
        if (cartList != null && !cartList.isEmpty()) {
            String detailsSql = "INSERT INTO orders_details (orders_no, product_no, quantity, product_price) " +
                               "VALUES (?, ?, ?, ?)";
                               
            for (Cart cart : cartList) {
            	ordersDao.saveOrderDetails(savedOrder.getOrders_no(), cart);
//                jdbcTemplate.update(detailsSql,
//                    savedOrder.getOrders_no(),
//                    cart.getProduct_no(),
//                    cart.getQuantity(),
//                    cart.getProduct_price()
//                );
            }
        }
        
        // 寄送訂單確認信
        String memberEmail = ordersDao.findMemberEmail(order.getMember_no());
//        String memberEmail = jdbcTemplate.queryForObject(
//            "SELECT member_email FROM member WHERE member_no = ?",
//            String.class,
//            order.getMember_no()
//        );
        
        if (memberEmail != null) {
            mailService.sendOrderConfirmationEmail(
                memberEmail,
                savedOrder.getOrders_no(),
                savedOrder.getOrders_total()
            );
        }
        
        httpSession.removeAttribute("shoppingcart");
        
        return savedOrder;
    }

    @Override
    public List<Orders> getAllOrders() {
        return ordersDao.findAll();
    }

    @Override
    public List<Orders> getMemberOrders(Integer memberNo) {
        return ordersDao.findByMemberNo(memberNo);
    }

    @Override
    public List<Map<String, Object>> getOrderDetails(Integer ordersNo) {
        return ordersDao.findDetailsByOrderNo(ordersNo);
    }
}
