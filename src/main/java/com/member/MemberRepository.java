package com.member;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import com.member.Member;

public interface MemberRepository extends JpaRepository<Member, Integer>,
										JpaSpecificationExecutor<Member> {
    
	// 檢查會員帳號是否存在
	boolean existsByMemberAccount(String memberAccount);
	
	// 檢查Email是否存在
	boolean existsByMemberEmail(String memberEmail);
    
	// 根據會員帳號查找會員
	Member findByMemberAccount(String memberAccount);

	// 根據會員email找會員
	Member findByMemberEmail(String email);
	
	// 根據會員名稱模糊查詢
    /*避免衝突
	@Query("SELECT m FROM Member m WHERE m.memberName LIKE %:name%")
    List<Member> findByMemberNameContaining(@Param("name") String name);
    */
}