package com.spring.dao.jdbc.daosupport;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.spring.beans.common.exception.DataAccessException;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.BeanFactory;

public class JdbcTemplate {
	private DataSource dataSource;
	private boolean deBug = false;
	private boolean detail = false;
	private BeanFactory beanFactory;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public JdbcTemplate(){}
	
	public JdbcTemplate(DataSource dataSource, BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.dataSource = dataSource;
	}


	/****
	 ****切换为Debug模式****
	 ****/
	public void setDeBug(boolean deBug){
		this.deBug = deBug;
	}
	
	/****
	 * 切换为详细日志模式(日志全开)
	 * @param detail
	 */
	public void setDetail(boolean detail){
		this.detail = detail;
	}
	
	
	
	
	/****
	 ****封装查询数据的结果集****
	 ****/
	public List queryForList(String sql){
		String name = "";
		Connection conn = null;
		ResultSet rs = null;
		Statement stat = null;
		ResultSetMetaData rsm = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			conn = this.dataSource.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			rsm = rs.getMetaData();
			while (rs.next()){
				Map<String,Object> map = new HashMap<String,Object>();
				for (int i = 1; i <= rsm.getColumnCount(); i++){
					name = rsm.getColumnName(i).toLowerCase();
					if (contains(rsm.getColumnTypeName(i),
						new String[]{"blob","longblob","mediumblob","tinyblob"})){
						byte[] bt = new byte[1024];
						Blob blob = rs.getBlob(i);
						if(blob != null && blob.length() != 0){
							InputStream is = blob.getBinaryStream();
							bt = new byte[(int)blob.length()];
							is.read(bt);
							is.close();
						}
						map.put(name,bt);
						if (detail) System.out.println("The BLOB");
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"clob","nclob","text","longtext","mediumtext","tinytext"})){
						String str = "";
						Clob clob = rs.getClob(i);
						Converter ct = new Converter();
						if(clob != null){
							str = ct.getStr(clob);
						}
						map.put(name,str);
						if(detail) System.out.println("The CLOB");
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"date","datetime","timestamp"})){
						String date = "";
						Timestamp ts = rs.getTimestamp(i);
						if (ts!=null){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							date = sdf.format(ts);
						}
						map.put(name,date);
						if(detail) System.out.println("The TIMESTAMP");
					} else {
						String str = rs.getString(i)==null?"":rs.getString(i);
						map.put(name,str);
						if(detail) System.out.println("The Other");
					}
				}
				list.add(map);
			}

		} catch (Exception e) {
			throw new DataAccessException("exception for the method queryForList");
		} finally {
			if(rs != null){try{rs.close();}catch(SQLException e){}}
			if(stat != null){try{stat.close();}catch(SQLException e){}}
			if(conn != null){try{conn.close();}catch(SQLException e){}}
		}
		return list;
	}
	
	
	/***
	 * 查询并返回指定的对象集合
	 * @param sql
	 * @param cl
	 * @return
	 */
	public List queryForList(String sql, Class<?> cl) {
		Object bean = null;
		Object args = null;
		String name = null;
		Method method = null;
		Connection conn = null;
		ResultSet rs = null;
		Statement stat = null;
		ResultSetMetaData rsm = null;
		List beans = new ArrayList();
		try {
			conn = this.dataSource.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			rsm = rs.getMetaData();
			Server server = new Server();
			while (rs.next()){
				bean = cl.newInstance(); //实例
				for (int i = 1; i <= rsm.getColumnCount(); i++){
					name = rsm.getColumnName(i).toLowerCase();
					method = lookupWriteMethod(name, cl);
					Class type = getFieldType(name, cl);
					if (contains(rsm.getColumnTypeName(i),
						new String[]{"blob","longblob","mediumblob","tinyblob"})){
						byte[] bt = new byte[1024];
						Blob blob = rs.getBlob(i);
						if (blob != null && blob.length() != 0){
							InputStream is = blob.getBinaryStream();
							bt = new byte[(int)blob.length()];
							is.read(bt);
							is.close();
							args = bt;
						}
						
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"clob","nclob","text","longtext","mediumtext","tinytext"})){
						Clob clob = rs.getClob(i);
						Converter ct = new Converter();
						if (clob != null){
							args = ct.getStr(clob);
						}
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"date","datetime","timestamp"})){
						Timestamp ts = rs.getTimestamp(i);
						if (ts != null){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String date = sdf.format(ts);
							args = server.conver(type, date, null); //日期格式需要调整
						}
					} else {
						String str = rs.getString(i) == null ? "" : rs.getString(i);
						args = server.conver(type, str, null);
					}
					if (method != null) method.invoke(bean, args);
				}
				beans.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null){try{rs.close();}catch(SQLException e){}}
			if (stat != null){try{stat.close();}catch(SQLException e){}}
			if (conn != null){try{conn.close();}catch(SQLException e){}}
		}
		return beans;
	}

	
	/***
	 * 把数据映射到java对象
	 * @param sql
	 * @param cl
	 * @return
	 */
	public <T> T queryForObject(String sql, Class<T> cl) {
		Object bean = null;
		Object args = null;
		String name = null;
		Method method = null;
		Connection conn = null;
		ResultSet rs = null;
		Statement stat = null;
		ResultSetMetaData rsm = null;
		try {
			conn = this.dataSource.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			rsm = rs.getMetaData();
			bean = cl.newInstance(); //实例
			Server server = new Server();
			if (rs.next()){
				for (int i = 1; i <= rsm.getColumnCount(); i++){
					name = rsm.getColumnName(i).toLowerCase();
					method = lookupWriteMethod(name, cl);
					Class type = getFieldType(name, cl);
					if (contains(rsm.getColumnTypeName(i),
						new String[]{"blob","longblob","mediumblob","tinyblob"})){
						byte[] bt = new byte[1024];
						Blob blob = rs.getBlob(i);
						if (blob != null && blob.length() != 0){
							InputStream is = blob.getBinaryStream();
							bt = new byte[(int)blob.length()];
							is.read(bt);
							is.close();
							args = bt;
						}
						
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"clob","nclob","text","longtext","mediumtext","tinytext"})){
						Clob clob = rs.getClob(i);
						Converter ct = new Converter();
						if (clob != null){
							args = ct.getStr(clob);
						}
					} else if (contains(rsm.getColumnTypeName(i),
						new String[]{"date","datetime","timestamp"})){
						Timestamp ts = rs.getTimestamp(i);
						if (ts != null){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String date = sdf.format(ts);
							args = server.conver(type, date, null); //日期格式需要调整
						}
					} else {
						String str = rs.getString(i) == null ? "" : rs.getString(i);
						args = server.conver(type, str, null);
					}
					if (method != null) method.invoke(bean, args);
				}
			}
			return cl.cast(bean);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null){try{rs.close();}catch(SQLException e){}}
			if (stat != null){try{stat.close();}catch(SQLException e){}}
			if (conn != null){try{conn.close();}catch(SQLException e){}}
		}
		return null;
	}
	
	
	
	
	private Method lookupWriteMethod(String name, Class cl) {
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].getName().equals(name)) {
					PropertyDescriptor property = new PropertyDescriptor(fields[i]
						.getName(), cl);
					return property.getWriteMethod();
				}
				
			} catch (Exception e) {
				System.out.println("lookupWriteMethod() " + e.getMessage());
			} 
		}
		return null;
	}
	
	
	//字段类型
	private Class getFieldType(String name, Class cl) {
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].getName().equals(name)) {
					return fields[i].getType();
				}
				
			} catch (Exception e) {
				System.out.println("lookupWriteMethod() " + e.getMessage());
			} 
		}
		return null;
	}
	
	
	/****
	 * 删除、修改、插入操作
	 * @param preparedSql
	 * @param params
	 * @return
	 */
	public int update(String preparedSql, Object[] params) throws Exception{
		int count = 0;
		Object value = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(preparedSql);
			for (int i = 1; i <= params.length; i++) {
				value = params[i-1];
				if (value instanceof String){
					ps.setString(i, String.valueOf(value));
				} else if (value instanceof Integer) {
					ps.setInt(i, Integer.parseInt(value.toString()));
				} else if (value instanceof Long) {
					ps.setLong(i, Long.parseLong(value.toString()));
				} else if (value instanceof Double) {
					ps.setDouble(i, Double.parseDouble(value.toString()));
				} else if (value instanceof Float) {
					ps.setFloat(i, Float.parseFloat(value.toString()));
				} else if(value instanceof java.util.Date) {
					DateFormat format = DateFormat.getDateInstance();
					String date = format.format(value);
					ps.setDate(i ,java.sql.Date.valueOf(date));
				} else if(value instanceof java.sql.Date) {
					ps.setDate(i ,(java.sql.Date)value);
				} else if(value instanceof Timestamp) {
					ps.setTimestamp(i, (Timestamp)value);
				} else if(value instanceof byte[]) {
					byte[] by = (byte[])value;
					InputStream is = new ByteArrayInputStream(by);
					ps.setBinaryStream(i,is,by.length);
				} else if(value instanceof char[]) {
					ps.setString(i, new String((char[])value));
				} else{
					System.out.println("不支持的数据类型!" + value);
				}	
			}
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException("exception for the method update");
		} finally {
			if(ps != null){try {ps.close();} catch (SQLException e) {}}
			if(conn != null){try{conn.close();}catch(SQLException e){}}
		}
		
		return count;
	}
	
		
		/****
		 ****执行数据的批量操作****
		 *默认使用","分隔
		 *@param sqlstr
		 ****/
		public void customExecuteBatch(String sqlstr) throws Exception{
			Connection conn = null;
			Statement stat = null;
			try {
				conn = this.dataSource.getConnection();
				stat = conn.createStatement();
				String[] arr = sqlstr.split(",");
				for(String sql : arr)
				{
					stat.addBatch(sql);
				}
				stat.executeBatch();
			} catch (SQLException e) {
				throw new DataAccessException("execute batch exception");
			} finally{
				if(stat != null){try{stat.close();}catch(SQLException e){}}
				if(conn != null){try{conn.close();}catch(SQLException e){}}
			}
		}
		
		
		/**
		 * 创建一个映射集合的插入操作
		 * @param tableName
		 * @param columns
		 * @return
		 */
		
		public int createInsert(String tableName, Map<String,Object> columns) throws Exception{

				int count = 0;
				Connection conn = null;
				PreparedStatement ps = null;
		   try {
			    conn = this.dataSource.getConnection();
				columns = filter(conn, tableName, columns);
				StringBuffer sb = new StringBuffer("insert into ");
				StringBuffer set = new StringBuffer(")values(");
				sb.append(tableName).append("(");
				for(Iterator it = columns.keySet().iterator();it.hasNext();){
					String column = it.next().toString();
					sb.append(column).append(",");
					set.append("?,");
				}
				String sql = sb.toString();
				sql = sql.substring(0,sql.length()-1);
				String str = set.toString();
				str = str.substring(0,str.length()-1);
				sql += str+")";
				if(deBug || detail) System.out.println("insert_sql = "+sql);
				ps = conn.prepareStatement(sql);
				count = dataBaseControl(ps,columns);
				if(deBug || detail)
					System.out.println(String.format(
					  "The successful insertion of %s records for table %s", count,tableName));
			} catch (Exception e) {
				throw new DataAccessException("exception for the method createInsert");
			} finally {
				if(ps != null){try {ps.close();} catch (SQLException e) {}}
				if(conn != null){try{conn.close();}catch(SQLException e){}}
			}
			return count;
		}


	/****
	 **** 创建一个映射集合的更新操作 ****
	 * @param tableName
	 * @param columns
	 * @return
	 * @throws Exception
	 */
		
		public int customUpdateData(String tableName, Map<String,Object> columns) throws Exception{
			int count = 0;
			Connection conn = null;
			PreparedStatement ps = null;
			try{
				conn = this.dataSource.getConnection();
				columns = filter(conn, tableName, columns);
				PrimaryKey pk = new PrimaryKey(conn, tableName, detail);
				String condition = pk.getPrimaryKeyAndValue(columns);
				StringBuffer sb = new StringBuffer("update ");
				sb.append(tableName).append(" set ");
				for(Iterator it = columns.keySet().iterator();it.hasNext();){
					String column = it.next().toString().toLowerCase();
					sb.append(column).append(" = ?,");
				}
				String sql = sb.toString();
				sql = sql.substring(0,sql.length()-1);
				sql += " where "+condition;
				if(deBug || detail) System.out.println("update_sql = "+sql);
				ps = conn.prepareStatement(sql);
				count = dataBaseControl(ps,columns);
				if(deBug || detail)
					System.out.println(String.format(
					  "Successfully updated %s records for table %s", count,tableName));
			} catch(Exception e) {
				throw new DataAccessException("exception for the method customUpdateData");
			} finally {
				if(ps != null) try {ps.close();} catch (SQLException e) {}
				if(conn != null){try{conn.close();}catch(SQLException e){}}
			}
			return count;
		}
		
		
		
		/**
		 * 数据组合的核心中间件
		 * @param ps
		 * @param columns
		 * @return
		 * @throws SQLException 
		 */
		private int dataBaseControl(PreparedStatement ps, Map<String,Object> columns)
		throws SQLException{
			
			int count = 0;int num = 1;
			for(Map.Entry<String,Object> en : columns.entrySet()){
				String key = en.getKey();
				Object[] objs = (Object[])en.getValue();
				String columnType = String.valueOf(objs[0]);
				Object param = objs[1];
				if(param == null){
					ps.setNull(num,Types.NULL);
				} else if(contains(columnType,
					new String[]{"char","varchar","varchar2"})){
					parseInput(ps,num,param,"varchar2",key);
				} else if(contains(columnType,
					new String[]{"int","smallint","bigint","tinyint","mediumint"})){
					parseInput(ps,num,param,"int",key);
				} else if(contains(columnType,new String[]{"long"})){
					parseInput(ps,num,param,"long",key);
				} else if(contains(columnType,
					new String[]{"number","double","decimal"})){
					parseInput(ps,num,param,"number",key);
				} else if(contains(columnType,new String[]{"float"})){
					parseInput(ps,num,param,"float",key);
				} else if(contains(columnType,
					new String[]{"date","time","datetime","timestamp","year"})){
					parseInput(ps,num,param,"date",key);
				} else if(contains(columnType,
					new String[]{"blob","longblob","mediumblob","tinyblob"})){
					parseInput(ps,num,param,"blob",key);
				} else if(contains(columnType,
					new String[]{"clob","nclob","text","longtext","mediumtext","tinytext"})){
					parseInput(ps,num,param,"clob",key);
				} else{
					ps.setString(num,String.valueOf(param));
				}
				if(detail) System.out.println("column:"+key+" type:"+columnType); 
				
				num++;
			}
			count = ps.executeUpdate();
			return count;
		}
		
				
		
		/********
		 * 依据表字段过滤用户传入的键值对
		 * @param tableName
		 * @param columns
		 * @return
		 */
		
		public Map<String,Object> filter(Connection connection, 
				String tableName, Map<String,Object> columns){
			
		    Statement stmt = null;
		    ResultSet rs = null;
			try {
				String sql = "SELECT * FROM " + tableName + " WHERE 1 = 0";
				stmt = connection.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				if(columns != null && columns.size()>0)
				{
					List<String> keys = new ArrayList();
					for(Iterator it = columns.keySet().iterator();it.hasNext();)
					{
						boolean isexist = false;
						String key = it.next().toString();
						for(int i = 1;i<=columnCount;i++)
						{
							String columnName = rsmd.getColumnName(i).toLowerCase();
							String columnTypeName = rsmd.getColumnTypeName(i).toLowerCase();
							if(key.equalsIgnoreCase(columnName)){
								Object obj = columns.get(key);
								if(obj instanceof Object[]) {
									if(detail) System.out.println("has did continue");
									isexist = true;	
									continue;
								}
								columns.put(key, new Object[]{columnTypeName,obj});
								isexist = true;	
							}
								
						}
						if(!isexist)keys.add(key);
					}
					for(String s : keys){
						columns.remove(s);
						if(deBug || detail)
							System.out.println(s+"键值对被移除!");
					}
				}
				
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally{
				if(stmt != null) try {stmt.close();} catch (SQLException e) {}
				if(rs != null) try {rs.close();} catch (SQLException e) {}
			}
		   
			return columns;
		}
		
		
		
		/******
		 * 判断字段的数据类型
		 * @param columnsTypeName
		 * @param types
		 * @return
		 */
		
		public boolean contains(String columnsTypeName, String[] types){
			if(types != null && types.length > 0){
				for(String e : types){
					if(columnsTypeName.equalsIgnoreCase(e))
						return true;
				}
			}
			return false;
		}
		
		
		/****
		 * 匹配字段类型并做出处理
		 * @param key
		 * @throws SQLException 
		 */
		public void parseInput(PreparedStatement ps, int num, Object param
				,String sign, String key) throws SQLException{
			String regex = "";
			if ("varchar2".equalsIgnoreCase(sign)){
				String result = "";
				if (param instanceof java.util.Date){
					DateFormat format = DateFormat.getDateInstance();
					result = format.format(param);
				} else
					result = String.valueOf(param);
				ps.setString(num, result);
			} else if ("int".equalsIgnoreCase(sign)){
				int result = 0;
				regex = "^\\d+$";
				if (Pattern.matches(regex, param.toString()))
					result = Integer.parseInt(param.toString());
				else
					printErrOfParse(key);
				ps.setInt(num, result);
			} else if("long".equalsIgnoreCase(sign)){
				long result = 0;
				regex = "^\\d+$";
				if (Pattern.matches(regex, param.toString()))
					result = Long.parseLong(param.toString());
				else
					printErrOfParse(key);
				ps.setLong(num, result);
			} else if("number".equalsIgnoreCase(sign)){
				Double result = 0.0;
				regex = "^\\d+\\.\\d+$|^\\d+$";
				if (Pattern.matches(regex, param.toString()))
					result = Double.parseDouble(param.toString());
				else
					printErrOfParse(key);
				ps.setDouble(num, result);
			} else if("float".equalsIgnoreCase(sign)){
				Float result = 0.0f;
				regex = "^\\d+\\.\\d+$|^\\d+$";
				if (Pattern.matches(regex, param.toString()))
					result = Float.parseFloat(param.toString());
				else
					printErrOfParse(key);
				ps.setFloat(num, result);
			} else if ("date".equalsIgnoreCase(sign)){
				if (param instanceof java.util.Date){
					DateFormat format = DateFormat.getDateInstance();
					String date = format.format(param);
					ps.setDate(num,java.sql.Date.valueOf(date));
				} else if (param instanceof java.sql.Date){
					ps.setDate(num,(java.sql.Date)param);
				} else if (param instanceof Timestamp){
					ps.setTimestamp(num,(Timestamp)param);
				} else if (param instanceof String){
					if (Pattern.matches("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$",(String)param )){
						ps.setDate(num,java.sql.Date.valueOf((String)param));
					} else if (Pattern.matches(
						"^(\\d{4})-([0-1]\\d)-([0-3]\\d)\\" +
						"s+([0-5]\\d):([0-5]\\d):([0-5]\\d)$",(String)param)){
						ps.setTimestamp(num, Timestamp.valueOf((String)param));
					} else {
						ps.setNull(num, Types.NULL);
						if(detail || deBug)
							System.out.println("invalid date format - "+param.toString());
					}
				} else {
					ps.setNull(num, Types.NULL);
					if(detail || deBug)
						System.out.println("invalid date param - "+param.toString());
				}
				
			} else if ("blob".equalsIgnoreCase(sign)){
				byte[] by = null;
				InputStream is = null;
				if(param instanceof byte[])
					by = (byte[])param;	
				else
					by = new byte[1024];
				is = new ByteArrayInputStream(by);
				ps.setBinaryStream(num,is,by.length);
			} else if ("clob".equalsIgnoreCase(sign)){
				String content = "null";
				if (param instanceof char[]){
					content = new String((char[])param);
				} else if (param instanceof String){
					content = (String)param;
				}
				Reader reader = new StringReader(content);
				ps.setCharacterStream(num, reader,content.length());
			}
		}
		
		
		
		/******打印匹配信息*****/
		public void printErrOfParse(String key){
			if(detail) 
				System.out.println("不匹配的数据类型--" + key + "--已被处理");
		}
		
		
		/****
		 ****将Clob对象转化为字符串类型****
		 ****/
		
		public static class Converter{
			public Converter(){}
			public String getStr(Clob clob) throws SQLException{
				String str = "";
				BufferedReader br = new BufferedReader(clob.getCharacterStream());
				char[] ch = new char[(int)clob.length()];
				try {
					br.read(ch);
					str  =  new String(ch);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return str;
			}
		}
		
}
