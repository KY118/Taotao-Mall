package com.taotao.test.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taotao.content.jedis.JedisClient;

public class TestJedisClient {
	@Test
	public void testdanji(){
		//1.初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-jedis.xml");
		//2.获取实现类实例
		JedisClient bean = context.getBean(JedisClient.class);
		//3.调用方法操作
		bean.set("jedisclientkey", "jedisclientkey");
		System.out.println(bean.get("jedisclientkey"));
	}
}
