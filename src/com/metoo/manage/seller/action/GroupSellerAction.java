package com.metoo.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.GoldLog;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.GroupArea;
import com.metoo.foundation.domain.GroupClass;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.GroupInfo;
import com.metoo.foundation.domain.GroupLifeGoods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.SalesLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.UserGoodsClass;
import com.metoo.foundation.domain.query.GroupGoodsQueryObject;
import com.metoo.foundation.domain.query.GroupInfoQueryObject;
import com.metoo.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.metoo.foundation.domain.query.OrderFormQueryObject;
import com.metoo.foundation.domain.query.SalesLogQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IGoldLogService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGroupAreaService;
import com.metoo.foundation.service.IGroupClassService;
import com.metoo.foundation.service.IGroupGoodsService;
import com.metoo.foundation.service.IGroupInfoService;
import com.metoo.foundation.service.IGroupLifeGoodsService;
import com.metoo.foundation.service.IGroupService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.ISalesLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserGoodsClassService;
import com.metoo.foundation.service.IUserService;
import com.metoo.lucene.LuceneUtil;
import com.metoo.lucene.LuceneVo;
import com.metoo.lucene.tools.LuceneVoTools;
import com.metoo.manage.admin.tools.OrderFormTools;

/**
 * 
 * 
 * <p>
 * Title:GroupSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心团购管理控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.metoo.com
 * </p>
 * 
 * @author jy
 * 
 * @date 2014年4月24日
 * 
 * @version metoo_b2b2c 2.0
 */
@Controller
public class GroupSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupAreaService groupAreaService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private ISalesLogService salesLogService;

	@SecurityMapping(title = "卖家团购列表", value = "/seller/group.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group.htm")
	public ModelAndView group(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gg_name,
			String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ("goods".equals(type) || CommUtil.null2String(type).equals("")) {
			type = "goods";
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage,
					mv, "addTime", "desc");
			qo.addQuery("obj.gg_goods.goods_store.user.id", new SysMap(
					"user_id", user.getId()), "=");
			if (!CommUtil.null2String(gg_name).equals("")) {
				qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name
						+ "%"), "like");
			}
			IPageList pList = this.groupGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		} else {
			mv = new JModelAndView("user/default/sellercenter/group_life.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			type = "life";
			String url = this.configService.getSysConfig().getAddress();
			GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
			if (!CommUtil.null2String(gg_name).equals("")) {
				qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name
						+ "%"), "like");
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupLifeGoods.class, mv);
			IPageList pList = this.groupLifeGoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		}
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "卖家团购添加", value = "/seller/group_add.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_add.htm")
	public ModelAndView group_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		if (store.getGroup_meal_endTime() == null) {
			ret = false;
		} else {
			if (new Date().after(store.getGroup_meal_endTime())) {
				ret = false;
			}
		}
		if (ret) {
			if (CommUtil.null2String(type).equals("")) {
				type = "life";
			}
			if (type.equals("life")) {
				mv = new JModelAndView(
						"user/default/sellercenter/group_life_add.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				params.put("joinEndTime", new Date());
				List<GroupLifeGoods> groupGoods = this.groupLifeGoodsService
						.query("select obj from GroupLifeGoods obj where obj.endTime>:joinEndTime and obj.group_status!=-1 and obj.user.id="
								+ user.getId(), params, -1, -1);
				params.put("type", 1);
				List<Group> groups = this.groupService
						.query("select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=:type and  (obj.status=0 or obj.status=1)",
								params, -1, -1);
				List<GroupArea> gas = this.groupAreaService
						.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
								null, -1, -1);
				params.clear();
				params.put("type", 1);
				List<GroupClass> gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=:type and obj.parent.id is null order by obj.gc_sequence asc",
								params, -1, -1);
				if (groups.size() == 0 || gas.size() == 0 || gcs.size() == 0) {// 后台团购尚未开启
					mv = new JModelAndView(
							"user/default/sellercenter/seller_error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/group.htm");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gas", gas);
				mv.addObject("groups", groups);
			} else {
				params.put("joinEndTime", new Date());
				List<GroupGoods> groupGoods = this.groupGoodsService
						.query("select obj from GroupGoods obj where obj.endTime>:joinEndTime and obj.gg_status!=-1 and obj.gg_goods.goods_store.id="
								+ user.getStore().getId(), params, -1, -1);
				params.put("type", 0);
				List<Group> groups = this.groupService
						.query("select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=:type and  (obj.status=0 or obj.status=1) ",
								params, -1, -1);
				List<GroupArea> gas = this.groupAreaService
						.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
								null, -1, -1);
				params.clear();
				params.put("type", 0);
				List<GroupClass> gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=:type and obj.parent.id is null order by obj.gc_sequence asc",
								params, -1, -1);
				if (groups.size() == 0 || gas.size() == 0 || gcs.size() == 0) {// 后台团购尚未开启
					mv = new JModelAndView(
							"user/default/sellercenter/seller_error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/group.htm");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gas", gas);
				mv.addObject("groups", groups);
			}
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有购买团购套餐");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/group_meal.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家团购编辑", value = "/seller/group_lifeedit.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_lifeedit.htm")
	public ModelAndView group_lifeedit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_life_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type=1 and (obj.status=0 or obj.status=1)",
						params, -1, -1);
		List<GroupArea> gas = this.groupAreaService
				.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		List<GroupClass> gcs = this.groupClassService
				.query("select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
		GroupLifeGoods obj = this.groupLifeGoodsService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购编辑", value = "/seller/group_edit.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_edit.htm")
	public ModelAndView group_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.joinEndTime>=:joinEndTime  and obj.group_type=0 and  (obj.status=0 or obj.status=1)",
						params, -1, -1);
		List<GroupArea> gas = this.groupAreaService
				.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		List<GroupClass> gcs = this.groupClassService
				.query("select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
		GroupGoods obj = this.groupGoodsService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购商品", value = "/seller/group_goods.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_goods.htm")
	public ModelAndView group_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("user_id", user.getId());
		List<UserGoodsClass> gcs = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.parent.id is null and obj.user_id=:user_id order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/seller/group_goods_load.htm")
	public void group_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("as", 0);
		params.put("combin_status", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("combin_status", 0);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		params.put("store_id", store.getId());
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		Set<Long> ids = this.genericUserGcIds(ugc);
		List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
		for (Long g_id : ids) {
			UserGoodsClass temp_ugc = this.userGoodsClassService
					.getObjById(g_id);
			ugc_list.add(temp_ugc);
		}
		String query = "select new Goods(id,goods_name,goods_current_price,goods_inventory) from Goods obj where obj.goods_name like :goods_name and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status  and obj.group_buy=:group_buy and obj.goods_store.id=:store_id and obj.activity_status=:as and obj.combin_status=:combin_status and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status and obj.enough_reduce=:enough_reduce and obj.goods_status=:goods_status";
		for (int i = 0; i < ugc_list.size(); i++) {
			if (i == 0) {
				query = query + " and (:ugc" + i + " member of obj.goods_ugcs";
				if (ugc_list.size() == 1) {
					query = query + ")";
				}
			} else {
				if (i == ugc_list.size() - 1) {
					query = query + " or :ugc" + i
							+ " member of obj.goods_ugcs)";
				} else
					query = query + " or :ugc" + i
							+ " member of obj.goods_ugcs";
			}
			params.put("ugc" + i, ugc_list.get(i));
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_price", obj.getGoods_current_price());
			map.put("store_inventory", obj.getGoods_inventory());
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "商品类团购商品保存", value = "/seller/group_goods_save.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_goods_save.htm")
	public void group_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id, String gg_price) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		WebForm wf = new WebForm();
		GroupGoods gg = null;
		if (id.equals("")) {
			gg = wf.toPo(request, GroupGoods.class);
			gg.setAddTime(new Date());
		} else {
			GroupGoods obj = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			gg = (GroupGoods) wf.toPo(request, obj);
		}
		gg.setGg_count(gg.getGg_count());
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		gg.setGroup(group);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		gg.setGg_goods(goods);
		GroupClass gc = this.groupClassService.getObjById(CommUtil
				.null2Long(gc_id));
		gg.setGg_gc(gc);
		GroupArea ga = this.groupAreaService.getObjById(CommUtil
				.null2Long(ga_id));
		gg.setGg_ga(ga);
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(10,
				CommUtil.div(gg_price, goods.getStore_price()))));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		Map map = new HashMap();
		try {
			String fileName = gg.getGg_img() == null ? "" : gg.getGg_img()
					.getName();
			map = CommUtil.saveFileToServer(request, "gg_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					gg.setGg_img(gg_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory gg_img = gg.getGg_img();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.div(CommUtil.mul(
				gg.getGg_price(), 10), gg.getGg_goods().getGoods_price())));
		String goods_lucene_path = System.getProperty("metoob2b2c.root")
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setConfig(this.configService.getSysConfig());
		lucene.setIndex_path(goods_lucene_path);
		if (id.equals("")) {
			this.groupGoodsService.save(gg);
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(gg.getId());
			vo.setVo_title(gg.getGg_name());
			vo.setVo_content(gg.getGg_content());
			vo.setVo_type("groupgoods");
			vo.setVo_store_price(CommUtil.null2Double(gg.getGg_price()));
			vo.setVo_add_time(gg.getAddTime().getTime());
			vo.setVo_goods_salenum(gg.getGg_selled_count());
			if (gg.getGg_img() != null) {
				vo.setVo_main_photo_url(gg.getGg_img().getPath() + "/"
						+ gg.getGg_img().getName());
			}
			vo.setVo_cat(gg.getGg_gc().getId().toString());
			vo.setVo_rate(CommUtil.null2String(gg.getGg_rebate()));
			if (gg.getGg_ga() != null) {
				vo.setVo_goods_area(gg.getGg_ga().getId().toString());
			}
			lucene.writeIndex(vo);
		} else {
			this.groupGoodsService.update(gg);
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(gg.getId());
			vo.setVo_title(gg.getGg_name());
			vo.setVo_content(gg.getGg_content());
			vo.setVo_type("groupgoods");
			vo.setVo_store_price(CommUtil.null2Double(gg.getGg_price()));
			vo.setVo_add_time(gg.getAddTime().getTime());
			vo.setVo_goods_salenum(gg.getGg_selled_count());
			if (gg.getGg_img() != null) {
				vo.setVo_main_photo_url(gg.getGg_img().getPath() + "/"
						+ gg.getGg_img().getName());
			}
			vo.setVo_cat(gg.getGg_gc().getId().toString());
			vo.setVo_rate(CommUtil.null2String(gg.getGg_rebate()));
			if (gg.getGg_ga() != null) {
				vo.setVo_goods_area(gg.getGg_ga().getId().toString());
			}
			lucene.update(CommUtil.null2String(gg.getId()),
					luceneVoTools.updateGroupGoodsIndex(gg));
		}
		goods.setGoods_current_price(gg.getGg_price());
		goods.setGroup_buy(1);
		// 商品的团购价格
		this.goodsService.update(goods);
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "申请团购成功");
		json.put("url", CommUtil.getURL(request)
				+ "/seller/group.htm?type=goods");
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@SecurityMapping(title = "生活类团购商品保存", value = "/seller/grouplife_goods_save.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_goods_save.htm")
	public void grouplife_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String gc_id, String ga_id, String beginTime, String endTime,
			String group_price, String cost_price) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		WebForm wf = new WebForm();
		GroupLifeGoods grouplifegoods = null;
		if (id.equals("")) {
			grouplifegoods = wf.toPo(request, GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.groupLifeGoodsService.getObjById(Long
					.parseLong(id));
			grouplifegoods = (GroupLifeGoods) wf.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(0);
		grouplifegoods.setGroup_status(0);
		GroupArea gg_ga = this.groupAreaService.getObjById(CommUtil
				.null2Long(ga_id));
		grouplifegoods.setGg_ga(gg_ga);
		GroupClass gg_gc = this.groupClassService.getObjById(CommUtil
				.null2Long(gc_id));
		grouplifegoods.setGg_gc(gg_gc);
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		grouplifegoods.setGroup(group);
		grouplifegoods.setUser(user);
		grouplifegoods.setBeginTime(CommUtil.formatDate(beginTime));
		grouplifegoods.setEndTime(CommUtil.formatDate(endTime));
		grouplifegoods.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(10,
				CommUtil.div(group_price, cost_price))));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		Map map = new HashMap();
		try {
			String fileName = grouplifegoods.getGroup_acc() == null ? ""
					: grouplifegoods.getGroup_acc().getName();
			map = CommUtil.saveFileToServer(request, "group_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					grouplifegoods.setGroup_acc(gg_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory gg_img = grouplifegoods.getGroup_acc();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.groupLifeGoodsService.save(grouplifegoods);
		} else {
			this.groupLifeGoodsService.update(grouplifegoods);
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "生活类团购商品保存成功");
		json.put("url", CommUtil.getURL(request)
				+ "/seller/group.htm?type=life");
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		if (ugc != null) {
			ids.add(ugc.getId());
			for (UserGoodsClass child : ugc.getChilds()) {
				Set<Long> cids = genericUserGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	@SecurityMapping(title = "团购商品删除", value = "/seller/group_del.htm*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_del.htm")
	public String group_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);
			// 删除索引
			String goods_lucene_path = System.getProperty("metoob2b2c.root")
					+ File.separator + "luence" + File.separator + "groupgoods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
			this.goodsService.update(goods);
			CommUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:group.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "团购商品删除", value = "/seller/group_lifedel.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_lifedel.htm")
	public String group_lifedel(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupLifeGoods gg = this.groupLifeGoodsService.getObjById(CommUtil
					.null2Long(id));
			if (gg != null && gg.getUser().getId().equals(user.getId())) {
				CommUtil.del_acc(request, gg.getGroup_acc());
				// 删除索引
				String goods_lucene_path = System.getProperty("metoob2b2c.root")
						+ File.separator + "luence" + File.separator
						+ "lifegoods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.delete_index(CommUtil.null2String(id));
				this.groupLifeGoodsService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:group.htm?type=life&currentPage=" + currentPage
				+ "type=life";
	}

	@SecurityMapping(title = "团购套餐购买", value = "/seller/group_meal.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal.htm")
	public ModelAndView group_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_meal.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "团购套餐购买", value = "/seller/group_meal_save.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal_save.htm")
	public void group_meal_save(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map json = new HashMap();
		json.put("ret", false);
		if (configService.getSysConfig().isGroupBuy()) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = configService.getSysConfig().getGroup_meal_gold();
			int days = 30;
			if (meal_day.equals("30")) {
				days = 30;
			}
			if (meal_day.equals("90")) {
				days = 90;
			}
			if (meal_day.equals("180")) {
				days = 180;
			}
			if (meal_day.equals("360")) {
				days = 360;
			}
			int costday = days / 30;
			if (user.getGold() >= costday * cost) {
				user.setGold(user.getGold() - costday * cost);
				this.userService.update(user);
				Date day = user.getStore().getGroup_meal_endTime();
				Date d = new Date();
				if (day != null) {
					if (day.after(new Date())) {
						user.getStore().setGroup_meal_endTime(
								this.addDate(user.getStore()
										.getGroup_meal_endTime(), CommUtil
										.null2Long(days)));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getGroup_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore()
								.getGroup_meal_endTime(), CommUtil
								.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4);// 促销类型为满就送
						this.salesLogService.save(c_log);
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setGroup_meal_endTime(
								CommUtil.formatDate(latertime,
										"yyyy-MM-dd HH:mm:ss"));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getGroup_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore()
								.getGroup_meal_endTime(), CommUtil
								.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4);// 促销类型为满就送
						this.salesLogService.save(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(ca.DATE, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setGroup_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.update(user.getStore());
					// 记录金币日志
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买团购套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.save(log);
					// 保存套餐购买信息
					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(user.getStore().getGroup_meal_endTime());
					c_log.setEnd_time(this.addDate(user.getStore()
							.getGroup_meal_endTime(), CommUtil.null2Long(days)));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(4);// 促销类型为满就送
					this.salesLogService.save(c_log);
				}
				json.put("ret", true);
				json.put("op_title", "购买成功");
				json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()),
						response);
			} else {
				json.put("ret", false);
				json.put("op_title", "您的金币不足，无法购买团购套餐");
				json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()),
						response);
			}
		} else {
			json.put("ret", false);
			json.put("op_title", "购买失败");
			json.put("url", CommUtil.getURL(request) + "/seller/group.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		}
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}

	@RequestMapping("/seller/verify_gourp_begintime.htm")
	public void verify_gourp_begintime(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String group_id) {
		boolean ret = false;
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(beginTime);
		if (date.after(group.getBeginTime())
				|| CommUtil.formatLongDate(date).equals(
						CommUtil.formatLongDate(group.getBeginTime()))) {
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/seller/verify_gourp_endtime.htm")
	public void verify_gourp_endtime(HttpServletRequest request,
			HttpServletResponse response, String endTime, String group_id,
			String beginTime) {
		boolean ret = false;
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(endTime);
		Date bdate = CommUtil.formatDate(beginTime);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (date.before(group.getEndTime())
				&& date.before(store.getGroup_meal_endTime())) {
			ret = true;
			if (date.after(bdate)) {
				ret = true;
			} else {
				ret = false;
			}
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "生活购订单列表", value = "/seller/grouplife_order.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_order.htm")
	public ModelAndView grouplife_selforder(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/grouplife_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 0), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 0), "=");// 无需查询主订单
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "=");
		ofqo.addQuery("obj.store_id", new SysMap("store_id", SecurityUserHolder
				.getCurrentUser().getStore().getId().toString()), "=");
		if (status != null && !status.equals("")) {
			ofqo.addQuery("obj.order_status", new SysMap("order_status",
					CommUtil.null2Int(status)), "=");
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
			mv.addObject("order_id", order_id);
		}
		mv.addObject("orderFormTools", orderFormTools);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "生活购消费码列表", value = "/seller/grouplife_selfinfo.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/grouplife_selfinfo.htm")
	public ModelAndView grouplife_selfinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String info_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/grouplife_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, "",
				"");
		qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 0),
				"=");
		qo.addQuery("obj.lifeGoods.user.id",
				new SysMap("user_id", user.getId()), "=");
		if (!CommUtil.null2String(info_id).equals("")) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", info_id), "=");
			mv.addObject("info_id", info_id);
		}
		qo.addQuery("obj.status", new SysMap("status", 0), "!=");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupInfo.class, mv);
		IPageList pList = this.groupInfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/seller/lifeorder_cancel.htm*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/lifeorder_cancel.htm")
	public String lifeorder_cancel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (of.getStore_id().equals(user.getStore().getId().toString())) {
			of.setOrder_status(0);
			this.orderFormService.update(of);
		}
		return "redirect:" + "/seller/grouplife_order.htm";
	}

	@SecurityMapping(title = "使用消费码", value = "/seller/check_group_code.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/check_group_code.htm")
	public void check_group_code(HttpServletRequest request,
			HttpServletResponse response, String value) {
		String code = "0";// 不存在
		GroupInfo info = this.groupInfoService.getObjByProperty(null,
				"group_sn", value);
		if (info != null) {
			if (info.getLifeGoods().getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				if (info.getStatus() == 1) {
					code = "-30";// 过期
				}
				if (info.getStatus() == -1) {
					code = "-50";// 过期
				}
				if (info.getStatus() == 3) {
					code = "-100";// 申请退款中
				}
				if (info.getStatus() == 5) {
					code = "-150";// 平台退款中
				}
				if (info.getStatus() == 7) {
					code = "-200";// 已经退款
				}
				if (info.getStatus() == 0) {
					info.setStatus(1);
					this.groupInfoService.update(info);
					code = "100";// 成功
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "消费码退款", value = "/seller/grouplife_return_confirm.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/grouplife_return_confirm.htm")
	public ModelAndView grouplife_return_confirm(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/grouplife_return_confirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo info = this.groupInfoService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (info != null) {
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				mv.addObject("obj", info);
			}
		}
		return mv;
	}

	@SecurityMapping(title = "消费码退款保存", value = "/seller/grouplife_return_confirm_save.htm*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/grouplife_return_confirm_save.htm")
	public String grouplife_return_confirm_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupInfoService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (info != null) {
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				info.setStatus(5);// 商家确认退款，后平台进行退款
				this.groupInfoService.update(info);
			}
		}
		return "redirect:/seller/grouplife_selfinfo.htm";
	}

	public void return_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "虚拟团购码验证", value = "/seller/group_code.htm*", rtype = "seller", rname = "团购套餐", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping("/seller/group_code.htm")
	public ModelAndView group_code(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "满就送销售套餐日志", value = "/seller/group_meal_log.htm*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping("/seller/group_meal_log.htm")
	public ModelAndView group_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/group_meal_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		SalesLogQueryObject qo = new SalesLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.sales_type", new SysMap("sales_type", 4), "=");
		IPageList pList = this.salesLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
