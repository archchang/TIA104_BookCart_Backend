package com.productcategory;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("product_category")
public class ProductCategory {
	@Id
	private Integer category_no;
	private String category_name;
	private String category_describe;
	public Integer getCategory_no() {
		return category_no;
	}
	public void setCategory_no(Integer category_no) {
		this.category_no = category_no;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getCategory_describe() {
		return category_describe;
	}
	public void setCategory_describe(String category_describe) {
		this.category_describe = category_describe;
	}
	
}