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
@Transactional(propagation = Propagation.SUPPORTS)
public class TransactionA implements TransactionInner{
	@Resource
	private DataSource dataSource;
	@Resource
	private TransactionInner transactionB;
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SQLException.class)
	public String test() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println("connA = " + conn.hashCode());
		Statement stat = conn.createStatement();
		stat.executeUpdate("insert into testA(name) values('测试A1')");
		transactionB.test();
		stat.executeUpdate("insert into testA(name) values('测试A2')");
		conn.close();
		return null;
	}
	
	
}
