package com.metoo.core.constant;

/**
 * 
 * <p>
 * Title: Globals.java
 * </p>
 * 
 * <p>
 * Description: 系统常量类，这里的常量是系统默认值，可以在系统中进行用户定制
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>

 */
public class Globals {
	public final static String DEFAULT_SYSTEM_TITLE = "metoo B2B2C商城系统";// 系统默认名称
	public final static boolean SSO_SIGN = true;// 是否允许单点登录，店铺二级域名开启
	public final static int DEFAULT_SHOP_VERSION = 20170214;// 软件发布小版本号
	public final static String DEFAULT_SHOP_OUT_VERSION = "V2.0";// 软件大版本号
	public final static String DEFAULT_WBESITE_NAME = "metoo";// 软件名称
	public final static String DEFAULT_CLOSE_REASON = "系统维护中...";// 系统关闭原因默认值
	public final static String DEFAULT_THEME = "blue";// 系统管理后台默认风格
	public final static String UPLOAD_FILE_PATH = "upload";// 默认文件上传路径
	public final static String DEFAULT_SYSTEM_LANGUAGE = "zh_cn";// 默认系统语言为简体中文
	public final static String DEFAULT_SYSTEM_PAGE_ROOT = "WEB-INF/templates/";// 默认系统页面根路径
	public final static String SYSTEM_MANAGE_PAGE_PATH = "WEB-INF/templates/uae-en/system/";// 默认系统后台页面路径为
	public final static String SYSTEM_FORNT_PAGE_PATH = "WEB-INF/templates/uae-en/shop/";// 默认系统页面英文前台路径
	public final static String SYSTEM_FORNT_PAGE_PATH_uae = "WEB-INF/templates/uae-ar/";// 默认系统页面阿拉伯文前台路径
	public final static String SYSTEM_DATA_BACKUP_PATH = "data";// 系统数据备份默认路径
	public final static Boolean SYSTEM_UPDATE = true;// 系统是否需要升级,预留字段
	public final static boolean SAVE_LOG = true;// 是否记录日志
	public final static String SECURITY_CODE_TYPE = "normal";// 默认验证码类型
	public final static boolean STORE_ALLOW = true;// 是否可以申请开店
	public final static boolean EAMIL_ENABLE = true;// 邮箱默认开启
	public final static String DEFAULT_IMAGESAVETYPE = "sidImg";// 默认图片存储路径格式
	public final static int DEFAULT_IMAGE_SIZE = 1024;// 默认上传图片最大尺寸,单位为KB
	public final static String DEFAULT_IMAGE_SUFFIX = "gif|jpg|jpeg|bmp|png|tbi";// 默认上传图片扩展名
	public final static int DEFAULT_IMAGE_SMALL_WIDTH = 160;// 默认商城小图片宽度
	public final static int DEFAULT_IMAGE_SMALL_HEIGH = 160;// 默认商城小图片高度
	public final static int DEFAULT_IMAGE_MIDDLE_WIDTH = 300;// 默认商城中图片宽度
	public final static int DEFAULT_IMAGE_MIDDLE_HEIGH = 300;// 默认商城中图片高度
	public final static int DEFAULT_IMAGE_BIG_WIDTH = 1024;// 默认商城大图片宽度
	public final static int DEFAULT_IMAGE_BIG_HEIGH = 1024;// 默认商城大图片高度
	public final static int DEFAULT_COMPLAINT_TIME = 30;// 默认投诉时效时间
	public final static String DEFAULT_TABLE_SUFFIX = "metoo_";// 默认表前缀名
	public final static String THIRD_ACCOUNT_LOGIN = "koala_thid_login_";// 第三方账号登录的前缀
	public final static String DEFAULT_SMS_URL = "http://121.37.40.49:8009/sys_port/gateway/";// 暂时使用第三方，以后公司会接入自己的接口
	
}
