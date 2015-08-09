package com.gdufs.edu.service;

import com.gdufs.edu.base.BaseService;
import com.gdufs.edu.model.ResFile;

public interface ResfileService extends BaseService<ResFile> {

	ResFile findFileByNickInstance(ResFile resFile);
}
