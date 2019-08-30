package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.utils.JsonUtils;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;
	
	@Override
	public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
		//1.设置分页信息
		PageHelper.startPage(page, rows);
		//2.封装查询条件  根据categoryId
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//3.执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		//4.根据list，封装PageInfo
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//5.根据PageInfo，封装EasyUIDataGridResult
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal((int)pageInfo.getTotal());
		//6.返回结果
		return result;
		
	
	}
	
	@Override
	public TaotaoResult addContent(TbContent content) {
		//补全pojo的属性
		content.setCreated( new Date());
		content.setUpdated(new Date());
		//插入到内容表
		contentMapper.insert(content);
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult editContent(TbContent tbContent) {
		tbContent.setUpdated(new Date());
		contentMapper.updateByPrimaryKey(tbContent);
		return TaotaoResult.ok();
	}
	
	@Override
	public TaotaoResult deleteContent(List<Long> ids) {
		for (Long id : ids) {
			contentMapper.deleteByPrimaryKey(id);
		}
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getContentListByCatId(Long categoryId) {
		try {
			String jsonString = jedisClient.hget(INDEX_CONTENT, categoryId+"");
			if(StringUtils.isNotBlank(jsonString)) {
				List<TbContent> list = JsonUtils.jsonToList(jsonString, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//1.注入mapper
		//2.创建example
		TbContentExample example = new TbContentExample();
		//3.设置查询的条件
		example.createCriteria().andCategoryIdEqualTo(categoryId);//select × from tbcontent where category_id=1
		//4.执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		
		try {
			jedisClient.hset(INDEX_CONTENT, categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;
	}
	
	


}
