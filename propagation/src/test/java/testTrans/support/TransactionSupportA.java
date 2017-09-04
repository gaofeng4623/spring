package testTrans.support;

import java.sql.Connection;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import testTrans.TransactionInner;

import com.spring.beans.common.annotation.Component;
import com.spring.transaction.common.Propagation;
import com.spring.transaction.common.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class TransactionSupportA implements TransactionInner{
	@Resource
	private DataSource dataSource;
	@Resource
	private TransactionInner transactionSupportB;

	public String test() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println("connA = " + conn);
		Statement stat = conn.createStatement();
		stat.execute("insert into testA(name) values('测试A1')");
		//conn.close();
		transactionSupportB.test();
		stat.execute("insert into testA(name) values('测试A2')");
		//if (true) throw new SQLException("哈哈哈"); //因为没定义rollbackfor，所以不会抛出
		return null;
	}
	
	
}
