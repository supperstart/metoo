package com.metoo.manage.admin.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.query.AccessoryQueryObject;
import com.metoo.foundation.domain.query.AlbumQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IAlbumService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.manage.admin.tools.ImageTools;

/**
 * 
 * <p>
 * Title: ImageManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台图片管理控制器，删除数据的同时也删除物理文件
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
 * @author jxz
 * 
 * @date 2014年5月27日
 * 
 * @version koala_b2b2c 2.0
 */
@Controller
public class ImageManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ImageTools ImageTools;

	@SecurityMapping(title = "会员相册列表", value = "/admin/user_photo_list.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_photo_list.htm")
	public ModelAndView user_album_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/photo_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AlbumQueryObject qo = new AlbumQueryObject(currentPage, mv, orderBy,
				orderType);
		if (store_name != null && !store_name.trim().equals("")) {
			qo.addQuery("obj.user.store.store_name", new SysMap(
					"store_store_name", "%" + store_name.trim() + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		qo.addQuery("obj.user.userRole", new SysMap("user_userRole", "ADMIN"),
				"!=");
		qo.setPageSize(15);
		IPageList pList = this.albumService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ImageTools", ImageTools);
		return mv;
	}

	@SecurityMapping(title = "会员相册删除", value = "/admin/user_photo_del.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_photo_del.htm")
	public String user_album_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Album album = this.albumService.getObjById(CommUtil
						.null2Long(id));
				if (album != null) {
					Map params = new HashMap();
					params.put("album_id", album.getId());
					List<Accessory> accs = this.accessoryService
							.query("select obj from Accessory obj where obj.album.id=:album_id",
									params, -1, -1);
					for (Accessory acc : accs) {
						CommUtil.del_acc(request, acc);
						for (Goods goods : acc.getGoods_main_list()) {
							goods.setGoods_main_photo(null);
							this.goodsService.update(goods);
						}
						for (Goods goods1 : acc.getGoods_list()) {
							goods1.getGoods_photos().remove(acc);
							this.goodsService.update(goods1);
						}
						this.accessoryService.delete(acc.getId());
					}
					this.albumService.delete(Long.parseLong(id));
				}
			}
		}
		String url = "redirect:/admin/user_photo_list.htm?currentPage="
				+ currentPage;
		return url;
	}

	@SecurityMapping(title = "会员相册图片列表", value = "/admin/user_pic_list.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_pic_list.htm")
	public ModelAndView user_pic_list(HttpServletRequest request,
			HttpServletResponse response, String aid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/pic_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject qo = new AccessoryQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.album.id",
				new SysMap("obj_album_id", CommUtil.null2Long(aid)), "=");
		qo.setPageSize(50);
		IPageList pList = this.accessoryService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		Album album = this.albumService.getObjById(CommUtil.null2Long(aid));
		mv.addObject("album", album);
		return mv;
	}

	/**
	 * 会员相册图片删除，删除数据的同时删除服务器上的图片资源
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "会员相册图片删除", value = "/admin/user_pic_del.htm*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping("/admin/user_pic_del.htm")
	public String user_pic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId,
			String aid) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			boolean flag = false;
			Accessory obj = this.accessoryService.getObjById(CommUtil
					.null2Long(id));
			for (Goods goods : obj.getGoods_list()) {
				if (goods.getGoods_main_photo().getId().equals(obj.getId())) {
					goods.setGoods_main_photo(null);
					this.goodsService.update(goods);
				}
				goods.getGoods_photos().remove(obj);
			}
			flag = this.accessoryService.delete(CommUtil.null2Long(id));
			if (flag) {
				CommUtil.del_acc(request, obj);
			}
		}
		String url = "redirect:/admin/user_pic_list.htm?currentPage="
				+ currentPage + "&aid=" + aid;
		return url;
	}
}
