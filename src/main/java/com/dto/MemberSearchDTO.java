package com.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class MemberSearchDTO {
	private String memberName;        // 會員名稱（模糊查詢）
    private String memberAccount;     // 會員帳號（精確查詢）
    private String memberPhone;       // 會員電話（模糊查詢）
    private String memberEmail;       // 會員Email（模糊查詢）
    private String memberAddress;     // 會員地址（模糊查詢）
    private Integer memberStatus;     // 會員狀態（精確查詢）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeStart;     // 建立時間起始
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeEnd;       // 建立時間結束
    
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getMemberAccount() {
		return memberAccount;
	}
	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}
	public String getMemberPhone() {
		return memberPhone;
	}
	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}
	public String getMemberEmail() {
		return memberEmail;
	}
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	public String getMemberAddress() {
		return memberAddress;
	}
	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}
	public Integer getMemberStatus() {
		return memberStatus;
	}
	public void setMemberStatus(Integer memberStatus) {
		this.memberStatus = memberStatus;
	}
	public Date getCreateTimeStart() {
		return createTimeStart;
	}
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
    
    
}