﻿package com.metoo.core.ip;

/**
 * 
 * <p>
 * Title: IPtest.java
 * </p>
 * 
 * <p>
 * Description:
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

public class IPtest {
	public static void main(String[] args) {
		// 指定纯真数据库的文件名，所在文件夹
		IPSeeker ip = new IPSeeker("QQWry.Dat", "f:/");
		String temp = "169.254.111.173";
		// 测试IP 58.20.43.13
		System.out.println(ip.getIPLocation(temp).getCountry() + ":"
				+ ip.getIPLocation(temp).getArea());
	}

}
