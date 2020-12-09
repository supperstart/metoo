package com.metoo.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.metoo.core.beans.BeanUtils;
import com.metoo.core.beans.BeanWrapper;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.GroupArea;
import com.metoo.foundation.domain.GroupClass;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.GroupInfo;
import com.metoo.foundation.domain.GroupLifeGoods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.GroupGoodsQueryObject;
import com.metoo.foundation.domain.query.GroupInfoQueryObject;
import com.metoo.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.metoo.foundation.domain.query.OrderFormQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGroupAreaService;
import com.metoo.foundation.service.IGroupClassService;
import com.metoo.foundation.service.IGroupGoodsService;
import com.metoo.foundation.service.IGroupInfoService;
import com.metoo.foundation.service.IGroupLifeGoodsService;
import com.metoo.foundation.service.IGroupService;
import com.metoo.foundation.service.IOrderFormService;
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
 * <p>
 * Title: GroupSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台平台商自营团购控制器。平台商也可以发布团购商品
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
 * @author jinxinzhe
 * 
 * @date 2014-5-20
 * 
 * @version metoo_b2b2c v2.0 2015版 
 */
@Controller
public class SelfGroupManageAction {
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
	private IGoodsClassService GoodsClassService;
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;

	@SecurityMapping(title = "自营商品类团购商品列表", value = "/admin/group_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self.htm")
	public ModelAndView group_self(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gg_name) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.gg_goods.goods_type", new SysMap(
				"gg_goods_goods_type", 0), "=");
		if (!CommUtil.null2String(gg_name).equals("")) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name
					+ "%"), "like");
		}
		IPageList pList = this.groupGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}

	/**
	 * grouplife_self列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营生活类团购商品列表", value = "/admin/grouplife_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_self.htm")
	public ModelAndView grouplife_self(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name) {
		ModelAndView mv = new JModelAndView("admin/blue/grouplife_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(
				currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 1), "=");
		if (!CommUtil.null2String(gg_name).equals("")) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name
					+ "%"), "like");
		}
		IPageList pList = this.grouplifegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}

	@SecurityMapping(title = "自营商品类团购商品添加", value = "/admin/group_self_add.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_add.htm")
	public ModelAndView group_self_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int g_type = 0;
		if ("life".equals(type)) {
			g_type = 1;
			mv = new JModelAndView("admin/blue/grouplife_self_add.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		params.put("g_type", g_type);
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.joinEndTime>=:joinEndTime and obj.group_type= :g_type and  (obj.status=0 or obj.status=1)",
						params, -1, -1);
		List<GroupArea> gas = this.groupAreaService
				.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		params.remove("joinEndTime");
		params.remove("status");
		List<GroupClass> gcs = this.groupClassService
				.query("select obj from GroupClass obj where obj.gc_type=:g_type and obj.parent.id is null order by obj.gc_sequence asc",
						params, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "自营团购商品编辑", value = "/admin/group_self_edit.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_edit.htm")
	public ModelAndView group_self_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/group_self_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GroupArea> gas = this.groupAreaService
				.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		List<GroupClass> gcs = this.groupClassService
				.query("select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
		if ("goods".equals(type)) {
			GroupGoods obj = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("group_type", 0);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.group_type=:group_type and  (obj.status=0 or obj.status=1)",
							params, -1, -1);
			mv.addObject("groups", groups);
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.getObjById(CommUtil
					.null2Long(id));
			mv = new JModelAndView("admin/blue/grouplife_self_add.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("obj", obj);
			Map params = new HashMap();
			params.put("group_type", 1);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.group_type=:group_type and  (obj.status=0 or obj.status=1)",
							params, -1, -1);
			mv.addObject("groups", groups);
		}
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		return mv;

	}

	@SecurityMapping(title = "自营团购商品", value = "/admin/group_goods_self.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_goods_self.htm")
	public ModelAndView group_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/group_goods_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.GoodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/admin/group_goods_self_load.htm")
	public void group_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("combin_status", 0);
		String query = "select new Goods(id,goods_name,goods_current_price,goods_inventory) from Goods obj where obj.goods_name like:goods_name and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status and obj.group_buy=:group_buy and obj.goods_status=:goods_status and obj.goods_type=:goods_type and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status and obj.enough_reduce=:enough_reduce";
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.GoodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			Set<Long> ids = this.genericGcIds(gc);
			params.put("ids", ids);
			query = query + " and obj.gc.id in (:ids)";
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getGoods_current_price());
			map.put("goods_name", obj.getGoods_name());
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

	@SecurityMapping(title = "商品类团购商品保存", value = "/admin/group_goods_self_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_goods_self_save.htm")
	public ModelAndView group_goods_self_save(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id, String gg_price) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
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
		boolean wrire_vo = gg.getBeginTime().before(new Date());
		if (wrire_vo) {
			gg.setGg_status(1);
			goods.setGroup_buy(2);
			goods.setGoods_current_price(gg.getGg_price());
			goods.setGroup(gg.getGroup());
		} else {
			gg.setGg_status(2);
			goods.setGroup_buy(4);
		}
		lucene.setIndex_path(goods_lucene_path);
		if (id.equals("")) {
			this.groupGoodsService.save(gg);
			if (wrire_vo) {
				LuceneVo vo = new LuceneVo();
				vo.setVo_id(gg.getId());
				vo.setVo_title(gg.getGg_name());
				vo.setVo_content(gg.getGg_content());
				vo.setVo_type("lifegoods");
				double ggPrice = CommUtil.null2Double(gg.getGg_price());
				vo.setVo_store_price(ggPrice);
				vo.setVo_curr_price(ggPrice);
				vo.setVo_sell_price(String.valueOf(ggPrice));
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
			}

		} else {
			this.groupGoodsService.update(gg);
			if (wrire_vo) {
				LuceneVo vo = new LuceneVo();
				vo.setVo_id(gg.getId());
				vo.setVo_title(gg.getGg_name());
				vo.setVo_content(gg.getGg_content());
				vo.setVo_type("lifegoods");
				double ggPrice = CommUtil.null2Double(gg.getGg_price());
				vo.setVo_store_price(ggPrice);
				vo.setVo_curr_price(ggPrice);
				vo.setVo_sell_price(String.valueOf(ggPrice));
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
		}
		this.goodsService.update(goods);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/group_self.htm");
		if (id != null && !id.equals("")) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/admin/group_self_add.htm?type=goods");
		}
		return mv;
	}

	@SecurityMapping(title = "生活类团购商品保存", value = "/admin/group_lifegoods_self_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifegoods_self_save.htm")
	public ModelAndView group_lifegoods_self_save(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String gc_id, String ga_id, String beginTime, String endTime,
			String group_price, String cost_price) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		WebForm wf = new WebForm();
		GroupLifeGoods grouplifegoods = null;
		if (id.equals("")) {
			grouplifegoods = wf.toPo(request, GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.getObjById(Long
					.parseLong(id));
			grouplifegoods = (GroupLifeGoods) wf.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(1);
		grouplifegoods.setGroup_status(1);
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
		if (id.equals("")) {
			this.grouplifegoodsService.save(grouplifegoods);
		} else
			this.grouplifegoodsService.update(grouplifegoods);
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
		String goods_lucene_path = System.getProperty("metoob2b2c.root")
				+ File.separator + "luence" + File.separator + "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		if (id.equals("")) {
			this.grouplifegoodsService.save(grouplifegoods);
			LuceneVo vo = this.luceneVoTools
					.updateLifeGoodsIndex(grouplifegoods);
			lucene.writeIndex(vo);
		} else {
			this.grouplifegoodsService.update(grouplifegoods);
			LuceneVo vo = this.luceneVoTools
					.updateLifeGoodsIndex(grouplifegoods);
			lucene.update(grouplifegoods.getId().toString(), vo);
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/grouplife_self.htm");
		if (id != null && !id.equals("")) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/admin/group_self_add.htm?type=life");
		}
		return mv;
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	@SecurityMapping(title = "团购商品删除", value = "/admin/group_self_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_del.htm")
	public String group_self_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);
			goods.setGroup(null);
			this.goodsService.update(goods);
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
			CommUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:/admin/grouplife_self.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "生活类团购商品删除", value = "/admin/group_lifeself_del.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifeself_del.htm")
	public String group_lifeself_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupLifeGoods grouplifegoods = this.grouplifegoodsService
						.getObjById(Long.parseLong(id));
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
				this.grouplifegoodsService.delete(Long.parseLong(id));
			}
		}
		return "redirect:grouplife_self?currentPage=" + currentPage;
	}

	@RequestMapping("/verify_gourp_begintime.htm")
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

	@RequestMapping("/verify_gourp_endtime.htm")
	public void verify_gourp_endtime(HttpServletRequest request,
			HttpServletResponse response, String endTime, String group_id) {
		boolean ret = false;
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(endTime);
		if (date.before(group.getEndTime())) {
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

	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/admin/group_lifeself_ajax.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_lifeself_ajax.htm")
	public void group_lifeself_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupLifeGoods obj = this.grouplifegoodsService.getObjById(Long
				.parseLong(id));
		Field[] fields = GroupLifeGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.grouplifegoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/admin/group_self_ajax.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/group_self_ajax.htm")
	public void group_self_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupGoods obj = this.groupGoodsService.getObjById(Long.parseLong(id));
		Field[] fields = GroupGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.groupGoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "生活购订单列表", value = "/admin/grouplife_selforder.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_selforder.htm")
	public ModelAndView grouplife_selforder(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/grouplife_selforder.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		ofqo.addQuery("obj.order_form", new SysMap("order_form", 1), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 0), "=");// 无需查询主订单
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "=");
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

	@SecurityMapping(title = "生活购消费码列表", value = "/admin/grouplife_selfinfo.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_selfinfo.htm")
	public ModelAndView grouplife_selfinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status,
			String info_id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/grouplife_selfinfo.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv, "",
				"");
		qo.addQuery("obj.lifeGoods.goods_type", new SysMap("goods_type", 1),
				"=");
		if (!CommUtil.null2String(info_id).equals("")) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", info_id), "=");
			mv.addObject("info_id", info_id);
		}
		if (status != null && !status.equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(status)), "=");
		}
		IPageList pList = this.groupinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/buyer/grouplife_selfinfo.htm", "", params, pList, mv);
		mv.addObject("status", status);
		return mv;
	}

	@SecurityMapping(title = "生活购订单取消", value = "/admin/lifeorder_cancel.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/lifeorder_cancel.htm")
	public String lifeorder_cancel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		of.setOrder_status(0);
		this.orderFormService.update(of);
		return "redirect:" + "/admin/grouplife_selforder.htm";
	}

	@SecurityMapping(title = "生活购订单详细", value = "/admin/lifeorder_view.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/lifeorder_view.htm")
	public ModelAndView lifeorder_view(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String info_id, String status) {
		ModelAndView mv = new JModelAndView("admin/blue/lifeorder_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && obj.getOrder_form() == 1
				&& obj.getOrder_status() == 20) {
			Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());
			String url = this.configService.getSysConfig().getAddress();
			if (url == null || url.equals("")) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv,
					"", "");
			if (status != null && !status.equals("")) {
				qo.addQuery("obj.status",
						new SysMap("status", CommUtil.null2Int(status)), "=");
			}
			qo.addQuery("obj.order_id", new SysMap("order_id", obj.getId()),
					"=");
			qo.addQuery(
					"obj.lifeGoods.id",
					new SysMap("goods_id", CommUtil.null2Long(json.get(
							"goods_id").toString())), "=");
			if (info_id != null && !info_id.equals("")) {
				qo.addQuery("obj.group_sn", new SysMap("info_id", info_id), "=");
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupInfo.class, mv);
			IPageList pList = this.groupinfoService.list(qo);
			CommUtil.saveIPageList2ModelAndView(url
					+ "/buyer/lifeorder_view.htm", "", params, pList, mv);
			GroupLifeGoods goods = this.groupLifeGoodsService
					.getObjById(CommUtil.null2Long(json.get("goods_id")));
			mv.addObject("infos", pList.getResult());
			mv.addObject("order", obj);
			mv.addObject("goods", goods);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/group.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "消费码退款", value = "/admin/grouplife_return_confirm.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_return_confirm.htm")
	public ModelAndView grouplife_return_confirm(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/grouplife_return_confirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo info = this.groupinfoService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", info);
		return mv;
	}

	@SecurityMapping(title = "消费码退款保存", value = "/admin/grouplife_return_confirm_save.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/grouplife_return_confirm_save.htm")
	public String grouplife_return_confirm_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupinfoService.getObjById(CommUtil
				.null2Long(id));
		info.setStatus(5);// 自营确认退款，后平台进行退款
		this.groupinfoService.update(info);
		return "redirect:/admin/grouplife_selfinfo.htm";
	}

	@SecurityMapping(title = "团购码管理", value = "/admin/group_code.htm*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping("/admin/group_code.htm")
	public ModelAndView group_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/group_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "生活购订单使用", value = "/admin/use_lifeinfo.htm*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping("/admin/use_lifeinfo.htm")
	public String use_lifeinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupinfoService.getObjById(CommUtil
				.null2Long(id));
		if (info != null && info.getStatus() == 0) {
			info.setStatus(1);
			this.groupinfoService.update(info);
		}
		return "redirect:" + "/admin/grouplife_selforder.htm";
	}

	@SecurityMapping(title = "生活购订单使用", value = "/admin/check_group_code.htm*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping("/admin/check_group_code.htm")
	public void check_group_code(HttpServletRequest request,
			HttpServletResponse response, String value) {
		String code = "0";// 不存在
		GroupInfo info = this.groupinfoService.getObjByProperty(null,
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
					this.groupinfoService.update(info);
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
}
