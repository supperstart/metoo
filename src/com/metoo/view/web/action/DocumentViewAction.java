package com.metoo.view.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.mv.JModelAndView;
import com.metoo.foundation.domain.Article;
import com.metoo.foundation.domain.ArticleClass;
import com.metoo.foundation.domain.Document;
import com.metoo.foundation.service.IArticleClassService;
import com.metoo.foundation.service.IArticleService;
import com.metoo.foundation.service.IDocumentService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;

/**
 * 
* <p>Title: DocumentViewAction.java</p>

* <p>Description:系统文章前台显示控制器 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.koala.com</p>

* @author erikzhang

* @date 2014-5-6

* @version koala_b2b2c v2.0 2015版 
 */
@Controller
public class DocumentViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleClassService articleClassService;
	
	@RequestMapping("/doc.htm")
	public ModelAndView doc(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		ModelAndView mv = new JModelAndView("article.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("doc", "doc");
		Document obj = this.documentService.getObjByProperty(null,"mark", mark);
		mv.addObject("obj", obj);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		List<Article> articles = this.articleService.query(
				"select obj from Article obj order by obj.addTime desc", null,
				0, 6);
		mv.addObject("articles", articles);
		mv.addObject("acs", acs);
		return mv;
	}
}
