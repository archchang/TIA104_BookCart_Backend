package com.orders;

import com.orders.OrdersDao;
import com.orders.Orders;
import com.shop.Cart;
import com.mail.MailService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.sql.Date;
import java.util.HashMap;
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
    
    @Override
    public List<Orders> searchOrders(Integer ordersNo, Integer memberNo, Date startDate, Date endDate) {
        // 這裡可視需要做商業邏輯處理，若單純查詢可直接呼叫 Dao
        return ordersDao.searchOrders(ordersNo, memberNo, startDate, endDate);
    }
    
    @Override
    public ByteArrayInputStream exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("訂單報表");
            
            // 創建標題列樣式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // 設定標題列
            Row headerRow = sheet.createRow(0);
            String[] columns = {"訂單編號", "會員編號", "收件人", "電話", "地址", 
                              "訂單金額", "訂單日期", "訂單狀態"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 取得訂單資料
            List<Orders> ordersList = getAllOrders();
            
            // 填充數據
            int rowNum = 1;
            for (Orders order : ordersList) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(order.getOrders_no());
                row.createCell(1).setCellValue(order.getMember_no());
                row.createCell(2).setCellValue(order.getOrders_receiver());
                row.createCell(3).setCellValue(order.getReceiver_phone());
                row.createCell(4).setCellValue(order.getReceiver_address());
                row.createCell(5).setCellValue(order.getOrders_total());
                row.createCell(6).setCellValue(order.getOrders_date().toString());
                row.createCell(7).setCellValue(getOrderStatusText(order.getOrders_status()));
            }
            
            // 調整欄寬
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
            
        } catch (IOException e) {
            throw new ExportException("匯出Excel失敗", e);
        }
    }
    
    @Override
    public ByteArrayInputStream exportToPdf() {
    	Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // 使用 itext-asian 的內建中文字型
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            
            // 設定字型大小與樣式
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font contentFont = new Font(baseFont, 12, Font.NORMAL);
            
            // 添加標題
            Paragraph title = new Paragraph("訂單報表", titleFont);
            title.setSpacingAfter(20f); // 設定標題後的間距
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // 創建表格
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            
            // 設定欄位寬度比例
            float[] columnWidths = new float[]{1, 1, 1, 1, 2, 1, 1, 1};
            table.setWidths(columnWidths);
            
            // 添加表頭
            String[] headers = {"訂單編號", "會員編號", "收件人", "電話", 
                              "地址", "訂單金額", "訂單日期", "訂單狀態"};
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                cell.setMinimumHeight(25);
                table.addCell(cell);
            }
            
            // 填充數據
            List<Orders> ordersList = getAllOrders();
            for (Orders order : ordersList) {
                addCell(table, String.valueOf(order.getOrders_no()), contentFont);
                addCell(table, String.valueOf(order.getMember_no()), contentFont);
                addCell(table, order.getOrders_receiver(), contentFont);
                addCell(table, order.getReceiver_phone(), contentFont);
                addCell(table, order.getReceiver_address(), contentFont);
                addCell(table, String.valueOf(order.getOrders_total()), contentFont);
                addCell(table, order.getOrders_date().toString(), contentFont);
                addCell(table, getOrderStatusText(order.getOrders_status()), contentFont);
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayInputStream(outputStream.toByteArray());
            
        } catch (DocumentException | IOException e) {
            throw new ExportException("匯出PDF失敗", e);
        }
    }
    
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setMinimumHeight(20);
        table.addCell(cell);
    }
    
    private String getOrderStatusText(Integer status) {
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "待處理");
        statusMap.put(2, "處理中");
        statusMap.put(3, "已出貨");
        statusMap.put(4, "已送達");
        statusMap.put(5, "已取消");
        return statusMap.getOrDefault(status, "未知狀態");
    }
}
