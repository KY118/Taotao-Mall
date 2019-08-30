package com.taotao.test.jedis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	@Test
	public void testJedis() {
		Jedis jedis = new Jedis("192.168.25.128",6379);
		jedis.set("key1","value1");
		System.out.println(jedis.get("key1"));
		jedis.close();		
	}
	
	
	@Test
	public void testJedisPool(){
		//1.创建jedispool 对象需要指定端口和地址
		JedisPool pool = new JedisPool("192.168.25.128", 6379);
		//2.获取jedis的对象
		Jedis jedis = pool.getResource();
		//3.直接操作redis
		jedis.set("keypool", "keypool");
		System.out.println(jedis.get("keypool"));
		//4.关闭redis  (释放资源到连接池)
		jedis.close();
		//5.关闭连接池（应用系统关闭的时候才关闭）
		pool.close();
	}
	
	
//	//测试集群版
//		@Test
//		public void testJedisCluster(){
//			Set<HostAndPort> nodes = new HashSet<>();
//			nodes.add(new HostAndPort("192.168.25.128", 7001));
//			nodes.add(new HostAndPort("192.168.25.128", 7002));
//			nodes.add(new HostAndPort("192.168.25.128", 7003));
//			nodes.add(new HostAndPort("192.168.25.128", 7004));
//			nodes.add(new HostAndPort("192.168.25.128", 7005));
//			nodes.add(new HostAndPort("192.168.25.128", 7006));
//			//1.创建jediscluster对象
//			JedisCluster cluster = new JedisCluster(nodes );
//			//2.直接根据jediscluster对象操作redis集群
//			cluster.set("keycluster", "cluster的value");
//			System.out.println(cluster.get("keycluster"));
//			//3.关闭jediscluster对象(是在应用系统关闭的时候关闭) 封装了连接池
//			cluster.close();
//		}
}
