package com.metoo.core.tools.database;

import java.sql.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 
 * <p>
 * Title: DbConnection.java
 * </p>
 * 
 * <p>
 * Description: 数据库的连接,使用线程安全管理，确保数据库链接只存在一个，维护系统性能正常
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 湖南**科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version koala_b2b2c v2.0 2015版 
 */
@Repository
@SuppressWarnings("serial")
public class DbConnection {
	@Autowired
	private javax.sql.DataSource dataSource;
	// 线程安全 将线程和连接绑定，保证事务能统一执行   是将connection放进threadlocal里的，以保证每个线程从连接池中获得的都是线程自己的connection。
	public static final ThreadLocal<Connection> thread = new ThreadLocal<Connection>();

	public Connection getConnection() {
		Connection conn = thread.get();
		if (conn == null) {
			try {
				conn = this.dataSource.getConnection();
				thread.set(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	/**
	 * 关闭链接
	 * 
	 * @throws Exception
	 */
	public void closeAll() {
		try {
			Connection conn = thread.get();
			if (conn != null) {
				conn.close();
				thread.set(null);
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
