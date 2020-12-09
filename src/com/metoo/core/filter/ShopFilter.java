package com.metoo.core.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: ShopFilter.java
 * </p>
 * 
 * <p>
 * Description:系統基础过滤器，主要用在系统第一次运行时进入安装界面，引导用户安装系统，导入系统基础数据信息，
 * 对于资源信息如css等该过滤器将不进行首次安装拦截
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 湖南创发科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Component
public class ShopFilter implements Filter {
	
	@Autowired
	private ISysConfigService configService;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		SysConfig config = this.configService.getSysConfig();
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		String url = request.getRequestURI();
		boolean redirect = false;
		String redirect_url = "";
		String path = request.getSession().getServletContext().getRealPath("/")
				+ "install.lock";
		File file = new File(path);
		/*System.out.println("当前url:" + url);
		System.out.println(path);*/
		if (file.exists()) {
			if (!config.isWebsiteState()) {
				if (this.init_url(url)) {
					if (url.indexOf("/admin") < 0
							&& url.indexOf("/install.htm") <= 0
							&& url.indexOf("/close.htm") < 0
							&& url.indexOf("/verify.json") < 0
							&& url.indexOf("login_success.json") < 0) {
						redirect = true;
						redirect_url = CommUtil.getURL(request) + "/close.htm";
					}
					if (url.indexOf("/mobile") >= 0) {
						redirect = true;
						redirect_url = CommUtil.getURL(request)
								+ "/mobile/close.htm";
					}
				}
			} else {
				User user = SecurityUserHolder.getCurrentUser();
				if (user != null) {
					if (url.indexOf("/login.htm") >= 0) {
						redirect = true;
						redirect_url = CommUtil.getURL(request) + "/index.htm";
					}
					if (url.indexOf("/register.htm") >= 0) {
						redirect = true;
						redirect_url = CommUtil.getURL(request) + "/index.htm";
					}
				} else {
					if (url.indexOf("/install") >= 0) {
						redirect_url = CommUtil.getURL(request) + "/index.htm";
						redirect = true;
					}
				}
			}
		} else {
			if (this.init_url(url)) {
				redirect_url = CommUtil.getURL(request) + "/install.htm";
				redirect = true;
				if (url.indexOf("/install") >= 0) {
					redirect = false;
				}
			}
		}
		if (redirect) {
			response.sendRedirect(redirect_url);
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	private boolean init_url(String url) {
		String prifix = "";
		if (url.indexOf(".") > 0) {
			prifix = url.substring(url.lastIndexOf(".") + 1);
		} else {
			prifix = url;
		}
		String[] extend_list = new String[] { "css", "jpg", "jpeg", "png",
				"gif", "bmp", "tbi", "js" };
		boolean flag = true;
		for (String temp : extend_list) {
			if (temp.equals(prifix)) {
				flag = false;
			}
		}

		return flag;
	}
}
