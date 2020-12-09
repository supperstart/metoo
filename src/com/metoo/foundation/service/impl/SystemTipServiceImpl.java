package com.metoo.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.foundation.domain.SystemTip;
import com.metoo.foundation.service.ISystemTipService;

@Service
@Transactional
public class SystemTipServiceImpl implements ISystemTipService{
	@Resource(name = "systemTipDAO")
	private IGenericDAO<SystemTip> systemTipDao;
	
	public boolean save(SystemTip systemTip) {
		/**
		 * init other field here
		 */
		try {
			this.systemTipDao.save(systemTip);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public SystemTip getObjById(Long id) {
		SystemTip systemTip = this.systemTipDao.get(id);
		if (systemTip != null) {
			return systemTip;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.systemTipDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> systemTipIds) {
		// TODO Auto-generated method stub
		for (Serializable id : systemTipIds) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(SystemTip.class,construct, query,
				params, this.systemTipDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(SystemTip systemTip) {
		try {
			this.systemTipDao.update( systemTip);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<SystemTip> query(String query, Map params, int begin, int max){
		return this.systemTipDao.query(query, params, begin, max);
		
	}
}
