package com.gdufs.edu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gdufs.edu.base.BaseDao;
import com.gdufs.edu.base.impl.BaseServiceImpl;
import com.gdufs.edu.model.Comment;
import com.gdufs.edu.service.CommentService;
@Service("commentService")
public class CommentServiceImpl extends BaseServiceImpl<Comment> implements
		CommentService {
	/**
	 * 重写该方法,目的是为了覆盖超类中该方法的注解,指明注入指定的Dao对象,否则spring
	 * 无法确定注入哪个Dao---有四个满足条件的Dao.
	 */
	@Resource(name="commentDao")
	public void setDao(BaseDao<Comment> dao) {
		super.setDao(dao);
	}

	@Override
	public void deleCommentsByQuestionId(int q_id) {
		String hql = "DELETE FROM Comment c WHERE c.questionId=?";
		dao.batchEntityByHQL(hql, q_id);
	}

	@Override
	public List<Comment> queryCommentsByQid(int q_id) {
		String hql = "FROM Comment c WHERE c.questionId=?";
		return dao.findEntityByHQL(hql, q_id);
	}
}
