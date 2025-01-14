package com.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
public class Product {
	@Id
	private Integer product_no;
	private Integer category_no;
	private String product_name;
	private Integer product_price;
	private String product_introduce;
	private Integer product_stock;
	private Integer product_status;
	private byte[] product_picture;

	// 新增建構子
	public Product() {
	}

	// 新增複製建構子，用於DTO轉換
	public Product(Product product) {
		this.product_no = product.getProduct_no();
		this.category_no = product.getCategory_no();
		this.product_name = product.getProduct_name();
		this.product_price = product.getProduct_price();
		this.product_introduce = product.getProduct_introduce();
		this.product_stock = product.getProduct_stock();
		this.product_status = product.getProduct_status();
		this.product_picture = product.getProduct_picture() != null ? 
						product.getProduct_picture().clone() : null;
	}

	public Integer getProduct_no() {
		return product_no;
	}

	public void setProduct_no(Integer product_no) {
		this.product_no = product_no;
	}

	public Integer getCategory_no() {
		return category_no;
	}

	public void setCategory_no(Integer category_no) {
		this.category_no = category_no;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public Integer getProduct_price() {
		return product_price;
	}

	public void setProduct_price(Integer product_price) {
		this.product_price = product_price;
	}

	public String getProduct_introduce() {
		return product_introduce;
	}

	public void setProduct_introduce(String product_introduce) {
		this.product_introduce = product_introduce;
	}

	public Integer getProduct_stock() {
		return product_stock;
	}

	public void setProduct_stock(Integer product_stock) {
		this.product_stock = product_stock;
	}

	public Integer getProduct_status() {
		return product_status;
	}

	public void setProduct_status(Integer product_status) {
		this.product_status = product_status;
	}

	public byte[] getProduct_picture() {
		return product_picture;
	}

	public void setProduct_picture(byte[] product_picture) {
		this.product_picture = product_picture;
	}

}