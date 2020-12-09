package com.metoo.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.metoo.core.constant.Globals;
import com.metoo.core.domain.IdEntity;
import com.metoo.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: GoodsClass.java
 * </p>
 * 
 * <p>
 * Description:商品分类管理类，用来管理商城商品分类信息
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
 * @author erikzhang、hezeng
 * 
 * @date 2014-4-25
 * 
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsclass")
public class GoodsClass extends IdEntity implements java.lang.Comparable {
	private String className;// 类别名称
	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "sequence asc")
	private Set<GoodsClass> childs = new TreeSet<GoodsClass>();// 子分类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass parent;// 父级分类
	private int sequence;// 索引
	private int level;// 层级
	private boolean display;// 是否显示
	private boolean recommend;// 是否推荐
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;// 商品类型
	@Column(columnDefinition = "LongText")
	private String seo_keywords;// 关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;// 描述
	@OneToMany(mappedBy = "gc")
	private List<Goods> goods_list = new ArrayList<Goods>();// 商城分类对应的商品
	@Column(columnDefinition = "int default 0")
	private int icon_type;// 一级分类图标显示类型，0为系统图标，1为上传图标
	private String icon_sys;// 系统图标
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory icon_acc;// 上传图标
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_rate;// 佣金比例
	private BigDecimal commission_vat;// 佣金vat
	@Column(precision = 12, scale = 0)
	private BigDecimal guarantee;// 保证金
	@ManyToMany(mappedBy = "spec_goodsClass_detail", cascade = { CascadeType.ALL })
	private List<GoodsSpecification> spec_detail = new ArrayList<GoodsSpecification>();// 作为详细类目对应的规格属性
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate;// 商品分类描述相符评分，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate;// 商品分类服务态度评价，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate;// 商品分类发货速度评价，同行业均分
	@Column(columnDefinition = "LongText")
	private String gc_advert;// 分类广告，使用json管理{"acc_id":1,"acc_url":"www.koala.com","adv_id":1,"adv_type":0},adv_type广告类型，0为自定义广告，1为系统广告，adv_id广告位id
	@Column(columnDefinition = "LongText")
	private String gb_info;// 分类弹出层显示品牌信息，使用json管理[{"id":2,"name":"阿迪"},"id":2,"name":"阿迪"}]

	private String app_icon;// 移动端一级分类的图标

	private String gc_color;// 分类底层颜色

	private String wx_icon;// 微商城一级分类的图标

	public GoodsClass(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public GoodsClass(Long id, String className) {
		super.setId(id);
		this.setClassName(className);
		// TODO Auto-generated constructor stub
	}

	public GoodsClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getWx_icon() {
		return wx_icon;
	}

	public void setWx_icon(String wx_icon) {
		this.wx_icon = wx_icon;
	}

	public String getApp_icon() {
		return app_icon;
	}

	public void setApp_icon(String app_icon) {
		this.app_icon = app_icon;
	}

	public String getGb_info() {
		return gb_info;
	}

	public void setGb_info(String gb_info) {
		this.gb_info = gb_info;
	}

	public String getGc_color() {
		return gc_color;
	}

	public void setGc_color(String gc_color) {
		this.gc_color = gc_color;
	}

	public String getGc_advert() {
		return gc_advert;
	}

	public void setGc_advert(String gc_advert) {
		this.gc_advert = gc_advert;
	}

	public List<GoodsSpecification> getSpec_detail() {
		return spec_detail;
	}

	public void setSpec_detail(List<GoodsSpecification> spec_detail) {
		this.spec_detail = spec_detail;
	}

	public BigDecimal getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}

	public int getIcon_type() {
		return icon_type;
	}

	public void setIcon_type(int icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_sys() {
		return icon_sys;
	}

	public void setIcon_sys(String icon_sys) {
		this.icon_sys = icon_sys;
	}

	public Accessory getIcon_acc() {
		return icon_acc;
	}

	public void setIcon_acc(Accessory icon_acc) {
		this.icon_acc = icon_acc;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	@JsonIgnore
	public GoodsType getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public GoodsClass getParent() {
		return parent;
	}

	public void setParent(GoodsClass parent) {
		this.parent = parent;
	}

	public String getSeo_keywords() {
		return seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

	public BigDecimal getDescription_evaluate() {
		return description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public BigDecimal getService_evaluate() {
		return service_evaluate;
	}

	public void setService_evaluate(BigDecimal service_evaluate) {
		this.service_evaluate = service_evaluate;
	}

	public BigDecimal getShip_evaluate() {
		return ship_evaluate;
	}

	public void setShip_evaluate(BigDecimal ship_evaluate) {
		this.ship_evaluate = ship_evaluate;
	}

	public BigDecimal getGuarantee() {
		return guarantee;
	}

	public void setGuarantee(BigDecimal guarantee) {
		this.guarantee = guarantee;
	}

	public Set<GoodsClass> getChilds() {
		return childs;
	}

	public void setChilds(Set<GoodsClass> childs) {
		this.childs = childs;
	}

	@Override
	public int compareTo(Object obj) {
		// TODO Auto-generated method stub
		GoodsClass gc = (GoodsClass) obj;
		return CommUtil.null2Int(this.getId() - gc.getId());
	}

	public BigDecimal getCommission_vat() {
		return commission_vat;
	}

	public void setCommission_vat(BigDecimal commission_vat) {
		this.commission_vat = commission_vat;
	}

}
