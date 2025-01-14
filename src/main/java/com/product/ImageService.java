package com.product;

import com.dto.StreamingData;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    // 處理圖片上傳
    byte[] processImageUpload(MultipartFile file) throws IOException;
    
    // 驗證圖片並轉換為串流
    StreamingData validateAndGetImageStream(byte[] imageData, String fileName) throws IOException;
    
    // 將圖片轉換為Resource
    Resource getImageAsResource(byte[] imageData, String contentType) throws IOException;
}