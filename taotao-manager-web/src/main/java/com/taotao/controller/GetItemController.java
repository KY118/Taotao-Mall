package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.service.GetItemService;

@Controller
public class GetItemController {
	@Autowired
	private GetItemService getItemService;
	
	@RequestMapping("/item")
	@ResponseBody
	public EasyUIDataGridResult getAll() {
		
		return getItemService.getAll();
		
	}

}
