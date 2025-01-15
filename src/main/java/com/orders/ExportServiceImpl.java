package com.orders;

import com.orders.ExportService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ExportServiceImpl implements ExportService {
    
    private final OrdersService ordersService;
    
    @Autowired
    public ExportServiceImpl(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @Override
    public ByteArrayInputStream exportToExcel() {
        return ordersService.exportToExcel();
    }

    @Override
    public ByteArrayInputStream exportToPdf() {
        return ordersService.exportToPdf();
    }

    @Override
    public String getExcelContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getPdfContentType() {
        return "application/pdf";
    }

    @Override
    public String getExcelFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return "orders_report_" + dateFormat.format(new Date()) + ".xlsx";
    }

    @Override
    public String getPdfFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return "orders_report_" + dateFormat.format(new Date()) + ".pdf";
    }
}