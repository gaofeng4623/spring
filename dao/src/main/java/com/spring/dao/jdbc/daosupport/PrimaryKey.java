package com.spring.dao.jdbc.daosupport;

/**
 * 获取表的字段和Map数据的交集，以便准备sql语句。
 *
 * @author 赵斌 2003-6-23
 * @author 胡长城 2003-6-23
 * @version $Revision: 1.1 $
 */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrimaryKey {
  private List lstPrimaryKey = null;
  private RSMetaData rsmt = null;
  private boolean detail = false;

      /******************************************************************************
   构造函数***********************************************************************
       ******************************************************************************/
  public PrimaryKey(Connection conn, String tableName,boolean detail) throws SQLException {
	this.detail = detail;
	DatabaseMetaData dbmd = conn.getMetaData();
	String dataBaseType = dbmd.getDatabaseProductName();
	if(this.detail) System.out.println("dataBaseType---"+dataBaseType);
    Statement stmt = conn.createStatement();
    List lst = new ArrayList();
    int dot = tableName.indexOf('.');
    String sql;
    if (dot > 0 && dot != tableName.length() - 1) { //.不在第一个或最后一个
      String owner = tableName.substring(0, dot);
      tableName = tableName.substring(dot + 1);
      sql = getDataBasePkSql(tableName,dataBaseType,owner);
    }
    else {
      sql = getDataBasePkSql(tableName,dataBaseType);
    }
    //P.pl("获取Oracle主键sql=" + sql);
    ResultSet rs = stmt.executeQuery(sql);

    while (rs.next()) {
      lst.add(rs.getString("COLUMN_NAME")); //对于Oracle数据库，应该是大写
    }

    if (lst.size() == 0) {
      throw new SQLException("您指定的表" + tableName + "不存在，或者没有主键，不能处理！");
    }
    lstPrimaryKey = lst;
    rsmt = new RSMetaData(conn,tableName);
  }

      /*****************************************************************************
       *****************************************************************************
   /**/
   public String getPrimaryKey(int idx) {
     return (String) lstPrimaryKey.get(idx);
   }

      /*****************************************************************************
       *****************************************************************************
   /**/

   public int size() {
     return lstPrimaryKey.size();
   }

  /**
   * @param tmData  包含字段名-值对的Map，使用ComparatorIgnoreCase的TreeMap，以便key不区分大小写。
   * @return 主键和对应的值，可用来定位纪录
   * @throws SQLException
   */
  public String getPrimaryKeyAndValue(Map tmRowData) throws
      SQLException {
    StringBuffer pkAndValue = new StringBuffer(); //主键和对应的值，可用来定位纪录
    for (int i = 0; i < lstPrimaryKey.size(); i++) {
      if (i != 0) {
        pkAndValue.append(" AND ");
      }
      String key = (String) lstPrimaryKey.get(i);
      Object o = tmRowData.get(key);
      if (o == null)o = tmRowData.get(key.toLowerCase());
      
      if (o == null) {
        String msg = "主键" + key + "在HashMap中没有对应数据";
        throw new SQLException(msg);
      }
      o = ((Object[])o)[1]; //因为调用过filter所以要加个适配器
      for (int j = 1, m = rsmt.getColumnCount(); j <= m; j++) {
        //key是从字典表中查出的，全部是大写，rsmtc.getName(j)也是大写，所以，不需要ignore case。
        if (key.equals(rsmt.getName(j))) {
          int t = rsmt.getType(j);
          if (t == Types.BIGINT || t == Types.BIT || t == Types.DECIMAL ||
              t == Types.DOUBLE ||
              t == Types.FLOAT || t == Types.INTEGER || t == Types.NUMERIC ||
              t == Types.REAL ||
              t == Types.SMALLINT || t == Types.TINYINT) {
            pkAndValue.append(key + "=" + o.toString());
          }
          //data is primary key
          else if (t == Types.DATE || t == Types.TIME || t == Types.TIMESTAMP) {
            /** @todo //pkAndValue.append(key + "=" + convert2DataStr(o)); */
            //pkAndValue.append(key + "=" + convert2DataStr(o));
          }
          else {
            pkAndValue.append(key + "='" + o.toString() + "'");
          }
          break;
        }
        if (j == m) { //一直都没有找到此主键，应该不会发生这种情况，除非主键有问题
          String msg = "主键" + key + "在数据库表中没有对应字段。";
          throw new SQLException(msg);
        }
      }
    }
    return new String(pkAndValue);
  }
  
  
  /**
   * 获得检索主键存在与否的SQL
   * @param tableName
   * @param tmRowData
   * @return
   * @throws SQLException
   */
  public String getPrimarykeySql(String tableName,Map tmRowData) throws SQLException{
	  String condition = getPrimaryKeyAndValue(tmRowData);
	  String sql = String.format("select * from %s where %s", tableName,condition);
	  if(detail) System.out.println("condition_sql :" + sql);
	  return sql;
  }
 
  /**
   * 获得删除主键SQL
   * @param tableName
   * @param tmRowData
   * @return
   * @throws SQLException 
   */
  public String getDeleteSQL(String tableName,Map tmRowData) throws SQLException{
	  String condition = getPrimaryKeyAndValue(tmRowData);
	  String sql = String.format("delete from %s where %s", tableName,condition);
	  if(detail) System.out.println("condition_sql :" + sql);
	  return sql;
  }
  
  /*****
   * 判断数据库类型
   * @param tableName
   * @param DatabaseProductName
   * @param owner
   * @return
   */
  public String getDataBasePkSql(String tableName,String DatabaseProductName,String owner){
	  String sql = "";
	  if("MYSQL".equalsIgnoreCase(DatabaseProductName)){
		 if(detail) System.out.println("get---MYSQLOWNER——SQL");
		sql ="SELECT t.TABLE_NAME,t.CONSTRAINT_TYPE," +
		"c.COLUMN_NAME,c.ORDINAL_POSITION FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS" +
		" AS t,INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS c WHERE t.TABLE_NAME = " +
		"c.TABLE_NAME AND t.TABLE_NAME='"+tableName.toLowerCase()+"' AND c.TABLE_SCHEMA='"+owner.toLowerCase()+"' AND" +
		" t.CONSTRAINT_TYPE = 'PRIMARY KEY'";
	  }else if("ORACLE".equalsIgnoreCase(DatabaseProductName)){
		  if(detail) System.out.println("get---ORACLEWNER——SQL");
		  sql = "SELECT *" +
          " FROM All_CONSTRAINTS C, All_IND_COLUMNS IC" +
          " WHERE C.TABLE_NAME = IC.TABLE_NAME" +
          " AND C.CONSTRAINT_NAME = IC.INDEX_NAME" +
          " AND C.TABLE_NAME = '" + tableName.toUpperCase() + "'" +
          " AND C.CONSTRAINT_TYPE = 'P'" +
          " AND C.OWNER = '" + owner.toUpperCase() + "'" +
          " ORDER BY IC.COLUMN_POSITION";
	  }else System.out.println("不支持此类数据库");
	  return sql;
  }
  
  public String getDataBasePkSql(String tableName,String DatabaseProductName){
	  String sql = "";
	  if("MYSQL".equalsIgnoreCase(DatabaseProductName)){
		  if(detail) System.out.println("GET--MYSQL--SQL");
		sql ="SELECT t.TABLE_NAME,t.CONSTRAINT_TYPE," +
		"c.COLUMN_NAME,c.ORDINAL_POSITION FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS" +
		" AS t,INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS c WHERE t.TABLE_NAME = " +
		"c.TABLE_NAME AND t.TABLE_NAME='"+tableName.toLowerCase()+"' AND" +
		" t.CONSTRAINT_TYPE = 'PRIMARY KEY'";
	  }else if("ORACLE".equalsIgnoreCase(DatabaseProductName)){
		 if(detail) System.out.println("GET--ORACLE--SQL");
		  sql = "SELECT *" +
          " FROM USER_CONSTRAINTS C, USER_IND_COLUMNS IC" +
          " WHERE C.TABLE_NAME = IC.TABLE_NAME" +
          " AND C.CONSTRAINT_NAME = IC.INDEX_NAME" +
          " AND C.TABLE_NAME = '" + tableName.toUpperCase() + "'" +
          " AND C.CONSTRAINT_TYPE = 'P'" +
          " ORDER BY IC.COLUMN_POSITION";
	  }else System.out.println("不支持此类数据库");
	  return sql;
  }

  public void setDetail(boolean detail){
	  this.detail = detail;
  }
}