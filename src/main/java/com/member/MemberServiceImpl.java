package com.member;

import com.config.UrlConfig;
import com.dto.MemberDTO;
import com.dto.MemberSearchDTO;
import com.dto.StreamingData;
import com.mail.MailService;
import com.member.Member;
import com.member.MemberRepository;
import com.member.MemberSpecifications;
import com.utils.JwtUtil;
import com.utils.SVGSecurityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
    private MailService mailService;
	
	@Autowired
    private UrlConfig urlConfig;
	
	@Value("${app.frontend.url}")
	private String frontendUrl;
    
    @Autowired
    private JwtUtil jwtUtil;

	@Override
	public MemberDTO register(String memberName, String memberAccount, String memberEmail, String memberPassword) {
		if (memberRepository.existsByMemberAccount(memberAccount)) {
			throw new RuntimeException("帳號已存在");
		}
		
		if (memberRepository.existsByMemberEmail(memberEmail)) {
	        throw new RuntimeException("此Email已被註冊");
	    }

		Member member = new Member();
		member.setMemberName(memberName);
		member.setMemberAccount(memberAccount);
		member.setMemberEmail(memberEmail);
		member.setMemberPassword(memberPassword);
		member.setCreateTime(new Date());
		// 設置狀態為 0 表示未驗證
		member.setMemberStatus(0);

		// 生成驗證token並存入memberRegid
	    String token = jwtUtil.generateEmailVerificationToken(memberAccount);
	    member.setMemberRegid(token);
	    
	    // 儲存會員資料
	    member = memberRepository.save(member);
	    
	    // 發送驗證信
	    String verificationUrl = urlConfig.getFrontendUrl() + "/verification.html?token=" + token;
	    mailService.sendVerificationEmail(memberEmail, verificationUrl);

	    return MemberDTO.fromEntity(member);
	}

	@Override
	public MemberDTO login(String memberAccount, String memberPassword) {
        Member member = memberRepository.findByMemberAccount(memberAccount);
        
        if (member == null || !memberPassword.equals(member.getMemberPassword())) {
            throw new RuntimeException("使用者名稱或密碼不正確");
        }

        return MemberDTO.fromEntity(member);
    }

	@Override
	public void logout(String memberAccount) {
		// 登出邏輯由 Controller 的 Session 處理
	}
	
	@Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        if (memberRepository.existsByMemberAccount(memberDTO.getMemberAccount())) {
            throw new RuntimeException("帳號已存在");
        }
        
        if (memberRepository.existsByMemberEmail(memberDTO.getMemberEmail())) {
            throw new RuntimeException("此Email已被註冊");
        }

        Member member = new Member();
        updateMemberFields(member, memberDTO);
        member.setCreateTime(new Date());
        member.setMemberStatus(1);

        return MemberDTO.fromEntity(memberRepository.save(member));
    }

    @Override
    public MemberDTO updateMember(Integer memberNo, MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new EntityNotFoundException("會員不存在"));

        // 若前端沒傳入新密碼，就維持舊有密碼
        if (memberDTO.getMemberPassword() == null || memberDTO.getMemberPassword().isEmpty()) {
            memberDTO.setMemberPassword(member.getMemberPassword());
        }
        
        updateMemberFields(member, memberDTO);
        return MemberDTO.fromEntity(memberRepository.save(member));
    }

    @Override
    public MemberDTO getMember(Integer memberNo) {
        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new EntityNotFoundException("會員不存在"));
        return MemberDTO.fromEntity(member);
    }

    @Override
    public void deleteMember(Integer memberNo) {
        if (!memberRepository.existsById(memberNo)) {
            throw new EntityNotFoundException("會員不存在");
        }
        memberRepository.deleteById(memberNo);
    }

    /*以使用SearchDTO，故刪除避免衝突
    @Override
    public List<MemberDTO> findMembersByName(String name) {
        return memberRepository.findByMemberNameContaining(name).stream()
            .map(MemberDTO::fromEntity)
            .collect(Collectors.toList());
    }
    */

    @Override
    public List<MemberDTO> searchMembers(MemberSearchDTO searchDTO) {
        List<Member> members = memberRepository.findAll(
            MemberSpecifications.withSearchCriteria(searchDTO)
        );
        return members.stream()
                .map(MemberDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MemberDTO> searchMembersWithPagination(MemberSearchDTO searchDTO, 
                                                      Pageable pageable) {
        Page<Member> membersPage = memberRepository.findAll(
            MemberSpecifications.withSearchCriteria(searchDTO),
            pageable
        );
        return membersPage.map(MemberDTO::fromEntity);
    }
    
    @Override
    public void updateMemberPhoto(Integer memberNo, MultipartFile photo) {
        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new EntityNotFoundException("會員不存在"));

        try {
            String contentType = photo.getContentType();
            // 檢查檔案類型
            if (contentType == null || !isValidImageType(contentType)) {
                throw new RuntimeException("不支援的圖片格式");
            }
            
            // SVG檔案的安全性檢查
            if ("image/svg+xml".equals(contentType)) {
                if (!SVGSecurityValidator.validateSVG(photo.getInputStream())) {
                    throw new RuntimeException("不安全的SVG檔案");
                }
            }
            
            // 建立 StreamingData 進行驗證
            StreamingData streamingData = new StreamingData(
                photo.getInputStream(),
                photo.getSize(),
                photo.getOriginalFilename(),
                contentType
            );
            
            if (!streamingData.isValidImageType()) {
                throw new RuntimeException("不支援的圖片格式");
            }
            
            member.setMemberPhoto(photo.getBytes());
            memberRepository.save(member);
        } catch (IOException e) {
            throw new RuntimeException("處理照片時發生錯誤", e);
        }
    }

    @Override
    public StreamingData getMemberPhoto(Integer memberNo) {
        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new EntityNotFoundException("會員不存在"));
        
        if (member.getMemberPhoto() == null) {
            return null;
        }
        
        try {
            // 嘗試判斷圖片類型
            String contentType = determineImageContentType(member.getMemberPhoto());
            
            return new StreamingData(
                new ByteArrayInputStream(member.getMemberPhoto()),
                member.getMemberPhoto().length,
                contentType
            );
        } catch (IOException e) {
            throw new RuntimeException("處理照片時發生錯誤", e);
        }
    }

    /**
     * 驗證是否為支援的圖片類型
     */
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/bmp") ||
            contentType.equals("image/svg+xml")
        );
    }

    /**
     * 嘗試判斷圖片類型（基於檔案頭部特徵）
     */
    private String determineImageContentType(byte[] imageData) throws IOException {
        if (imageData == null || imageData.length < 8) {
            return "image/jpeg"; // 預設類型
        }

        // 檢查檔案頭部特徵
        if (imageData[0] == (byte)0xFF && imageData[1] == (byte)0xD8) {
            return "image/jpeg";
        } else if (imageData[0] == (byte)0x89 && imageData[1] == (byte)0x50) {
            return "image/png";
        } else if (imageData[0] == (byte)0x47 && imageData[1] == (byte)0x49) {
            return "image/gif";
        } else if (new String(imageData, 0, 5).equals("<?xml") || 
                  new String(imageData, 0, 4).equals("<svg")) {
            return "image/svg+xml";
        } else {
            return "image/jpeg"; // 預設類型
        }
    }
    
 // 私有輔助方法：更新會員資料
    private void updateMemberFields(Member member, MemberDTO memberDTO) {
        member.setMemberName(memberDTO.getMemberName());
        member.setMemberAccount(memberDTO.getMemberAccount());
        member.setMemberEmail(memberDTO.getMemberEmail());
        member.setMemberPassword(memberDTO.getMemberPassword()); // 加入這行
        member.setMemberId(memberDTO.getMemberId());
        member.setMemberPhone(memberDTO.getMemberPhone());
        member.setMemberZip(memberDTO.getMemberZip());
        member.setMemberAddress(memberDTO.getMemberAddress());
        member.setMemberStatus(memberDTO.getMemberStatus());
        member.setCardNo(memberDTO.getCardNo());    // 加入信用卡相關欄位
        member.setCardYy(memberDTO.getCardYy());
        member.setCardMm(memberDTO.getCardMm());
        member.setCardId(memberDTO.getCardId());
        member.setMemberRegid(memberDTO.getMemberRegid()); // 恢復memberRegid的設置
    }
    
    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MemberDTO> getAllMembersWithPagination(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDTO::fromEntity);
    }
    
    @Override
    public void processForgotPassword(String account, String email) {
        Member member = memberRepository.findByMemberAccount(account);
        if (member == null || !member.getMemberEmail().equals(email)) {
            throw new RuntimeException("帳號或Email不正確");
        }

        String token = jwtUtil.generatePasswordResetToken(account);
        
     // 使用動態取得的 frontendUrl
        String resetUrl = urlConfig.getFrontendUrl() + "/reset-password.html?token=" + token;
        mailService.sendPasswordResetEmail(email, resetUrl);
    }

    @Override
    public boolean validateResetToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        if (!validateResetToken(token)) {
            throw new RuntimeException("無效或已過期的重設密碼連結");
        }

        String memberAccount = jwtUtil.getMemberAccountFromToken(token);
        Member member = memberRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new RuntimeException("找不到會員帳號");
        }
        
        member.setMemberPassword(newPassword);
        memberRepository.save(member);
    }
    
    @Override
    public boolean verifyEmail(String token) {
        try {
            // 驗證 token
            if (!jwtUtil.validateToken(token)) {
                return false;
            }
            
            // 取得會員帳號
            String memberAccount = jwtUtil.getMemberAccountFromToken(token);
            Member member = memberRepository.findByMemberAccount(memberAccount);
            
            if (member == null || !token.equals(member.getMemberRegid())) {
                return false;
            }
            
            // 如果已驗證過直接回傳true (狀態為1表示已驗證)
            if (member.getMemberStatus() == 1) {
                return true;
            }
            
            // 更新驗證狀態
            member.setMemberStatus(1); // 設置為已驗證
            member.setMemberRegid(null); // 清除驗證token
            memberRepository.save(member);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void resendVerificationEmail(String email) {
        Member member = memberRepository.findByMemberEmail(email);
        if (member == null) {
            throw new RuntimeException("找不到此Email的會員");
        }
        
        if (member.getMemberStatus() == 1) {
            throw new RuntimeException("此帳號已完成驗證");
        }
        
        // 生成新的驗證token
        String token = jwtUtil.generateEmailVerificationToken(member.getMemberAccount());
        member.setMemberRegid(token);
        memberRepository.save(member);
        
        // 重新發送驗證信  
        String verificationUrl = urlConfig.getFrontendUrl() + "/verification.html?token=" + token;
        mailService.sendVerificationEmail(member.getMemberEmail(), verificationUrl);
    }
}