package com.metoo.manage.admin.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.foundation.domain.Payment;
import com.metoo.foundation.service.IPaymentService;
import com.metoo.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: PaymentTools.java
 * </p>
 * 
 * <p>
 * Description: 支付方式处理工具类，用来管理支付方式信息，主要包括查询支付方式等
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
 * @date 2014-5-25
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Component
public class PaymentTools {
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IUserService userService;

	public boolean queryPayment(String mark) {
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		if (objs.size() > 0) {
			// System.out.println(objs.get(0).isInstall());
			return objs.get(0).isInstall();
		} else
			return false;
	}

	public Map queryShopPayment(String mark) {
		Map ret = new HashMap();
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("content", objs.get(0).getContent());
		} else {
			ret.put("install", false);
			ret.put("content", "");
		}
		return ret;
	}
}
