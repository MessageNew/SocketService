package db;

import beans.ResultSql;
import beans.SqlData;

import java.sql.*;

public class Jdbc {
	/**
	 * JDBC简单封装
	 */
	private static String dataname="bulletinfo";
	private static String encoding="UTF-8";
	private static String username="root";
	private static String password="19980915mysql";
	private static String url = "jdbc:mysql://127.0.0.1:3306/"+dataname+"?characterEncoding="+encoding+"";
	public static Connection Jdbc(){
		try {
			System.out.println("驱动名称:MYSQL驱动2");
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("驱动名称:MYSQL驱动");
			Connection conn = (Connection) DriverManager.getConnection(url,username,password);
			System.out.println("CONN:"+conn);
			return conn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			return null;
		}
	}
	/**
	 * 
	 * @param sql
	 */
	public static void exesql(String sql){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = (Connection) DriverManager.getConnection(url,username,password);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.executeUpdate(sql);
			pst.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * sql数据查询
	 * @param sqlData 传入的数据
	 * @param con 数据库连接
	 * @param originalsql 原始sql语句(带?)
	 * @return
	 */
	public static ResultSql SearchData(SqlData sqlData,Connection con,String originalsql){
		try {
			PreparedStatement pstmt = con.prepareStatement(originalsql);
			// count ? 的个数
			int count = SearchCount(originalsql, "?");
			System.out.println(count);
			for(int i = 1;i <= count;i ++){
				pstmt.setObject(i, SqlGetString(i, sqlData));
			}
			ResultSet rs = (ResultSet) pstmt.executeQuery();
			ResultSql resultSql = new ResultSql();
			resultSql.setRs(rs);
			resultSql.setPstmt(pstmt);
			return resultSql;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 添加sql数据
	 * @param sqlData sql数据
	 * @param con sql连接
	 * @param originalsql 原始sql语句
	 * @return
	 */
	public static boolean AddData(SqlData sqlData,Connection con,String originalsql){
		try{
			PreparedStatement pstmt = con.prepareStatement(originalsql);
			// count ? 的个数
			int count = SearchCount(originalsql, "?");
			System.out.println(count);
			for(int i = 1;i <= count;i ++){
				pstmt.setObject(i, SqlGetString(i, sqlData));
			}
			if(!pstmt.execute()){
				pstmt.close();
				con.close();
			}
		}catch(Exception e){
			System.out.println(e);
			return false;
		}
		
		
		return true;
	}
	
	/**
	 * 添加表单
	 * @param sqlData sql数据
	 * @param con sql连接
	 * @param originalsql sql原始语句
	 * @return
	 */
	public static boolean AddForm(SqlData sqlData,Connection con,String originalsql){

		try {
			PreparedStatement pstmt = con.prepareStatement(originalsql);
			// count ? 的个数
			int count = SearchCount(originalsql, "?");
			System.out.println(count);
			for(int i = 1;i <= count;i ++){
				pstmt.setObject(i, SqlGetString(i, sqlData));
			}
			pstmt.executeUpdate();
			pstmt.close();
			con.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 删除数据
	 * @param sqlData sql数据
	 * @param con sql连接
	 * @param originalsql sql原始语句
	 * @return
	 */
	public static boolean DeleteData(SqlData sqlData,Connection con,String originalsql){
		try {
			PreparedStatement pstmt = con.prepareStatement(originalsql);
			// count ? 的个数
			int count = SearchCount(originalsql, "?");
			System.out.println(count);
			for(int i = 1;i <= count;i ++){
				pstmt.setObject(i, SqlGetString(i, sqlData));
			}
			pstmt.executeUpdate();
			pstmt.close();
			con.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 更新数据
	 * @param sqlData sql数据
	 * @param con sql连接
	 * @param originalsql sql原始语句
	 * @return
	 */
	public static boolean UpdateData(SqlData sqlData,Connection con,String originalsql){
		try {
			PreparedStatement pstmt = con.prepareStatement(originalsql);
			// count ? 的个数
			int count = SearchCount(originalsql, "?");
			for(int i = 1;i <= count;i ++){
				pstmt.setObject(i, SqlGetString(i, sqlData));
			}
			pstmt.executeUpdate();
			pstmt.close();
			con.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 查询指定字符串在语句中的个数
	 * @param original 原始字符串
	 * @param key 查询的key
	 * @return
	 */
	public static int SearchCount(String original,String key){   
        int count = 0;  // 初始值  
        //一共有str.length()的循环次数    
        for(int i=0; i<original.length() ; ){    
            int c = -1;    
            c = original.indexOf(key);    
            //如果有S这样的子串。则C的值不是-1.    
            if(c != -1){  // 如果c=-1则说明不在在  
        //这里的c+1 而不是 c+ s.length();这是因为。如果str的字符串是“aaaa”， s = “aa”，则结果是2个。但是实际上是3个子字符串    
            //将剩下的字符冲洗取出放到str中    
            	original = original.substring(c + 1);  // 从存在的那个下标后一位开始  
                 count ++;      
            }else {    
                break;    
            }    
        } 
        return count;
	}
	
	/**
	 * sql参数化传值
	 * @param i 序列号
	 * @param sqlData sql数据
	 * @return
	 */
	public static Object SqlGetString(int i , SqlData sqlData){
		switch (i) {
			case 1:
				return sqlData.getColumnValue1();
			case 2:
				return sqlData.getColumnValue2();
			case 3:
				return sqlData.getColumnValue3();
			case 4:
				return sqlData.getColumnValue4();
			case 5:
				return sqlData.getColumnValue5();
			case 6:
				return sqlData.getColumnValue6();
			case 7:
				return sqlData.getColumnValue7();
			default:
				return null;
		}
	}
	
}
