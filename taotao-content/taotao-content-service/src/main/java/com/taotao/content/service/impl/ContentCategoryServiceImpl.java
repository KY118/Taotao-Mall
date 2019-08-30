 package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理service
 * <p>Title: ContentCategoryServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到结果列表
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		//创建一个pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全对象的属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排序，默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			//如果父节点为叶子节点应该改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
			
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult upDateContentCategory(Long id, String name) {
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		tbContentCategory.setName(name);
		int PrimaryKey = contentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey((long)PrimaryKey);
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult delectContentCategory(Long id) {
		//1.获取删除节点的is_parent
				TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
				//2.如果is_parent=false，说明要删除的节点不是父节点，可以删除。否则不允许删除
				//因为大多数删除节点是父节点的请求，都被js过滤掉了，所以将is_parent=false的情况放在最先执行
				if(!tbContentCategory.getIsParent()) {
					//3.封装更新的字段status。1(正常),2(删除)
					tbContentCategory.setStatus(2);
					//4.更新内容分类表
					//updateByPrimaryKey：按主键更新
					//updateByPrimaryKeySelective：按主键更新值不为null的字段
					contentCategoryMapper.updateByPrimaryKey(tbContentCategory);
					//5.判断该节点的父节点是否还有子节点，如果没有需要把父节点的isparent改为false
					//查询根据parentId查询所有子节点节点
					Long parentId = tbContentCategory.getParentId();
					List<EasyUITreeNode> list = getContentCategoryList(parentId);
					if(list.size() == 0) {
						TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
						parentNode.setIsParent(false);
					}
					return TaotaoResult.ok();
				}else {
					String msg = "请先删  "+tbContentCategory.getName()+" 分类下的所有子分类，再删除 "+tbContentCategory.getName()+"分类";
					TaotaoResult result = TaotaoResult.build(500,msg,null);
					return result;
				}
	}
	
	

}
