package testTrans.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import testTrans.TransactionInner;

import com.spring.beans.common.annotation.Component;
import com.spring.transaction.common.Propagation;
import com.spring.transaction.common.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = SQLException.class)
public class TransactionSupportB implements TransactionInner{
	@Resource
	private DataSource dataSource;
	
	public String test() throws SQLException {
		Connection conn = dataSource.getConnection();
		System.out.println("connB = " + conn);
		Statement stat = conn.createStatement();
		stat.execute("insert into testB(name) values('测试B1')");
		stat.execute("insert into testB(name) values('测试B2')");
		if (true) throw new SQLException("回滚点B");
		stat.execute("insert into testB(name) values('测试B3')");
		conn.close();
		return null;
	}

}
