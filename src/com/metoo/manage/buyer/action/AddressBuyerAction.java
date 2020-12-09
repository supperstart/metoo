package com.metoo.manage.buyer.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.app.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.query.AddressQueryObject;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IIntegralGoodsOrderService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: AddressBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:买家中心地址管理控制器，该控制用来添加、修改、删除地址、设置常用地址
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
 * @date 2014-9-17
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Controller
public class AddressBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IIntegralGoodsOrderService igorder;

	private static Result result = new Result();

	/**
	 * Address列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "收货地址列表", value = "/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address.htm")
	public ModelAndView address(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/address.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		AddressQueryObject qo = new AddressQueryObject(currentPage, mv, "default_val asc,obj.addTime", "desc");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
		IPageList pList = this.addressService.list(qo);

		CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		return mv;

	}

	@SecurityMapping(title = "收货地址列表", value = "/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address.json")
	@ResponseBody
	public ModelAndView addresss(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/address.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		AddressQueryObject qo = new AddressQueryObject(currentPage, mv, "default_val desc,obj.addTime", "desc");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
		IPageList pList = this.addressService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		try {
			response.getWriter().print(mv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mv;

	}

	@SecurityMapping(title = "新增收货地址", value = "/buyer/address_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_add.htm")
	public ModelAndView address_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		// [获取省级区域信息]
		ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "编辑收货地址", value = "/buyer/address_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_edit.htm")
	public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {// 只允许修改自己的地址信息
			mv.addObject("obj", obj);
			mv.addObject("areas", areas);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}

	/**
	 * address保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/buyer/address_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_save.htm")
	public String address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id,
			String currentPage) {

		WebForm wf = new WebForm();// 封装添加表单对象
		Address address = null;
		// [如果id为空为添加新增，否则为更新保存]
		if (id.equals("")) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {// 只允许修改自己的地址信息
				address = (Address) wf.toPo(request, obj);
			}
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id.equals("")) {
			this.addressService.save(address);
		} else
			this.addressService.update(address);
		return "redirect:/buyer/address.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "收货地址删除", value = "/buyer/address_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_del.htm")
	public String address_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.getObjById(Long.parseLong(id));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {// 只允许删除自己的地址信息
					this.addressService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:address.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "收货地址默认设置", value = "/buyer/address_default.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_default.htm")
	public String address_default(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.getObjById(Long.parseLong(id));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {// 只允许修改自己的地址信息
					// [设置查询条件]
					Map<String, Number> params = new HashMap<String, Number>();
					params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
					params.put("id", CommUtil.null2Long(id));
					params.put("default_val", 1);// [是否为默认收货地址，1为默认地址]
					// [获取当前用户的默认地址]
					List<Address> addrs = this.addressService.query(
							"select obj from Address obj where obj.user.id=:user_id and obj.id!=:id and obj.default_val=:default_val",
							params, -1, -1);
					// [循环设值为0并更新]
					for (Address addr1 : addrs) {
						addr1.setDefault_val(0);
						this.addressService.update(addr1);
					}
					address.setDefault_val(1);
					this.addressService.update(address);
				}
			}
		}
		return "redirect:address.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "收货地址默认取消", value = "/buyer/address_default_cancle.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/address_default_cancle.htm")
	public String address_default_cancle(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.getObjById(Long.parseLong(id));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {// 只允许修改自己的地址信息
					address.setDefault_val(0);
					this.addressService.update(address);
				}
			}
		}
		return "redirect:address.htm?currentPage=" + currentPage;
	}
}