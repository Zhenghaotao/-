package com.gdufs.edu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gdufs.edu.base.BaseDao;
import com.gdufs.edu.base.impl.BaseServiceImpl;
import com.gdufs.edu.model.Page;
import com.gdufs.edu.model.Question;
import com.gdufs.edu.model.User;
import com.gdufs.edu.service.QuestionService;
@Service("questionService")
public class QuestionServiceImpl extends BaseServiceImpl<Question> implements
		QuestionService {
	/**
	 * 重写该方法,目的是为了覆盖超类中该方法的注解,指明注入指定的Dao对象,否则spring
	 * 无法确定注入哪个Dao---有四个满足条件的Dao.
	 */
	@Resource(name="questionDao")
	public void setDao(BaseDao<Question> dao) {
		super.setDao(dao);
	}

	@Override
	public List<Question> querySelfQuestionList(int userId) {
		String hql = "FROM Question q WHERE q.userId = ? order by id desc";
		List<Question> list = dao.findEntityByHQL(hql, userId);
		
		return list;
	}
	/**
	 * 暂时没用
	 */
	@Override
	public Page queryQuestionListByPage(Page page) {
		String countHql = "SELECT COUNT(*) FROM Question q";
		int count = (int) dao.uniqueResult(countHql, null);
		page.setCount(count);
		return page;
	}

	@Override
	public List<Question> queryQuestionList() {
		String hql = "FROM Question q order by id desc";
		List<Question> list = dao.findEntityByHQL(hql);
		
		return list;
	}
	
	
}