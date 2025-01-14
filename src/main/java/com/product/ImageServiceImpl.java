package com.product;

import com.dto.StreamingData;
import com.product.ImageService;
import com.utils.SVGSecurityValidator;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public byte[] processImageUpload(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        // 使用StreamingData驗證圖片類型
        try (InputStream inputStream = file.getInputStream()) {
            StreamingData streamingData = new StreamingData(
                inputStream,
                file.getSize(),
                fileName,
                contentType
            );
            
            // 如果是SVG，進行安全性驗證
            if (streamingData.isVectorImage()) {
                if (!SVGSecurityValidator.validateSVG(new ByteArrayInputStream(file.getBytes()))) {
                    throw new IllegalArgumentException("不安全的SVG文件");
                }
            }
            
            return file.getBytes();
        }
    }

    @Override
    public StreamingData validateAndGetImageStream(byte[] imageData, String fileName) throws IOException {
        String contentType = determineContentType(fileName);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        
        return new StreamingData(inputStream, imageData.length, fileName, contentType);
    }

    @Override
    public Resource getImageAsResource(byte[] imageData, String contentType) throws IOException {
        return new ByteArrayResource(imageData);
    }
    
    // 根據檔名判斷ContentType
    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".svg" -> "image/svg+xml";
            case ".webp" -> "image/webp";
            case ".bmp" -> "image/bmp";
            default -> "application/octet-stream";
        };
    }
}