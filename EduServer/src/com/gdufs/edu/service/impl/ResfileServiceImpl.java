package com.gdufs.edu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gdufs.edu.base.BaseDao;
import com.gdufs.edu.base.impl.BaseServiceImpl;
import com.gdufs.edu.model.ResFile;
import com.gdufs.edu.service.ResfileService;
import com.gdufs.edu.util.CommonUtil;
@Service("resfileService")
public class ResfileServiceImpl extends BaseServiceImpl<ResFile> implements
		ResfileService {
	/**
	 * 重写该方法,目的是为了覆盖超类中该方法的注解,指明注入指定的Dao对象,否则spring
	 * 无法确定注入哪个Dao---有四个满足条件的Dao.
	 */
	@Resource(name="resfileDao")
	public void setDao(BaseDao<ResFile> dao) {
		super.setDao(dao);
	}

	@Override
	public ResFile findFileByNickInstance(ResFile resFile) {
		String hql = "FROM ResFile r WHERE r.nickname=? and r.filename=?";
		List<ResFile> list = dao.findEntityByHQL(hql, resFile.getNickname(),resFile.getFilename());
		if(CommonUtil.isValid(list)){
			return list.get(0);
		}
		return null;
	}

}
