package com.gdufs.edu.service;

import java.util.List;

import com.gdufs.edu.base.BaseService;
import com.gdufs.edu.model.Comment;

public interface CommentService extends BaseService<Comment>{

	void deleCommentsByQuestionId(int q_id);

	List<Comment> queryCommentsByQid(int q_id);
}
