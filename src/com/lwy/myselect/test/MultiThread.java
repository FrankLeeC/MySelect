package com.lwy.myselect.test;

import java.util.List;

import com.lwy.myselect.session.Session;
import com.lwy.myselect.session.SimpleSession;
import com.lwy.myselect.session.SessionFactory;
import com.lwy.myselect.entity.Entity;

public class MultiThread implements Runnable {
	private int id;
	private SessionFactory sf;

	public MultiThread(int id,SessionFactory sf) {
		this.id = id;
		this.sf = sf;
	}

	@Override
	public void run() {
		if(id == 1){
			System.out.println(id+"==========================");
			Session session1 = sf.getCurrentSession(Entity.class);
			System.out.println(id+"   %   "+session1.hashCode()+"   &   "+session1.getConnection().hashCode());
			Entity e = new Entity();
			e.setInte(5);
			List<Object> list = (List<Object>) session1.select("selectEntityStr", e);
			for(int i=0;i<list.size();i++){
				System.out.println(id+"==============="+list.get(i));
			}
			sf.closeSession(session1);
			System.out.println(id+"==========================");
		}
		else if(id ==2 ){
			System.out.println(id+"==========================");
			Session session2 = sf.getCurrentSession(Entity.class);
			System.out.println(id+"   %   "+session2.hashCode()+"   &   "+session2.getConnection().hashCode());
			Entity e = new Entity();
			e.setInte(5);
			List<Object> list2 = (List<Object>) session2.select("selectEntity", e);
			for(int i=0;i<list2.size();i++){
				System.out.println(id+"==============="+list2.get(i));
			}
			sf.closeSession(session2);
			System.out.println(id+"==========================");
		}
		else if(id == 3){
			System.out.println(id+"==========================");
			Session session3 = sf.getCurrentSession(Entity.class);
			System.out.println(id+"   %   "+session3.hashCode()+"   &   "+session3.getConnection().hashCode());
			Entity e = new Entity();
			e.setInte(5);
			long count = (long) session3.select("selectCountEntity", e);
			System.out.println(id+"===============count="+count);
			sf.closeSession(session3);
			System.out.println(id+"==========================");
		}
		else{
			System.out.println(id+"==========================");
			Session session4 = sf.getCurrentSession(Entity.class);
			System.out.println(id+"   %   "+session4.hashCode()+"   &   "+session4.getConnection().hashCode());
			Entity e = new Entity();
			e.setInte(5);
			long count2 = (long) session4.select("selectCountSpecialEntity", e);
			System.out.println(id+"===============count2="+count2);
			sf.closeSession(session4);
			System.out.println(id+"==========================");
			System.out.println(id+"==========================");
//			SessionFactory sh2 = new SessionFactory(sf.getConfiguration());
//			Session session5 = sh2.getCurrentSession(Entity.class);
//			System.out.println(id+"   %   "+session5.hashCode()+"   &   "+session5.getConnection().hashCode());
//			long count3 = (long) session5.select("selectCountSpecialEntity", e);
//			System.out.println(id+"===============count3="+count3);
//			sh2.closeSession(session5);
//			System.out.println(id+"==========================");
		}
	}

}
