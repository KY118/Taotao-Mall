package com.taotao.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper mapper;
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name = "topicDestination")
	private Destination destination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;
	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);
		//取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal((int)pageInfo.getTotal());
		//返回结果
		return result;
	}

	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成商品id
				final long itemId = IDUtils.genItemId();
				//补全item的属性
				item.setId(itemId);
				//商品状态，1-正常，2-下架，3-删除
				item.setStatus((byte) 1);
				item.setCreated(new Date());
				item.setUpdated(new Date());
				//向商品表插入数据
				mapper.insert(item);
				//创建一个商品描述表对应的pojo
				TbItemDesc itemDesc = new TbItemDesc();
				//补全pojo的属性
				itemDesc.setItemId(itemId);
				itemDesc.setItemDesc(desc);
				itemDesc.setUpdated(new Date());
				itemDesc.setCreated(new Date());
				//向商品描述表插入数据
				tbItemDescMapper.insert(itemDesc);
				//向Activemq发送商品添加消息
				jmsTemplate.send(destination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送商品id
						TextMessage textMessage = session.createTextMessage(itemId + "");
						return textMessage;
					}
				});
				
				//返回结果
				return TaotaoResult.ok();
	}
	
	@Override
	public TbItem getItemById(Long itemId) {
		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = jedisClient.get(ITEM_INFO_KEY + ":" + itemId + ":BASE");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItem.class);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 2如果没有数据

		// 注入mapper
		// 调用方法
		TbItem tbItem = mapper.selectByPrimaryKey(itemId);
		// 返回tbitem

		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			jedisClient.set(ITEM_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
			// 设置缓存的有效期
			jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
		
	}

	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = jedisClient.get(ITEM_INFO_KEY + ":" + itemId + ":DESC");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				System.out.println("有缓存");
				jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItemDesc.class);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//如果没有查到数据 从数据库中查询
		TbItemDesc itemdesc = tbItemDescMapper.selectByPrimaryKey(itemId);
		//添加缓存
		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			jedisClient.set(ITEM_INFO_KEY + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemdesc));
			// 设置缓存的有效期
			jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemdesc;
	}
	
	

}
