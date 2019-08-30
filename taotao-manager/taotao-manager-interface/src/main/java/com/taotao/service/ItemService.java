package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

/**商品相关处理的service
 * @author 旷远
 *
 */
public interface ItemService {
	
	/**
	 * 根据当前的页码和每页的行数进行分页查询
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult getItemList(Integer page,Integer rows); 
	
	public TaotaoResult addItem(TbItem item,String desc);
	
    public TbItem  getItemById(Long itemId);
	
	//根据商品的id查询商品的描述
	public TbItemDesc getItemDescById(Long itemId);
}
