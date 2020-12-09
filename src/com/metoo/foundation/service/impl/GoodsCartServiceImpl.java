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
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.service.IGoodsCartService;

@Service
@Transactional
public class GoodsCartServiceImpl implements IGoodsCartService{
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	
	public boolean save(GoodsCart goodsCart) {
		/**
		 * init other field here
		 */
		try {
			this.goodsCartDao.save(goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsCart getObjById(Long id) {
		GoodsCart goodsCart = this.goodsCartDao.get(id);
		if (goodsCart != null) {
			return goodsCart;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsCartDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsCartIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsCartIds) {
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
		GenericPageList pList = new GenericPageList(GoodsCart.class,construct, query,
				params, this.goodsCartDao);
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
	
	public boolean update(GoodsCart goodsCart) {
		try {
			this.goodsCartDao.update( goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsCart> query(String query, Map params, int begin, int max){
		return this.goodsCartDao.query(query, params, begin, max);
		
	}
}
