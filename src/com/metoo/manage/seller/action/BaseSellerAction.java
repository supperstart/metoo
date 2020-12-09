package com.metoo.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Article;
import com.metoo.foundation.domain.Complaint;
import com.metoo.foundation.domain.Consult;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.ReturnGoodsLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IArticleService;
import com.metoo.foundation.service.IComplaintService;
import com.metoo.foundation.service.IConsultService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IMessageService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IPayoffLogService;
import com.metoo.foundation.service.IReturnGoodsLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.seller.tools.MenuTools;
import com.metoo.view.web.tools.AreaViewTools;
import com.metoo.view.web.tools.OrderViewTools;
import com.metoo.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: BaseSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台基础管理器 主要功能包括商家后台的基础管理、快捷菜单设置等
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
 * @date 2014-6-10
 * 
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Controller
public class BaseSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private IPayoffLogService payofflogService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IComplaintService complaintService;
	@Autowired
	private MenuTools menuTools;
	@Autowired
	private IConsultService consultService;

	/**
	 * 商城商家登录入口，商家登录后只能进行商家操作，不能进行购物等其他操作，系统严格区分商家、买家、管理员
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("verify_code");// 如果系统未开启前台登录验证码，则需要移除session中保留的验证码信息
		return mv;
	}

	/**
	 * 商家后台顶部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 商家中心首页，该请求受系统ss权限管理，对应角色名为"商家中心",商家中心添加子账户时默认添加“商家中心”权限，“
	 * user_center_seller”不可更改
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/seller/index.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String name1 = user.getNickName();
		String name2 = user.getTrueName();
		String name3 = user.getUserName();
		Map params = new HashMap();
		params.put("class_mark", "new_func");
		params.put("display", true);
		List<Article> func_articles = this.articleService
				.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		params.clear();
		params.put("type", "store");// 只查询给商家看的文章信息
		params.put("display", true);
		params.put("class_mark", "new_func");
		List<Article> articles = this.articleService
				.query("select obj from Article obj where obj.type=:type and obj.articleClass.mark!=:class_mark and obj.display=:display order by obj.addTime desc",
						params, 0, 5);
		params.clear();
		params.put("store_id", user.getStore().getId());
		params.put("goods_status", 0);
		List<Goods> goods_sale_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, 5);
		params.clear();
		params.put("store_id", user.getStore().getId());
		params.put("goods_return_status", "5");
		List<ReturnGoodsLog> returngoods = this.returngoodslogService
				.query("select obj from ReturnGoodsLog obj where obj.store_id=:store_id and obj.goods_return_status=:goods_return_status order by addTime desc",
						params, -1, -1);
		params.clear();
		params.put("store_id", user.getStore().getId().toString());
		List<OrderForm> orders = this.orderformService
				.query("select obj from OrderForm obj where obj.store_id=:store_id order by addTime desc",
						params, -1, -1);
		params.clear();
		params.put("status", false);
		params.put("store_id",  user.getStore().getId());
		List<Consult> msgs = this.consultService
				.query("select obj from Consult obj where obj.reply=:status and obj.store_id=:store_id ",
						params, -1,-1);
		params.clear();
		params.put("status", 0);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Complaint> complaints = this.complaintService
				.query("select obj from Complaint obj where obj.to_user.id=:user_id and obj.status=:status",
						params, -1, -1);
		mv.addObject("complaints", complaints);
		mv.addObject("msgs", msgs);
		mv.addObject("orders", orders);
		mv.addObject("returngoods", returngoods);
		mv.addObject("goods_sale_list", goods_sale_list);
		mv.addObject("articles", articles);
		mv.addObject("user", user);
		mv.addObject("store", user.getStore());
		mv.addObject("func_articles", func_articles);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		mv.addObject("menuTools", menuTools);
		return mv;
	}

	@SecurityMapping(title = "商家中心导航", value = "/seller/nav.htm*", rtype = "seller", rname = "商家中心导航", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("op", op);
		mv.addObject("user", user);
		int store_status = (user.getStore() == null ? 0 : user.getStore()
				.getStore_status());
		if (store_status != 15) {
			mv.addObject("limit", true);
		}
		return mv;
	}

	@SecurityMapping(title = "商家中心快捷功能设置保存", value = "/seller/store_quick_menu_save.htm*", rtype = "seller", rname = "商家中心快捷功能设置保存", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/store_quick_menu_save.htm")
	public void store_quick_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menus) {
		String[] menu_navs = menus.split(";");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> list = new ArrayList<Map>();
		for (String menu_nav : menu_navs) {
			if (!menu_nav.equals("")) {
				String[] infos = menu_nav.split(",");
				Map map = new HashMap();
				map.put("menu_name", infos[0]);
				map.put("menu_url", infos[1]);
				list.add(map);
			}
		}
		store.setStore_quick_menu(Json.toJson(list, JsonFormat.compact()));
		this.storeService.update(store);
		String ret = Json.toJson(list, JsonFormat.compact());
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

	/**
	 * 商家后台操作成功提示页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seller/success.htm")
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 商家后台操作错误提示页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/seller/error.htm")
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 店铺到期关闭后，商家申请续费重新开店
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "商家中心申请重新开店", value = "/seller/store_renew.htm*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping("/seller/store_renew.htm")
	public void store_renew(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Boolean ret = false;
		if (user.getStore() != null) {
			Store store = user.getStore();
			store.setStore_status(26);
			this.storeService.update(store);
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
}
