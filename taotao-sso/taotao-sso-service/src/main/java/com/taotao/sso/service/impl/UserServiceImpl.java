package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.jedis.JedisClient;
import com.taotao.sso.service.UserService;
import com.taotao.utils.JsonUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper usermapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;

	
	@Override
	public TaotaoResult checkData(String param, Integer type) {
		//1.注入mapper
		//2.根据参数动态的生成查询的条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if(type==1){//username
			if(StringUtils.isEmpty(param)){
				return TaotaoResult.ok(false);
			}
			criteria.andUsernameEqualTo(param);
		}else if(type==2){
			//phone
			criteria.andPhoneEqualTo(param);
		}else if(type==3){
			//email
			criteria.andEmailEqualTo(param);
		}else{
			//是非法的参数    
			//return 非法的
			return TaotaoResult.build(400, "非法的参数");
		}
		//3.调用mapper的查询方法 获取数据   
		List<TbUser> list = usermapper.selectByExample(example);
		//4.如果查询到了数据   --数据不可以用   false
		if(list!=null && list.size()>0){
			return TaotaoResult.ok(false);
		}
		//5.如果没查到数据  ---数据是可以用  true
		return TaotaoResult.ok(true);
	}


	@Override
	public TaotaoResult register(TbUser user) {
		//1.注入mapper
		//2.校验数据
			//2.1 校验用户名和密码不能为空
		if(StringUtils.isEmpty(user.getUsername())){
			return TaotaoResult.build(400, "注册失败. 请校验数据后请再提交数据");
		}
		if(StringUtils.isEmpty(user.getPassword())){
			return TaotaoResult.build(400, "注册失败. 请校验数据后请再提交数据");
		}
			//2.2 校验用户名是否被注册了
		TaotaoResult result = checkData(user.getUsername(),1);
		if(!(boolean)result.getData()){
			//数据不可用
			return TaotaoResult.build(400, "注册失败. 用户名重复");
		}
			//2.3 校验电话号码是否被注册了
		if(StringUtils.isNotBlank(user.getPhone())){
			TaotaoResult result2 = checkData(user.getPhone(),2);
			if(!(boolean)result2.getData()){
				//数据不可用
				return TaotaoResult.build(400, "注册失败. 电话重复");
			}
		}
			//2.4 校验email是否被注册了
		if(StringUtils.isNotBlank(user.getEmail())){
			TaotaoResult result2 = checkData(user.getEmail(),3);
			if(!(boolean)result2.getData()){
				//数据不可用
				return TaotaoResult.build(400, "注册失败. 邮箱重复");
			}
		}
		
		//3.如果校验成功   补全其他的属性
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//4.对密码进行MD5加密
		String md5password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5password);
		//5.插入数据
		usermapper.insertSelective(user);
		//6.返回taotaoresult
		return TaotaoResult.ok();
	}
	
	@Override
	public TaotaoResult login(String username, String password) {
		//判断用户名和密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = usermapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			//返回登录失败
			return TaotaoResult.build(400, "用户名或密码不正确");
		}
		TbUser user = list.get(0);
		//密码要进行md5加密然后再校验
		if (!DigestUtils.md5DigestAsHex(password.getBytes())
				.equals(user.getPassword())) {
			//返回登录失败
			return TaotaoResult.build(400, "用户名或密码不正确");
		}
		//生成token，使用uuid
		String token = UUID.randomUUID().toString();
		//清空密码
		user.setPassword(null);
		//把用户信息保存到redis，key就是token，value就是用户信息
		jedisClient.set(USER_SESSION + ":" + token, JsonUtils.objectToJson(user));
		//设置key的过期时间
		jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
		//返回登录成功，其中要把token返回。
		return TaotaoResult.ok(token);
	}
	
	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(USER_SESSION + ":" + token);
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "用户登录已经过期");
		}
		//重置Session的过期时间
		jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
		//把json转换成User对象
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
//		return TaotaoResult.ok(json);
	}


	@Override
	public TaotaoResult logout(String token) {
		jedisClient.hdel(token);
		return TaotaoResult.ok();
	}

	
}
