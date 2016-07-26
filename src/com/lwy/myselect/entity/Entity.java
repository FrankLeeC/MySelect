package com.lwy.myselect.entity;

import java.io.Serializable;
import java.util.Date;

import com.lwy.myselect.annotation.Cache;
import com.lwy.myselect.annotation.Fields;
import com.lwy.myselect.annotation.KeyProperty;
import com.lwy.myselect.annotation.Table;

@Table(alias="entity", value = "entity")
@Cache("FIFO")
public class Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@KeyProperty(type = Integer.class, value = "entity_inte", strategy = "auto_increment")
	private Integer inte;
	@Fields(nullable = false, type = Long.class, value = "entity_lon")
	private long lon;
	@Fields(nullable = false, type = String.class, value = "entity_str")
	private String str;
	@Fields(nullable = false, type = Float.class, value = "entity_fl")
	private float fl;
	@Fields(nullable = false, type = Double.class, value = "entity_dou")
	private double dou;
	@Fields(nullable = false, type = Date.class, value = "entity_date")
	private Date date;
	
	public Entity(){}
	
	public Entity(Integer inte, long lon, String str,
			float fl, double dou, Date date) {
		super();
		this.inte = inte;
		this.lon = lon;
		this.str = str;
		this.fl = fl;
		this.date = date;
	}
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public long getLon() {
		return lon;
	}
	public void setLon(long lon) {
		this.lon = lon;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public float getFl() {
		return fl;
	}
	public void setFl(float fl) {
		this.fl = fl;
	}
	public double getDou() {
		return dou;
	}
	public void setDou(double dou) {
		this.dou = dou;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "inte:"+inte+"\tlon:"+lon+"\tstr:"+str+"\tfl:"+fl+"\tdou:"+dou+"\tdate:"+date;
	}
}
