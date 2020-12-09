package com.metoo.foundation.service.impl;

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
import com.metoo.foundation.domain.PointRecord;
import com.metoo.foundation.service.IPointRecordService;

@Service
@Transactional
public class PointRecordServiceImpl implements IPointRecordService{
					  
	@Resource(name = "pointRecordDAo")
	private IGenericDAO<PointRecord> pointRecordDao;
	
	@Override
	public boolean save(PointRecord pr) {
		// TODO Auto-generated method stub
		try {
			this.pointRecordDao.save(pr);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@Override
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		 try {
				this.pointRecordDao.remove(id);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
	}

	@Override
	public boolean update(PointRecord pr) {
		// TODO Auto-generated method stub
		try {
			this.pointRecordDao.update(pr);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@Override
	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(PointRecord.class, construct,
				query, params, this.pointRecordDao);
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

	@Override
	public PointRecord getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.pointRecordDao.get(id);
	}

	@Override
	public PointRecord getObjByProperty(String construct, String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.pointRecordDao.getBy(construct, propertyName, value);
	}

	@Override
	public List<PointRecord> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.pointRecordDao.query(query, params, begin, max);
	}

}
