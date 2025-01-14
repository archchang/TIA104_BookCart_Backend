package com.orders;

import java.sql.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "orders")
public class Orders {
	@Id
    private Integer orders_no;
    private Integer member_no;
    private Integer orders_status;
    private String orders_receiver;
    private String receiver_phone;
    private String receiver_zip;
    private String receiver_address;
    private Integer orders_total;
    private Date orders_date;
	public Integer getOrders_no() {
		return orders_no;
	}
	public void setOrders_no(Integer orders_no) {
		this.orders_no = orders_no;
	}
	public Integer getMember_no() {
		return member_no;
	}
	public void setMember_no(Integer member_no) {
		this.member_no = member_no;
	}
	public Integer getOrders_status() {
		return orders_status;
	}
	public void setOrders_status(Integer orders_status) {
		this.orders_status = orders_status;
	}
	public String getOrders_receiver() {
		return orders_receiver;
	}
	public void setOrders_receiver(String orders_receiver) {
		this.orders_receiver = orders_receiver;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_zip() {
		return receiver_zip;
	}
	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}
	public String getReceiver_address() {
		return receiver_address;
	}
	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;
	}
	public Integer getOrders_total() {
		return orders_total;
	}
	public void setOrders_total(Integer orders_total) {
		this.orders_total = orders_total;
	}
	public Date getOrders_date() {
		return orders_date;
	}
	public void setOrders_date(Date orders_date) {
		this.orders_date = orders_date;
	}

	
}