package com.spring.dao.jdbc.daosupport;


public class ColumnMD
{
  private String name;
  private String displayName;
  private int type;
  private String typeName;
  private int precision;
  private int scale;
  private boolean nullable = true;
  private boolean primaryKey = false;
  private boolean disabled = false;
  private boolean islink = false;
  /******************************************************************************
  构造函数**********************************************************************
  ******************************************************************************/
  public ColumnMD()
  {
     }
  /******************************************************************************
  构造函数**********************************************************************
  ******************************************************************************/
  public ColumnMD(String[] name)
  {
   this.name=name[0];	
   this.displayName=name[1];	
     }
  /******************************************************************************
  构造函数**********************************************************************
  ******************************************************************************/
  public ColumnMD(String enname,String cnname)
  {
   this.name=enname;	
   this.displayName=cnname;	
     }

  /*****************************************************************************
   *****************************************************************************/
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /*****************************************************************************
   *****************************************************************************/
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setType(int type) {
    this.type = type;
  }
  public int getType() {
    return type;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
  public String getTypeName() {
    return typeName;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setPrecision(int precision) {
    this.precision = precision;
  }
  public int getPrecision() {
    return precision;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setScale(int scale) {
    this.scale = scale;
  }
  public int getScale() {
    return scale;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }
  public boolean isNullable() {
    return nullable;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setPrimaryKey(boolean primaryKey) {
  this.primaryKey = primaryKey;
}
public boolean isPrimaryKey() {
  return primaryKey;
}

/*****************************************************************************
 *****************************************************************************/
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
  public boolean isDisabled() {
    return disabled;
  }

  /*****************************************************************************
   *****************************************************************************/
  public void setLink(boolean islink) {
    this.islink = islink;
  }
  public boolean isLink() {
    return islink;
  }
}
