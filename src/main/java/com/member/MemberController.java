package com.member;

import com.dto.MemberDTO;
import com.dto.MemberSearchDTO;
import com.dto.StreamingData;
import com.member.MemberService;
import com.utils.SVGSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberRepository memberRepository;
    //註冊
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
    	try {
            validateRegisterRequest(request);
            
            MemberDTO member = memberService.register(
                request.get("memberName"),
                request.get("memberAccount"),
                request.get("memberEmail"),
                request.get("memberPassword")
            );
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    //登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
    	try {
            validateLoginRequest(request);
            
            MemberDTO member = memberService.login(
                request.get("memberAccount"),
                request.get("memberPassword")
            );
            
            // 將會員資訊存入 Session
            session.setAttribute("loggedInMember", member);
            
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //登出
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().body(Map.of("message", "登出成功"));
    }

    //新增會員
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO createdMember = memberService.createMember(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //更新會員
    @PutMapping("/{memberNo}")
    public ResponseEntity<MemberDTO> updateMember(
            @PathVariable Integer memberNo,
            @RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO updatedMember = memberService.updateMember(memberNo, memberDTO);
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //查詢會員
    @GetMapping("/{memberNo}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Integer memberNo) {
        try {
            MemberDTO member = memberService.getMember(memberNo);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    //刪除會員
    @DeleteMapping("/{memberNo}")
    public ResponseEntity<Void> deleteMember(@PathVariable Integer memberNo) {
        try {
            memberService.deleteMember(memberNo);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    //複合查詢會員資料
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembers(MemberSearchDTO searchDTO) {
        try {
            List<MemberDTO> members = memberService.searchMembers(searchDTO);
            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    //分頁複合查詢會員資料
    @GetMapping("/search/page")
    public ResponseEntity<Page<MemberDTO>> searchMembersWithPagination(
            MemberSearchDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "memberNo,desc") String[] sort) {
        
        try {
            List<Sort.Order> orders = new ArrayList<>();
            for (String sortParam : sort) {
                String[] parts = sortParam.split(",");
                orders.add(new Sort.Order(
                    parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? 
                        Sort.Direction.DESC : Sort.Direction.ASC,
                    parts[0]
                ));
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
            Page<MemberDTO> membersPage = memberService.searchMembersWithPagination(
                searchDTO, pageable);
            return ResponseEntity.ok(membersPage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    
    
    //上傳會員照片
    @PostMapping("/{memberNo}/photo")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Integer memberNo,
            @RequestParam("photo") MultipartFile photo) {
        
        // 基本驗證
        if (photo.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "請選擇要上傳的檔案"));
        }
        
        if (photo.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "檔案大小不能超過5MB"));
        }

        try {
            // 使用 StreamingData 處理上傳的檔案
            StreamingData streamingData = new StreamingData(
                photo.getInputStream(),
                photo.getSize(),
                photo.getOriginalFilename(),
                photo.getContentType()
            );

            // 驗證圖片類型
            if (!streamingData.isValidImageType()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "不支援的檔案格式，請上傳有效的圖片檔案"));
            }

            // 使用 SVGSecurityValidator 進行 SVG 安全性檢查
            if (StreamingData.ImageType.SVG.getMimeType().equals(streamingData.getContentType())) {
                if (!SVGSecurityValidator.validateSVG(photo.getInputStream())) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "SVG檔案包含不安全的內容"));
                }
            }

            memberService.updateMemberPhoto(memberNo, photo);
            return ResponseEntity.ok()
                .body(Map.of("message", "照片上傳成功"));
                
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "處理檔案時發生錯誤"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    
    //取得會員照片
    @GetMapping("/{memberNo}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Integer memberNo) {
        try {
            StreamingData photoData = memberService.getMemberPhoto(memberNo);
            if (photoData == null) {
                return ResponseEntity.notFound().build();
            }

            // 設置 HTTP 標頭
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(photoData.getContentType()));
            headers.setContentLength(photoData.getContentLength());
            headers.setCacheControl("max-age=3600");
            
            // 取得圖片資料
            byte[] imageData = photoData.getInputStream().readAllBytes();
            
            // 處理可能的 SVG 檔案
            if (StreamingData.ImageType.SVG.getMimeType().equals(photoData.getContentType())) {
                InputStream svgStream = new ByteArrayInputStream(imageData);
                if (!SVGSecurityValidator.validateSVG(svgStream)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
                }
            }

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    //驗證註冊請求參數
    private void validateRegisterRequest(Map<String, String> request) {
        List<String> errors = new ArrayList<>();
        
        // 驗證姓名
        if (isEmpty(request.get("memberName"))) {
            errors.add("姓名不能為空");
        }
        
        // 驗證帳號
        String memberAccount = request.get("memberAccount");
        if (isEmpty(memberAccount)) {
            errors.add("帳號不能為空");
        } else {
            // 檢查帳號是否已存在
            if (memberRepository.existsByMemberAccount(memberAccount)) {
                errors.add("此帳號已被註冊");
            }
        }
        
        // 驗證Email
        String memberEmail = request.get("memberEmail");
        if (memberEmail == null || !memberEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("Email格式不正確");
        } else {
            // 檢查Email是否已存在
            if (memberRepository.existsByMemberEmail(memberEmail)) {
                errors.add("此Email已被註冊");
            }
        }
        
        // 驗證密碼
        String password = request.get("memberPassword");
        if (password == null ||
            !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[\\x21-\\x7E]{8,}$")) {
            errors.add("密碼至少需要8個字符，包含至少1個大寫字母、1個小寫字母和1個數字（可選擇使用特殊符號）");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
    
    //驗證登入請求參數
    private void validateLoginRequest(Map<String, String> request) {
        List<String> errors = new ArrayList<>();
        
        if (isEmpty(request.get("memberAccount"))) {
            errors.add("帳號不能為空");
        }
        
        if (isEmpty(request.get("memberPassword"))) {
            errors.add("密碼不能為空");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    //檢查字串是否為空
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
 // 取得所有會員資料
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        try {
            List<MemberDTO> members = memberService.getAllMembers();
            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 取得所有會員資料(分頁)
    @GetMapping("/page")
    public ResponseEntity<Page<MemberDTO>> getAllMembersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "memberNo,desc") String[] sort) {
        
        try {
            List<Sort.Order> orders = new ArrayList<>();
            for (String sortParam : sort) {
                String[] parts = sortParam.split(",");
                orders.add(new Sort.Order(
                    parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? 
                        Sort.Direction.DESC : Sort.Direction.ASC,
                    parts[0]
                ));
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
            Page<MemberDTO> membersPage = memberService.getAllMembersWithPagination(pageable);
            return ResponseEntity.ok(membersPage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String account = request.get("account");
            String email = request.get("email");
            
            if (isEmpty(account) || isEmpty(email)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, 
                                "message", "帳號和Email不能為空"));
            }
            
            memberService.processForgotPassword(account, email);
            return ResponseEntity.ok()
                .body(Map.of("success", true, 
                            "message", "重設密碼連結已發送至您的信箱"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, 
                            "message", e.getMessage()));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = memberService.validateResetToken(token);
        return ResponseEntity.ok()
            .body(Map.of("valid", isValid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");
            
            if (isEmpty(token) || isEmpty(newPassword)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, 
                                "message", "Token和新密碼不能為空"));
            }
            
            memberService.resetPassword(token, newPassword);
            return ResponseEntity.ok()
                .body(Map.of("success", true, 
                            "message", "密碼重設成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, 
                            "message", e.getMessage()));
        }
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean verified = memberService.verifyEmail(token);
        return ResponseEntity.ok()
            .body(Map.of("verified", verified));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (isEmpty(email)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email不能為空"));
            }
            
            memberService.resendVerificationEmail(email);
            return ResponseEntity.ok()
                .body(Map.of("message", "驗證信已重新發送"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }
}