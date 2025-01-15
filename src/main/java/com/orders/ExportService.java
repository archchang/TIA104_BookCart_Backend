package com.orders;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;

@Service
public interface ExportService {
    ByteArrayInputStream exportToExcel();
    ByteArrayInputStream exportToPdf();
    String getExcelContentType();
    String getPdfContentType();
    String getExcelFileName();
    String getPdfFileName();
}