package com.member;

import com.dto.MemberDTO;
import com.dto.MemberSearchDTO;
import com.dto.StreamingData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MemberService {
	// 註冊會員
    MemberDTO register(String memberName, String memberAccount, 
                      String memberEmail, String memberPassword);
    
    // 會員登入
    MemberDTO login(String memberAccount, String memberPassword);
    
    // 會員登出
    void logout(String memberAccount);
    
    // 新增會員
    MemberDTO createMember(MemberDTO memberDTO);
    
    // 更新會員資料
    MemberDTO updateMember(Integer memberNo, MemberDTO memberDTO);
    
    // 查詢會員資料
    MemberDTO getMember(Integer memberNo);
    
    // 刪除會員資料
    void deleteMember(Integer memberNo);
    
    // 根據會員名稱模糊查詢
    /*註解避免衝突
    List<MemberDTO> findMembersByName(String name);
    */
    // 複合查詢
    List<MemberDTO> searchMembers(MemberSearchDTO searchDTO);
    
    // 分頁複合查詢
    Page<MemberDTO> searchMembersWithPagination(MemberSearchDTO searchDTO, Pageable pageable);
    
    // 上傳會員照片
    void updateMemberPhoto(Integer memberNo, MultipartFile photo);
    
    // 取得會員照片
    StreamingData getMemberPhoto(Integer memberNo);
    
 // 取得所有會員資料
    List<MemberDTO> getAllMembers();
    
    // 取得所有會員資料(分頁)
    Page<MemberDTO> getAllMembersWithPagination(Pageable pageable);
    
 // 處理忘記密碼請求
    void processForgotPassword(String account, String email);
    
    // 驗證重設密碼Token
    boolean validateResetToken(String token);
    
    // 重設密碼
    void resetPassword(String token, String newPassword);
    
 // 驗證註冊信箱
    boolean verifyEmail(String token);

    // 重新發送驗證信
    void resendVerificationEmail(String email);
    
    
}
