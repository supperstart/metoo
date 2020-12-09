package com.metoo.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.metoo.core.constant.Globals;
import com.metoo.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Accessory.java
 * </p>
 * 
 * <p>
 * Description:系统附件管理类，用来管理系统所有附件信息，包括图片附件、rar附件等等
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-25
 * 
 * @version koala_b2b2c v2.0 2015版 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity//告诉JPA这是一个实体类（和数据表映射的类)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "accessory")//@Table来指定和哪个数据表对应;如果省略默认表名就是customer；
public class Accessory extends IdEntity {
	private String name;// 附件名称
	private String path;// 存放路径
	@Column(precision = 12, scale = 2)
	private BigDecimal size;// 附件大小
	private int width;// 宽度
	private int height;// 高度
	private String ext;// 扩展名，不包括.
	private String info;// 附件说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 附件对应的用户，可以精细化管理用户附件
	@ManyToOne(fetch = FetchType.LAZY)
	private Album album;// 图片对应的相册
	@ManyToOne(fetch = FetchType.LAZY)
	private SysConfig config;// 对应登录页左侧图片 
	@OneToMany(mappedBy = "goods_main_photo")//双向关联指定mappedBy
	private List<Goods> goods_main_list = new ArrayList<Goods>();
	@ManyToMany(mappedBy = "goods_photos")
	private List<Goods> goods_list = new ArrayList<Goods>();// 商品列表
	@ManyToMany(mappedBy = "c_goods_photos")
	private List<CGoods> c_goods_list = new ArrayList<CGoods>();// 子商品列表
	
	public List<CGoods> getC_goods_list() {
		return c_goods_list;
	}

	public void setC_goods_list(List<CGoods> c_goods_list) {
		this.c_goods_list = c_goods_list;
	}

	public Accessory(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Accessory(Long id) {
		super.setId(id);
		// TODO Auto-generated constructor stub
	}

	public Accessory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public List<Goods> getGoods_main_list() {
		return goods_main_list;
	}

	public void setGoods_main_list(List<Goods> goods_main_list) {
		this.goods_main_list = goods_main_list;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public BigDecimal getSize() {
		return size;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	@JsonIgnore 
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public SysConfig getConfig() {
		return config;
	}

	public void setConfig(SysConfig config) {
		this.config = config;
	}

}
