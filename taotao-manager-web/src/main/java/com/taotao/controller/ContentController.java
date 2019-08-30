package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

@Controller
public class ContentController {
	@Autowired
	private ContentService contentService;
	
	@RequestMapping(value = "/content/query/list",method = RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
		return contentService.getContentList(categoryId, page, rows);
	}
	
		
	@RequestMapping(value = "/content/save",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult addContent(TbContent content) {
		return contentService.addContent(content);
	}
	
	@RequestMapping(value = "/rest/content/edit",method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult editContent(TbContent tbContent) {
		return contentService.editContent(tbContent);
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(@RequestParam(value="ids") List<Long> ids) {
		TaotaoResult result = contentService.deleteContent(ids);
		return result;
	}


}
