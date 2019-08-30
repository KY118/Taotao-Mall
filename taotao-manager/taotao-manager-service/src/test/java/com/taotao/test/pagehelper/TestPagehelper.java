package com.taotao.test.pagehelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;

public class TestPagehelper {
	@Test 
	public void testHelper() {
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicatonContext-dao.xml");
//		TbItemMapper tbItemMapper = applicationContext.getBean(TbItemMapper.class);
		//初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//获得Mapper的代理对象
		TbItemMapper tbItemMapper = applicationContext.getBean(TbItemMapper.class);
//		设置分页信息
		PageHelper.startPage(1, 30);
		TbItemExample example = new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		List<TbItem> list1 = tbItemMapper.selectByExample(example);
		
		
		PageInfo<TbItem> info = new PageInfo<TbItem>(list);
		
		System.out.println("第一种："+info.getPageSize());
		System.out.println("第二种："+list1.size());
			
		for (TbItem tbItem : list) {
			System.out.println(tbItem.getId()+"》》名字》》"+tbItem.getTitle());
		} 
		
	}
}
