package com.dto;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StreamingData {
    // 支援的圖片類型列舉
    public enum ImageType {
        JPEG("image/jpeg", Arrays.asList(".jpg", ".jpeg")),
        PNG("image/png", Arrays.asList(".png")),
        GIF("image/gif", Arrays.asList(".gif")),
        WEBP("image/webp", Arrays.asList(".webp")),
        BMP("image/bmp", Arrays.asList(".bmp")),
        SVG("image/svg+xml", Arrays.asList(".svg"));

        private final String mimeType;
        private final List<String> extensions;

        ImageType(String mimeType, List<String> extensions) {
            this.mimeType = mimeType;
            this.extensions = extensions;
        }

        public String getMimeType() {
            return mimeType;
        }

        public List<String> getExtensions() {
            return extensions;
        }

        public static Optional<ImageType> fromMimeType(String mimeType) {
            return Arrays.stream(values())
                    .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                    .findFirst();
        }

        public static Optional<ImageType> fromExtension(String filename) {
            String ext = getExtension(filename).toLowerCase();
            return Arrays.stream(values())
                    .filter(type -> type.getExtensions().contains(ext))
                    .findFirst();
        }

        private static String getExtension(String filename) {
            int lastDotIndex = filename.lastIndexOf(".");
            return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
        }
    }

    private final InputStream inputStream;
    private final long contentLength;
    private final String contentType;
    private final ImageType imageType;

    public StreamingData(InputStream inputStream, long contentLength, String contentType) {
        this.inputStream = inputStream;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.imageType = ImageType.fromMimeType(contentType)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported image type: " + contentType));
    }

    // 新增建構子，可從檔名判斷圖片類型
    public StreamingData(InputStream inputStream, long contentLength, String filename, String contentType) {
        this.inputStream = inputStream;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.imageType = ImageType.fromExtension(filename)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported image extension: " + filename));
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public boolean isValidImageType() {
        return imageType != null;
    }

    // 檢查是否為向量圖
    public boolean isVectorImage() {
        return imageType == ImageType.SVG;
    }

    // 檢查是否為點陣圖
    public boolean isRasterImage() {
        return imageType != null && imageType != ImageType.SVG;
    }
}

