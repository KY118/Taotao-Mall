package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {

	EasyUIDataGridResult getContentList(Long categoryId,Integer page,Integer rows);
	
	TaotaoResult addContent(TbContent content);
	
	TaotaoResult editContent(TbContent tbContent);
	
	TaotaoResult deleteContent(List<Long> ids);
	
	public List<TbContent> getContentListByCatId(Long categoryId);
	
	
}
