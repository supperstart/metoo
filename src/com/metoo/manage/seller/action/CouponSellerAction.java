package com.metoo.manage.seller.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

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
import com.metoo.core.service.IQueryService;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Coupon;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.CouponInfoQueryObject;
import com.metoo.foundation.domain.query.CouponQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.ICouponInfoService;
import com.metoo.foundation.service.ICouponService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreGradeService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;

/**
 * 
 * 
 * <p>
 * Title:CouponSeller.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心优惠劵控制器
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
 * @author jy
 * 
 * @date 2014年5月5日
 * 
 * @version koala_b2b2c 2.0
 */
@Controller
public class CouponSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponinfoService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IQueryService queryService;
	@Autowired
	private IStoreService storeService;

	/**
	 * 优惠券列表信息页面，分页显示优惠券列表信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return 优惠券列表信息页
	 */
	@SecurityMapping(title = "优惠券列表", value = "/seller/coupon.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon.htm")
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_name, String coupon_begin_time,
			String coupon_end_time) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/coupon_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
		if (!CommUtil.null2String(coupon_name).equals("")) {
			qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%"
					+ coupon_name + "%"), "like");
		}
		if (!CommUtil.null2String(coupon_begin_time).equals("")) {
			qo.addQuery(
					"obj.coupon_begin_time",
					new SysMap("coupon_begin_time", CommUtil
							.formatDate(coupon_begin_time)), ">=");
		}
		if (!CommUtil.null2String(coupon_end_time).equals("")) {
			qo.addQuery("obj.coupon_end_time", new SysMap("coupon_end_time",
					CommUtil.formatDate(coupon_end_time)), "<=");
		}
		IPageList pList = this.couponService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 添加优惠券信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券添加", value = "/seller/coupon_add.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_add.htm")
	public ModelAndView coupon_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/coupon_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 编辑优惠券信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券编辑", value = "/seller/coupon_edit.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_edit.htm")
	public ModelAndView coupon_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/coupon_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", coupon);
		mv.addObject("edit", true);
		return mv;
	}

	/**
	 * 优惠券保存，保存或者更新一个优惠券信息
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存", value = "/seller/coupon_save.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_save.htm")
	public void coupon_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		WebForm wf = new WebForm();
		Coupon coupon = null;
		if (id.equals("")) {
			coupon = wf.toPo(request, Coupon.class);
			coupon.setAddTime(new Date());
		} else {
			coupon = this.couponService.getObjById(Long.parseLong(id));
			coupon = (Coupon) wf.toPo(request, coupon);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		/*String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "coupon";*/
		String saveFilePathName = uploadFilePath + "/" + "coupon";
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map map = new HashMap();
		try {
			map = CommUtil.httpsaveFileToServer(request, "coupon_img",
					saveFilePathName, null, null);
			if (map.get("fileName") != "") {
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(CommUtil.null2String(map.get("fileName")));
				coupon_acc.setExt((String) map.get("mime"));
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setWidth(CommUtil.null2Int(map.get("width")));
				coupon_acc.setHeight(CommUtil.null2Int(map.get("height")));
				coupon_acc.setAddTime(new Date());
				this.accessoryService.save(coupon_acc);
				String pressImg = saveFilePathName + File.separator
						+ coupon_acc.getName();
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				String targetImg = saveFilePathName + "/"
						+ targetImgName;
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(pressImg, targetImg,
							"满 " + coupon.getCoupon_order_amount() + " 减",
							"#726960", font, 95, 90, 1);
					font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(
							targetImg,
							targetImg,
							this.configService.getSysConfig()
									.getCurrency_code()
									+ coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
				} catch (Exception e) {

				}
				coupon_acc.setName(targetImgName);
				File file = new File(pressImg);
				file.delete();
				this.accessoryService.update(coupon_acc);
				coupon.setCoupon_acc(coupon_acc);
			} else {
				String pressImg = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ "resources"
						+ File.separator
						+ "style"
						+ File.separator
						+ "common"
						+ File.separator
						+ "template" + File.separator + "coupon_template.jpg";
				String targetImgPath = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ uploadFilePath
						+ File.separator
						+ "coupon" + File.separator;
				if (!CommUtil.fileExist(targetImgPath)) {
					CommUtil.createFolder(targetImgPath);
				}
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				try {
					Font font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(
							pressImg,
							targetImgPath + targetImgName,
							this.configService.getSysConfig()
									.getCurrency_code()
									+ coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
					font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(targetImgPath + targetImgName,
							targetImgPath + targetImgName,
							"满 " + coupon.getCoupon_order_amount() + " 减",
							"#726960", font, 95, 90, 1);
				} catch (Exception e) {

				}
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(targetImgName);
				coupon_acc.setExt("jpg");
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setAddTime(new Date());
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil
						.null2Double(28.4)));
				this.accessoryService.save(coupon_acc);
				coupon.setCoupon_acc(coupon_acc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			coupon.setCoupon_type(1);// 设置为商家发布
			coupon.setStore(store);
			this.couponService.save(coupon);
		} else {
			this.couponService.update(coupon);
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "优惠券保存成功");
		json.put("url", CommUtil.getURL(request) +  "/seller/coupon.htm?currentPage=" + currentPage);
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@SecurityMapping(title = "优惠券保存成功", value = "/seller/coupon_success.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_success.htm")
	public ModelAndView coupon_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/coupon.htm?currentPage=" + currentPage);
		mv.addObject("op_title", "优惠券保存成功");
		return mv;
	}

	/**
	 * 删除优惠券
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券删除", value = "/seller/coupon_del.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_del.htm")
	public String coupon_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		if(coupon!=null&&coupon.getStore().getId().equals(user.getStore().getId())){
			Accessory acc = coupon.getCoupon_acc();// 删除优惠券图片
			this.couponService.delete(CommUtil.null2Long(id));
			boolean ret = this.accessoryService.delete(acc.getId());
			if (ret) {
				CommUtil.del_acc(request, acc);
			}
		}
		return "redirect:coupon.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "优惠券发放", value = "/seller/coupon_send.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_send.htm")
	public ModelAndView coupon_send(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/coupon_send.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		mv.addObject("obj",
				this.couponService.getObjById(CommUtil.null2Long(id)));
		return mv;
	}

	@SecurityMapping(title = "优惠券发放保存", value = "/seller/coupon_send_save.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_send_save.htm")
	public void coupon_send_save(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String users,
			String order_amount, String currentPage) throws IOException {
		List<User> user_list = new ArrayList<User>();
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		Store store = coupon.getStore();
		if (type.equals("all_user")) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			params.put("user_id", store.getUser().getId());
			user_list = this.userService
					.query("select obj from User obj where obj.userRole!=:userRole and obj.id!= :user_id order by obj.user_goods_fee desc",
							params, -1, -1);
		}
		if (type.equals("the_user")) {
			List<String> user_names = CommUtil.str2list(users);
			for (String user_name : user_names) {
				User user = this.userService.getObjByProperty(null,"userName",
						user_name);
				if (user.getId() != CommUtil.null2Long(store.getUser().getId())) {// 排除当前用户
					user_list.add(user);
				}
			}
		}
		if (type.equals("the_order")) {
			Map params = new HashMap();
			params.put("order_status", 50);
			params.put("store_id", CommUtil.null2String(store.getId()));
			List list = this.queryService
					.query("select obj.user_id,sum(obj.totalPrice) from OrderForm obj where obj.order_status>=:order_status and obj.store_id= :store_id group by obj.user_id",
							params, -1, -1);

			for (int i = 0; i < list.size(); i++) {
				Object[] list1 = (Object[]) list.get(i);
				Long user_id = CommUtil.null2Long(list1[0]);
				double order_total_amount = CommUtil.null2Double(list1[1]);
				if (order_total_amount > CommUtil.null2Double(order_amount)) {
					User user = this.userService.getObjById(user_id);
					user_list.add(user);
				}
			}
		}

		for (int i = 0; i < user_list.size(); i++) {
			if (coupon.getCoupon_count() > 0) {
				if (i < coupon.getCoupon_count()) {
					CouponInfo info = new CouponInfo();
					info.setAddTime(new Date());
					info.setCoupon(coupon);
					info.setStore_id(store.getId());
					info.setCoupon_sn(UUID.randomUUID().toString());
					info.setUser(user_list.get(i));
					this.couponinfoService.save(info);
				} else
					break;
			} else {
				CouponInfo info = new CouponInfo();
				info.setStore_id(store.getId());
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user_list.get(i));
				this.couponinfoService.save(info);
			}
		}
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "优惠券发放成功");
		json.put("url", CommUtil.getURL(request) +  "/seller/coupon.htm?currentPage=" + currentPage);
		this.return_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	@SecurityMapping(title = "优惠券详细信息", value = "/seller/coupon_ajax.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_info_list.htm")
	public ModelAndView coupon_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/coupon_info_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.coupon.id",
				new SysMap("coupon_id", CommUtil.null2Long(coupon_id)), "=");
		IPageList pList = this.couponinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/seller/coupon_info_list.htm", "", params, pList, mv);
		mv.addObject("coupon_id", coupon_id);
		return mv;
	}

	private static boolean waterMarkWithText(String filePath, String outPath,
			String text, String markContentColor, Font font, int left, int top,
			float qualNum) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("宋体", Font.BOLD, 20);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(CommUtil.getColor(markContentColor));
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		g.drawImage(theImg, 0, 0, null);
		FontMetrics metrics = new FontMetrics(font) {
		};
		g.drawString(text, left, top); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage,
					filePath.substring(filePath.lastIndexOf(".") + 1), out);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
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
}
