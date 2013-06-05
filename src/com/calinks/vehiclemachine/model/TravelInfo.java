package com.calinks.vehiclemachine.model;

import java.io.Serializable;
/**
 * 提取obd行程数据，并存储
 * @author Administrator
 *
 */
public class TravelInfo implements Serializable {
	public long this_start_time;
	public long this_end_time;
	public float this_miles;
	public long this_travel_time;
	public float this_oil_consum;
	public float this_oil_consum_havg;
	public float last_oil_consum_havg;
}

//"this_end_time long," +
//"this_miles float," +
//"this_oil_consum float," +
//"this_oil_consum_havg float," +
//"this_start_time long," +
//"this_travel_time long," +
//"last_oil_consum_havg float" +