package com.gdufs.edu.service;

import java.util.List;

import com.gdufs.edu.base.BaseService;
import com.gdufs.edu.model.Page;
import com.gdufs.edu.model.Question;

public interface QuestionService extends BaseService<Question>{

	List<Question> querySelfQuestionList(int userId);

	Page queryQuestionListByPage(Page page);

	List<Question> queryQuestionList();
}
