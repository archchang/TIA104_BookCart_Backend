package com.member;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "member")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_no", updatable = false)
	private Integer memberNo;

	@Column(name = "member_account", nullable = false)
	private String memberAccount;

	@Column(name = "member_password", nullable = false)
	private String memberPassword;

	@Column(name = "member_name", nullable = false)
	private String memberName;

	@Column(name = "member_id")
	private String memberId;

	@Column(name = "member_phone")
	private String memberPhone;

	@Column(name = "member_zip")
	private String memberZip;

	@Column(name = "member_address")
	private String memberAddress;

	@Column(name = "card_no")
	private String cardNo;

	@Column(name = "card_yy")
	private String cardYy;

	@Column(name = "card_mm")
	private String cardMm;

	@Column(name = "card_id")
	private String cardId;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Lob
	@Column(name = "member_photo")
	private byte[] memberPhoto;

	@Column(name = "member_email", nullable = false)
	private String memberEmail;

	@Column(name = "member_status", nullable = false)
	private Integer memberStatus;

	@Column(name = "member_regid")
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public byte[] getMemberPhoto() {
		return memberPhoto;
	}

	public void setMemberPhoto(byte[] memberPhoto) {
		this.memberPhoto = memberPhoto;
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

	public String getMemberRegid() {
		return memberRegid;
	}

	public void setMemberRegid(String memberRegid) {
		this.memberRegid = memberRegid;
	}

	
}