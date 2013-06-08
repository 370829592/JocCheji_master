package com.calinks.vehiclemachine.model.db;

public class DBConfig {
	public static final String TABLE_NAME_1 = "this_travel_info_table";//行程详情表
	public static final String TABLE_NAME_2 = "all_total_travel_table";//行程统计表
	public static final String TABLE_NAME_3 = "error_code_table";//故障码表
	public static final String TABLE_NAME_4 = "consum_record_table";//消费记录表
	
	
	public static final String CREATE_TABLE_1 = "create table "+TABLE_NAME_1+"(" +
			"_id integer not null primary key autoincrement," +
			"this_end_time text," +
			"this_miles float," +
			"this_oil_consum float," +
			"this_oil_consum_havg float," +
			"this_start_time text," +
			"this_travel_time long," +
			"last_oil_consum_havg float" +
			");";
	
	public static final String CREATE_TABLE_2 = "create table "+TABLE_NAME_2+"(" +
			"_id integer not null primary key autoincrement," +
			"all_total_miles float," +
			"all_travel_time long," +
			"all_oil_consum float" +
			");";
	
	public static final String CREATE_TABLE_3 = "create table "+TABLE_NAME_3+"(" +
			"_id integer not null primary key autoincrement," +
			"error_code text" +
			");";
	
	public static  final String CREATE_TABLE_4 = "create table "+TABLE_NAME_4+"(" +
			"_id integer not null primary key autoincrement," +
			"date text," + 
			"money text," +
			"miles text," +
			"content text," +
			"charge_type text" +
			");";
	
	public static final String THE_FIRST_INSERT_INTO_TABLE_3 = "insert into "+TABLE_NAME_3+" values(" +
			"1,'0')";	
			
	
	
	public static final String THE_FIRST_INSERT_INTO_TABLE_2 = "insert into "+TABLE_NAME_2+" values(" +
			"1,0,0,0)";
	
	public static final String SELECT_ALL_TOTAL_TABLE = "select * from "+TABLE_NAME_2; 
	
	public static String getInsertIntoTable1(String this_end_time,float this_miles,float this_oil_consum,
			float this_oil_consum_havg,String this_start_time,long this_travel_time,float last_oil_consum_havg){
		String sql = "insert into "+TABLE_NAME_1+" values(" +
				"null,'"+this_end_time+"',"+this_miles+","+this_oil_consum+","+this_oil_consum_havg+",'"+this_start_time+"',"+this_travel_time+","+last_oil_consum_havg+");";
		return sql;
	}
	
	public static String updateTable2SQLString(String column,String value){
		String sql = "update "+TABLE_NAME_2+" set "+column+"="+value;
		return sql;
	}
	
	public static String selectDayTravelInfo(String key){
		return "select * from "+TABLE_NAME_1+" where this_end_time like '%"+key+"%'";
	}
	
	public static final String SELECT_THIS_TRAVEL = "select * from "+TABLE_NAME_1+" where _id = (select max(_id) from "+TABLE_NAME_1+");";
	
}