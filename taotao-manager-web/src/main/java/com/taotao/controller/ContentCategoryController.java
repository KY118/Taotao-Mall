package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;

@Controller
public class ContentCategoryController {
	@Autowired
	private ContentCategoryService service;
	/**
	 * url : '/content/category/list',
	   animate: true,
	   method : "GET"
	       参数 ： id
	 */
	 
	@RequestMapping(value = "/content/category/list",method =RequestMethod.GET )
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value = "id",defaultValue = "0") Long parentId){
		return service.getContentCategoryList(parentId);
	}
	
	
	
	@RequestMapping(value = "/content/category/create",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult addContentCategory(Long parentId,String name) {
		return service.addContentCategory(parentId, name);
	}
	
	
	@RequestMapping(value = "/content/category/update",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult upDataContentCategory(Long id,String name) {
		return service.upDateContentCategory(id, name);
	}
	
	@RequestMapping(value = "/content/category/delete",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult delectContentCategory(Long id) {
		return service.delectContentCategory(id);
	}
	
	
	
}
