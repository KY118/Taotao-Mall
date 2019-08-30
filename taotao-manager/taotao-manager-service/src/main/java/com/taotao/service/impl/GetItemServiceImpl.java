package com.taotao.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.GetItemService;

@Service
public class GetItemServiceImpl implements GetItemService {

	@Autowired 
	private TbItemMapper mapper;

	@Override
	public EasyUIDataGridResult getAll() {
		
		TbItemExample example = new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);
		EasyUIDataGridResult result = new EasyUIDataGridResult();		
		result.setTotal(list.size());		
		result.setRows(list);		
		return result;
	} 
	
	

}
