package com.scnu.pojo;

public class Product {
	//根据驼峰命名定义属性
	
	private String  productId;
	private String  productName;
	//封装类完成基本类型的使用
	//满足业务意义
	private Double  productPrice;
	private String  productCategory;
	private String  productImgurl;
	private String  productDetailimgurl1;
	private String  productDetailimgurl2;

	public String getProductDetailimgurl1() {
		return productDetailimgurl1;
	}

	public void setProductDetailimgurl1(String productDetailimgurl1) {
		this.productDetailimgurl1 = productDetailimgurl1;
	}

	public String getProductDetailimgurl2() {
		return productDetailimgurl2;
	}

	public void setProductDetailimgurl2(String productDetailimgurl2) {
		this.productDetailimgurl2 = productDetailimgurl2;
	}
	private Integer productNum;
	private String  productDescription;
	//getter setter
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductImgurl() {
		return productImgurl;
	}
	public void setProductImgurl(String productImgurl) {
		this.productImgurl = productImgurl;
	}
	public Integer getProductNum() {
		return productNum;
	}
	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName=" + productName + ", productPrice=" + productPrice
				+ ", productCategory=" + productCategory + ", productImgurl=" + productImgurl +
				",productDetailimgurl1=" + productDetailimgurl1 + ",productDetailimgurl2=" + productDetailimgurl2
				+ ", productNum=" + productNum + ", productDescription=" + productDescription + "]";
	}
	
}
