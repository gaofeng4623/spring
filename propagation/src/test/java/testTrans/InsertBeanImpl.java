package testTrans;

import com.spring.dao.jdbc.daosupport.JdbcDaoSupport;

public class InsertBeanImpl extends JdbcDaoSupport implements InsertBean{

	public void insert(String name, int age) throws Exception {
		String sql = "insert into user (name,age) values (?,?)";
		getJdbcTemplate().update(sql, new Object[] {name, age});
	}

}
