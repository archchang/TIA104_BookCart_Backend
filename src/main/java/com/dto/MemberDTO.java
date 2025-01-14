package com.dto;

import com.member.Member;
import java.util.Date;

public class MemberDTO {
	private Integer memberNo;
	private String memberAccount;
	private String memberPassword;
	private String memberName;
	private String memberId;
	private String memberPhone;
	private String memberZip;
	private String memberAddress;
	private String memberEmail;
	private Integer memberStatus;
	private Date createTime;

	// 新增的屬性
	private String cardNo;
	private String cardYy;
	private String cardMm;
	private String cardId;
	private String memberRegid;

	public Integer getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(Integer memberNo) {
		this.memberNo = memberNo;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}

	public String getMemberPassword() {
		return memberPassword;
	}

	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}

	
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public String getMemberZip() {
		return memberZip;
	}

	public void setMemberZip(String memberZip) {
		this.memberZip = memberZip;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public Integer getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(Integer memberStatus) {
		this.memberStatus = memberStatus;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	// 新增的 Getters 和 Setters

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardYy() {
        return cardYy;
    }

    public void setCardYy(String cardYy) {
        this.cardYy = cardYy;
    }

    public String getCardMm() {
        return cardMm;
    }

    public void setCardMm(String cardMm) {
        this.cardMm = cardMm;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getMemberRegid() {
        return memberRegid;
    }

    public void setMemberRegid(String memberRegid) {
        this.memberRegid = memberRegid;
    }
	
	// 設計 fromEntity 靜態方法
	public static MemberDTO fromEntity(Member member) {
		MemberDTO dto = new MemberDTO();
		dto.setMemberNo(member.getMemberNo());
		dto.setMemberAccount(member.getMemberAccount());
		dto.setMemberPassword(member.getMemberPassword());
		dto.setMemberName(member.getMemberName());
		dto.setMemberId(member.getMemberId());
		dto.setMemberPhone(member.getMemberPhone());
		dto.setMemberZip(member.getMemberZip());
		dto.setMemberAddress(member.getMemberAddress());
		dto.setMemberEmail(member.getMemberEmail());
		dto.setMemberStatus(member.getMemberStatus());
		dto.setCreateTime(member.getCreateTime());
		
		// 設置新增的屬性
        dto.setCardNo(member.getCardNo());
        dto.setCardYy(member.getCardYy());
        dto.setCardMm(member.getCardMm());
        dto.setCardId(member.getCardId());
        dto.setMemberRegid(member.getMemberRegid());
		return dto;
	}
}