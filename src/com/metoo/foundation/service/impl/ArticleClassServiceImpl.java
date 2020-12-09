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
import com.metoo.foundation.domain.ArticleClass;
import com.metoo.foundation.service.IArticleClassService;

@Service
@Transactional
public class ArticleClassServiceImpl implements IArticleClassService {
	@Resource(name = "articleClassDAO")
	private IGenericDAO<ArticleClass> articleClassDao;

	public boolean save(ArticleClass articleClass) {
		/**
		 * init other field here
		 */
		try {
			this.articleClassDao.save(articleClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArticleClass getObjById(Long id) {
		ArticleClass articleClass = this.articleClassDao.get(id);
		if (articleClass != null) {
			return articleClass;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.articleClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> articleClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : articleClassIds) {
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
		GenericPageList pList = new GenericPageList(ArticleClass.class,
				construct, query, params, this.articleClassDao);
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

	public boolean update(ArticleClass articleClass) {
		try {
			this.articleClassDao.update(articleClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ArticleClass> query(String query, Map params, int begin, int max) {
		return this.articleClassDao.query(query, params, begin, max);

	}

	@Override
	public ArticleClass getObjByPropertyName(String construct,
			String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.articleClassDao.getBy(construct, propertyName, value);
	}
}
