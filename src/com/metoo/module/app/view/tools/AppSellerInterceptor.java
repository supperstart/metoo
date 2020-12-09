package com.metoo.module.app.view.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: AppSellerInterceptor.java
 * </p>
 * 
 * <p>
 * Description: 卖家信息验证
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.koala.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-4-13
 * 
 * @version 1.0
 */
public class AppSellerInterceptor implements HandlerInterceptor {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception exc)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj, ModelAndView mv)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = true;
		String msg = "用户信息正确";
		Map json_map = new HashMap();
		String verify = CommUtil.null2String(request.getHeader("verify"));
		String user_id = request.getParameter("user_id");
		String token = request.getParameter("token");
		if (user_id == null || token == null) {
			msg = "缺少参数";
			flag = false;
		}
		if (user_id != null) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user == null) {
				msg = "指定参数错误";
				flag = false;
			}
		}
		if (user_id != null && token != null) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (!user.getApp_seller_login_token().equals(
						token.toLowerCase())) {
					msg = "验证信息错误";
					flag = false;
				} else {
					if (!this.verify_user(user, verify)) {
						msg = "验证信息错误";
						flag = false;
					}
				}
			} else {
				msg = "用户信息错误";
				flag = false;
			}
		}
		if (verify == null || verify.equals("")) {
			msg = "非指定设备发送请求";
			flag = false;
		}
		if (!flag) {
			json_map.put("ret", "false");
			json_map.put("verify", "false");
			json_map.put("code", -100);
			json_map.put("msg", msg);
			String json = Json.toJson(json_map, JsonFormat.compact());
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
		return flag;
	}

	/**
	 * 验证用户登录App后是否在pc端修改密码
	 * 
	 * @param user
	 * @param verify
	 * @return
	 */
	private boolean verify_user(User user, String verify) {
		boolean ret = false;
		String app_verify = user.getPassword() + user.getApp_login_token();
		app_verify = Md5Encrypt.md5(app_verify).toLowerCase();
		if (app_verify.equals(verify)) {
			ret = true;
		}
		return ret;
	}
}
