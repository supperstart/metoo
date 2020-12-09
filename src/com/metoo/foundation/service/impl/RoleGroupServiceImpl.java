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
import com.metoo.foundation.domain.RoleGroup;
import com.metoo.foundation.service.IRoleGroupService;

@Service
@Transactional
public class RoleGroupServiceImpl implements IRoleGroupService {
	@Resource(name = "roleGroupDAO")
	private IGenericDAO<RoleGroup> roleGroupDao;

	public boolean save(RoleGroup roleGroup) {
		/**
		 * init other field here
		 */
		try {
			this.roleGroupDao.save(roleGroup);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public RoleGroup getObjById(Long id) {
		RoleGroup roleGroup = this.roleGroupDao.get(id);
		if (roleGroup != null) {
			return roleGroup;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.roleGroupDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> roleGroupIds) {
		// TODO Auto-generated method stub
		for (Serializable id : roleGroupIds) {
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
		GenericPageList pList = new GenericPageList(RoleGroup.class,construct, query,
				params, this.roleGroupDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(RoleGroup roleGroup) {
		try {
			this.roleGroupDao.update(roleGroup);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<RoleGroup> query(String query, Map params, int begin, int max) {
		return this.roleGroupDao.query(query, params, begin, max);

	}

	@Override
	public RoleGroup getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		return this.roleGroupDao.getBy(construct, propertyName, value);
	}
}
