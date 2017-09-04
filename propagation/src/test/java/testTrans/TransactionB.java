package testTrans;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.spring.beans.common.annotation.Component;
import com.spring.transaction.common.Propagation;
import com.spring.transaction.common.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
public class TransactionB implements TransactionInner{
	@Resource
	private DataSource dataSource;
	
	public String test() throws SQLException {
		Connection conn = dataSource.getConnection();
		System.out.println("connB = " + conn.hashCode());
		Statement stat = conn.createStatement();
		stat.execute("insert into testB(name) values('测试B')");
		if (true) throw new SQLException("test");
		conn.close();
		return null;
	}
}
