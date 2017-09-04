package com.spring.dao.jdbc.daosupport;

/**
 * 获取表的字段和Map数据的交集，以便准备sql语句。
 *
 * @author 赵斌 2003-6-23
 * @author 胡长城 2003-6-23
 * @version $Revision: 1.1 $
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

public class RSMetaData {
  private ColumnMD data[] = null;
  private HashMap hmData = null;
  private int columnCount;

      /******************************************************************************
   构造函数***********************************************************************
       ******************************************************************************/
  public RSMetaData(Connection conn, String tableName) throws SQLException {
    String sql = "SELECT * FROM " + tableName + " WHERE 1=0";
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    setResultSet(rs);
    rs.close();
    stmt.close();
  }

      /*****************************************************************************
       *****************************************************************************
   /**/
   public RSMetaData(ResultSet rs) throws SQLException {
     setResultSet(rs);
   }

      /*****************************************************************************
       *****************************************************************************
   /**/
   private void setResultSet(ResultSet rs) throws SQLException
   {
     ResultSetMetaData rsmt = rs.getMetaData();
     columnCount = rsmt.getColumnCount();
     data = new ColumnMD[columnCount]; //ResultSetMetaData是从1开始的，数组是从0开始的
     hmData = new HashMap();
     for(int i = 1; i <= columnCount; i++)
        {
         ColumnMD meta = new ColumnMD();
         String fieldName = rsmt.getColumnName(i);
         meta.setName(fieldName);
         //meta.setDisplayName(TableField.displayName(fieldName));
         //rsmt.isNullable(i)有3个返回值，对于 columnNullableUnknown 我从来没有遇到过，所以，认为只有2个返回值。
         meta.setNullable(rsmt.isNullable(i) == ResultSetMetaData.columnNullable);
         int iType = rsmt.getColumnType(i);
         meta.setType(iType);
         int precision;
         if(iType == Types.BLOB ||
            iType == Types.CLOB ||
            iType == Types.LONGVARBINARY ||
            iType == Types.LONGVARCHAR)
           {
            precision = Integer.MAX_VALUE;
           }
         else
           {
            precision = rsmt.getColumnDisplaySize(i); //rsmt.getPrecision(i)在Sybase的jdbc应该是取数值精度，如果有字符类型的会出错。
           }
         meta.setPrecision(precision);
         meta.setScale(rsmt.getScale(i));
         meta.setTypeName(rsmt.getColumnTypeName(i));
         data[i - 1] = meta;
         hmData.put(fieldName,meta);
     }
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public ColumnMD getColumnMetaData(int i) {
     return data[i - 1];
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public ColumnMD getColumnMetaData(String columnName) {
     if (columnName == null) {
       return null;
     }
     ColumnMD cmd=(ColumnMD)hmData.get(columnName);
     return cmd;
   }

      /*****************************************************************************
       *****************************************************************************
   /**/
   public int getColumnCount() {
     return this.columnCount;
   }

      /*****************************************************************************
       *****************************************************************************
   /**/
   public String getName(int i) {
     return getColumnMetaData(i).getName();
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public String getDisplayName(int i) {
     return getColumnMetaData(i).getDisplayName();
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public int getType(int i) {
     return getColumnMetaData(i).getType();
   }

      /*****************************************************************************
       *****************************************************************************
     /**/
   public String getTypeName(int i) {
     return getColumnMetaData(i).getTypeName();
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public int getPrecision(int i) {
     return getColumnMetaData(i).getPrecision();
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public int getScale(int i) {
     return getColumnMetaData(i).getScale();
   }

      /*****************************************************************************
       *****************************************************************************
    /**/
   public boolean isNullable(int i) {
     return getColumnMetaData(i).isNullable();
   }
}
