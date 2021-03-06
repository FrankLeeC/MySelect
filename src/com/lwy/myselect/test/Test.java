package com.lwy.myselect.test;

import com.lwy.myselect.entity.Entity;
import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.mapper.parser.XMLParser;
import com.lwy.myselect.session.Session;
import com.lwy.myselect.session.SessionFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration configuration = null;
		try {
			configuration = new XMLParser().parse();
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
		SessionFactory sf = new SessionFactory(configuration);
//		insert(sf);
//		delete(sf);
//		update(sf);
//		select(sf);
//		concurrentSelect(sf);
//		transactionTest(sf);
		selectSame(sf);
//		batchTest(sf);
	}

	private static void batchTest(SessionFactory sf){
		Entity e = new Entity();
		e.setInte(21);
		e.setDate(new Date());
		Entity e1 = new Entity();
		e1.setInte(22);
		e1.setDate(new Date());
		Entity e2 = new Entity();
		e2.setInte(23);
		e2.setDate(new Date());
		Session session = sf.getCurrentSession(Entity.class);
		session.addBatch("insertEntity");
		session.addBatch(e);
		session.addBatch(e1);
		session.addBatch(e2);
		try {
			session.executeBatch(2);
		} catch (SQLException e3) {
			try {
				session.rollback();
				sf.closeSession(session);
			} catch (SQLException e4) {
				e4.printStackTrace();
			}
		} finally {
			sf.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	private static void selectSame(SessionFactory sf){
		Entity e = new Entity();
		e.setInte(5);
		Session session2 = sf.getCurrentSession(Entity.class);
//		System.out.println(session2.hashCode()+"      "+((BaseSession)session2).getConnection().hashCode());
		List<Object> list2 = (List<Object>) session2.select("selectEntity", e);
		for(int i=0;i<list2.size();i++){
			System.out.println(list2.get(i));
		}
		sf.closeSession(session2);
		System.out.println("=============");
		Session session3 = sf.getCurrentSession(Entity.class);
//		System.out.println(session3.hashCode()+"      "+session3.getConnection().hashCode());
		List<Object> list3 = (List<Object>) session3.select("selectEntity", e);
		for(int i=0;i<list3.size();i++){
			System.out.println(list3.get(i));
		}
		sf.closeSession(session3);
	}

	private static void insert(SessionFactory sf) {
		Entity e = new Entity();
		e.setDate(new Date());
		e.setDou(893.215);
		e.setFl(89.7f);
		e.setLon(598794234);
		e.setStr("grj");
		Session session = sf.getSession(Entity.class);
		session.insert("insertEntity", e);
	}
	
	private static void delete(SessionFactory sf){
		Session session = sf.getSession(Entity.class);
		Entity e = new Entity();
		e.setInte(20);
		session.delete("deleteEntity", e);
		sf.closeSession(session);
	}
	
	private static void update(SessionFactory sf){
		Session session = sf.getSession(Entity.class);
		Entity e = new Entity();
		e.setInte(19);
		e.setDou(6.77);
		session.update("updateEntity", e);
	}
	
	@SuppressWarnings("unchecked")
	private static void select(SessionFactory sf){
		Session session1 = sf.getCurrentSession(Entity.class);
//		System.out.println(session1.hashCode()+"      "+session1.getConnection().hashCode());
		Entity e = new Entity();
		e.setInte(5);
		List<Object> list = (List<Object>) session1.select("selectEntityStr", e);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
		sf.closeSession(session1);
		System.out.println("==================");
		Session session2 = sf.getCurrentSession(Entity.class);
//		System.out.println(session2.hashCode()+"      "+session2.getConnection().hashCode());
		List<Object> list2 = (List<Object>) session2.select("selectEntity", e);
		for(int i=0;i<list2.size();i++){
			System.out.println(list2.get(i));
		}
		sf.closeSession(session2);
		System.out.println("==================");
		Session session3 = sf.getCurrentSession(Entity.class);
//		System.out.println(session3.hashCode()+"      "+session3.getConnection().hashCode());
		long count = (long) session3.select("selectCountEntity", e);
		System.out.println(count);
		sf.closeSession(session3);
		System.out.println("==================");
		Session session4 = sf.getCurrentSession(Entity.class);
//		System.out.println(session4.hashCode()+"      "+session4.getConnection().hashCode());
		long count2 = (long) session4.select("selectCountSpecialEntity", e);
		System.out.println(count2);
		sf.closeSession(session4);
	}
	
	private static void concurrentSelect(SessionFactory sf){
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i=0;i<4;i++){
			exec.execute(new MultiThread(i+1,sf));
		}
		exec.shutdown();
	}
	
	private static void transactionTest(SessionFactory sf){
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
		Session session = sf.getCurrentSession(Entity.class);
		session.openTransaction();
		session.insert("insertEntity", list);
		session.commit();
		session.close();
	}

}
