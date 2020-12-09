package com.metoo.core.security.support;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.AccessDeniedHandler;
import org.springframework.security.ui.AccessDeniedHandlerImpl;

import com.metoo.foundation.domain.User;

/**
 * 
 * <p>
 * Title: ShopAccessDeniedHandlerImpl.java
 * </p>
 * 
 * <p>
 * Description:
 * 重写SpringSecurity权限控制接口，管理系统无权限操作的页面导向，及用户多次登录分别加载对应权限信息，系统可以采用多种登陆机制，
 * 用户从前台登陆，仅仅加载非管理员权限，从管理员页面登陆，通过该控制器加载管理员权限
 * </p>
 * description:AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
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
 * @version koala_b2b2c 2.0
 */
public class ShopAccessDeniedHandlerImpl implements AccessDeniedHandler {
	public static final String SPRING_SECURITY_ACCESS_DENIED_EXCEPTION_KEY = "SPRING_SECURITY_403_EXCEPTION";
	protected static final Log logger = LogFactory
			.getLog(AccessDeniedHandlerImpl.class);
	private String errorPage;

	public void handle(ServletRequest request, ServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {
		this.errorPage = "/authority.htm";
		User user = SecurityUserHolder.getCurrentUser();
		
		GrantedAuthority[] all_authorities = user.get_all_Authorities();
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		GrantedAuthority[] current_authorities = auth.getAuthorities();
		if (user.getUserRole().indexOf("ADMIN") < 0) {
			this.errorPage = "/buyer/authority.htm";
		} else {
			// for(GrantedAuthority auth1:all_authorities){
			// boolean p=false;
			// for(GrantedAuthority auth2:current_authorities){
			// if(auth1.toString().equals(auth2.toString())){
			// p=true;
			// }
			// }
			// if(p){
			// System.out.println("共同存在:"+auth1.toString());
			// }else{
			// System.out.println("=========================================不存在:"+auth1.toString());
			// }
			// }
			if (all_authorities.length != current_authorities.length) {
				this.errorPage = "/admin/login.htm";
			}
		}
		if (this.errorPage != null) {
			((HttpServletRequest) request).setAttribute(
					"SPRING_SECURITY_403_EXCEPTION", accessDeniedException);

			RequestDispatcher rd = request.getRequestDispatcher(this.errorPage);
			rd.forward(request, response);
		}

		if (!response.isCommitted()) {
			((HttpServletResponse) response).sendError(403,
					accessDeniedException.getMessage());
		}
	}

	public void setErrorPage(String errorPage) {
		if ((errorPage != null) && (!errorPage.startsWith("/"))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}

		this.errorPage = errorPage;
	}

}
