package com.lwy.myselect.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lwy.myselect.core.Session;
import com.lwy.myselect.core.SessionFactory;
import com.lwy.myselect.entity.Entity;
import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.mapper.parser.XMLParser;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration configuration = XMLParser.parse();
//		insert();
//		delete();
//		update();
//		select();
		concurrentSelect();
//		transactionTest();
	}

	private static void insert() {
		Entity e = new Entity();
		e.setDate(new Date());
		e.setDou(95.9);
		e.setFl(22.6f);
//		e.setInte(8);
		e.setLon(908);
		e.setStr("bbb");
		SessionFactory sh = new SessionFactory();
		Session session = sh.getSession(Entity.class);
		session.insert("insertEntity", e);
	}
	
	private static void delete(){
		SessionFactory sh = new SessionFactory();
		Session session = sh.getSession(Entity.class);
		Entity e = new Entity();
		e.setInte(5);
		session.delete("deleteEntity", e);
		sh.closeSession(session);
	}
	
	private static void update(){
		SessionFactory sh = new SessionFactory();
		Session session = sh.getSession(Entity.class);
		Entity e = new Entity();
		e.setInte(5);
		e.setDou(6.77);
		session.update("updateEntity", e);
	}
	
	@SuppressWarnings("unchecked")
	private static void select(){
		SessionFactory sh = new SessionFactory();
		Session session1 = sh.getCurrentSession(Entity.class);
		System.out.println(session1.hashCode()+"      "+session1.getConnection().hashCode());
		Entity e = new Entity();
		e.setInte(5);
		List<Object> list = (List<Object>) session1.select("selectEntityStr", e);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
		sh.closeSession(session1);
		System.out.println("==================");
		Session session2 = sh.getCurrentSession(Entity.class);
		System.out.println(session2.hashCode()+"      "+session2.getConnection().hashCode());
		List<Object> list2 = (List<Object>) session2.select("selectEntity", e);
		for(int i=0;i<list2.size();i++){
			System.out.println(list2.get(i));
		}
		sh.closeSession(session2);
		System.out.println("==================");
		Session session3 = sh.getCurrentSession(Entity.class);
		System.out.println(session3.hashCode()+"      "+session3.getConnection().hashCode());
		long count = (long) session3.select("selectCountEntity", e);
		System.out.println(count);
		sh.closeSession(session3);
		System.out.println("==================");
		Session session4 = sh.getCurrentSession(Entity.class);
		System.out.println(session4.hashCode()+"      "+session4.getConnection().hashCode());
		long count2 = (long) session4.select("selectCountSpecialEntity", e);
		System.out.println(count2);
		sh.closeSession(session4);
	}
	
	private static void concurrentSelect(){
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i=0;i<4;i++){
			exec.execute(new MultiThread(i+1));
		}
		exec.shutdown();
	}
	
	private static void transactionTest(){
		Entity e1 = new Entity();
		e1.setDate(new Date());
		e1.setDou(20.2);
		e1.setFl(20.02f);
		e1.setLon(200);
		e1.setStr("twen");
		Entity e2 = new Entity();
		e2.setDate(new Date());
		e2.setDou(21.1);
		e2.setFl(21.12f);
		e2.setLon(211);
		e2.setStr("tweo");
		Entity e3 = new Entity();
		e3.setDate(new Date());
		e3.setDou(22.2);
		e3.setFl(22.22f);
		e3.setLon(222);
		e3.setStr("twet");
		List<Entity> list = new ArrayList<>();
		list.add(e1);
		list.add(e2);
		list.add(e3);
		Session session = new SessionFactory().getCurrentSession(Entity.class);
		session.openTransaction();
		session.insert("insertEntity", list);
		session.commit();
		session.close();
	}

}
