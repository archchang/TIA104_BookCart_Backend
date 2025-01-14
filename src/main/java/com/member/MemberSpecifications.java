package com.member;

import com.member.Member;
import com.dto.MemberSearchDTO;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MemberSpecifications {
	
	public static Specification<Member> withSearchCriteria(MemberSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 會員名稱模糊查詢
            if (searchDTO.getMemberName() != null && !searchDTO.getMemberName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("memberName"),
                    "%" + searchDTO.getMemberName() + "%"
                ));
            }
            
            // 會員帳號精確查詢
            if (searchDTO.getMemberAccount() != null && !searchDTO.getMemberAccount().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("memberAccount"),
                    searchDTO.getMemberAccount()
                ));
            }
            
            // 會員電話模糊查詢
            if (searchDTO.getMemberPhone() != null && !searchDTO.getMemberPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("memberPhone"),
                    "%" + searchDTO.getMemberPhone() + "%"
                ));
            }
            
            // Email模糊查詢
            if (searchDTO.getMemberEmail() != null && !searchDTO.getMemberEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("memberEmail"),
                    "%" + searchDTO.getMemberEmail() + "%"
                ));
            }
            
            // 地址模糊查詢
            if (searchDTO.getMemberAddress() != null && !searchDTO.getMemberAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("memberAddress"),
                    "%" + searchDTO.getMemberAddress() + "%"
                ));
            }
            
            // 會員狀態精確查詢
            if (searchDTO.getMemberStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("memberStatus"),
                    searchDTO.getMemberStatus()
                ));
            }
            
            // 建立時間區間查詢
            if (searchDTO.getCreateTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createTime"),
                    searchDTO.getCreateTimeStart()
                ));
            }
            if (searchDTO.getCreateTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createTime"),
                    searchDTO.getCreateTimeEnd()
                ));
            }
            
            // 組合所有查詢條件
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}