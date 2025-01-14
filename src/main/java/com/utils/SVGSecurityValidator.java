package com.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class SVGSecurityValidator {
    
    // 允許的 SVG 元素列表
    private static final Set<String> ALLOWED_ELEMENTS = new HashSet<>(Arrays.asList(
        "svg", "path", "rect", "circle", "ellipse", "line", "polyline", "polygon",
        "text", "g", "defs", "title", "desc", "image", "use", "symbol",
        "linearGradient", "radialGradient", "stop", "pattern", "clipPath", "mask",
        "filter", "feGaussianBlur", "feOffset", "feBlend", "feComposite"
    ));

    // 允許的屬性列表
    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(
        "id", "class", "width", "height", "x", "y", "x1", "y1", "x2", "y2",
        "cx", "cy", "r", "rx", "ry", "fill", "stroke", "stroke-width",
        "stroke-linecap", "stroke-linejoin", "opacity", "transform",
        "d", "points", "viewBox", "preserveAspectRatio", "xmlns",
        "style", "text-anchor", "font-family", "font-size", "font-weight",
        "text-decoration", "clip-path", "mask", "filter"
    ));

    // 不允許的 URL scheme
    private static final Set<String> BLOCKED_URL_SCHEMES = new HashSet<>(Arrays.asList(
        "javascript", "data", "vbscript", "mailto", "tel", "file", "ftp",
        "blob", "about", "chrome", "mocha"
    ));

    /**
     * 驗證 SVG 檔案是否安全
     */
    public static boolean validateSVG(InputStream input) {
        try {
            // 設置 XML 解析器的安全選項
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            configureSecurity(dbf);
            
            // 解析 SVG
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(input);
            
            // 檢查根元素
            Element root = doc.getDocumentElement();
            if (!"svg".equals(root.getTagName())) {
                return false;
            }
            
            // 遞迴檢查所有元素
            return validateElement(root);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 設置 XML 解析器的安全選項
     */
    private static void configureSecurity(DocumentBuilderFactory dbf) throws Exception {
        // 停用外部實體處理
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
    }

    /**
     * 遞迴驗證元素及其子元素
     */
    private static boolean validateElement(Element element) {
        // 檢查元素名稱
        String tagName = element.getTagName().toLowerCase();
        if (!ALLOWED_ELEMENTS.contains(tagName)) {
            return false;
        }
        
        // 檢查所有屬性
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            Node attr = element.getAttributes().item(i);
            String attrName = attr.getNodeName().toLowerCase();
            String attrValue = attr.getNodeValue();
            
            // 檢查屬性名稱
            if (!ALLOWED_ATTRIBUTES.contains(attrName)) {
                return false;
            }
            
            // 檢查 URL
            if (attrValue != null && (attrValue.contains(":") || attrValue.contains("\\"))) {
                if (!validateUrl(attrValue)) {
                    return false;
                }
            }
            
            // 檢查 style 屬性
            if ("style".equals(attrName)) {
                if (!validateStyle(attrValue)) {
                    return false;
                }
            }
        }
        
        // 遞迴檢查子元素
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (!validateElement((Element) child)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 驗證 URL 是否安全
     */
    private static boolean validateUrl(String url) {
        url = url.trim().toLowerCase();
        
        // 檢查是否包含不安全的 scheme
        for (String scheme : BLOCKED_URL_SCHEMES) {
            if (url.startsWith(scheme + ":")) {
                return false;
            }
        }
        
        // 檢查相對路徑
        if (url.startsWith("//")) {
            return false;
        }
        
        return true;
    }

    /**
     * 驗證 style 屬性是否安全
     */
    private static boolean validateStyle(String style) {
        if (style == null) {
            return true;
        }
        
        style = style.toLowerCase();
        
        // 檢查是否包含 expression
        if (style.contains("expression") || style.contains("javascript")) {
            return false;
        }
        
        // 檢查是否包含 url()
        if (style.contains("url(")) {
            int start = style.indexOf("url(");
            int end = style.indexOf(")", start);
            if (end > start) {
                String url = style.substring(start + 4, end).trim();
                url = url.replaceAll("[\"']", "");
                return validateUrl(url);
            }
            return false;
        }
        
        return true;
    }
}