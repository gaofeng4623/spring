package testTrans.nested;

import java.sql.Connection;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import testTrans.TransactionInner;

import com.spring.beans.common.annotation.Component;
import com.spring.transaction.common.annotation.Transactional;

@Component
@Transactional
public class TransactionNestedA implements TransactionInner{
	@Resource
	private DataSource dataSource;
	@Resource
	private TransactionInner transactionNestedB;
	@Resource
	private TransactionInner transactionNestedC;

	public String test() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println("connA = " + conn);
		Statement stat = conn.createStatement();
		stat.execute("insert into testA(name) values('测试A1')");
		conn.close();
		transactionNestedB.test();
		stat.execute("insert into testA(name) values('测试A2')");
		transactionNestedC.test();
		stat.execute("insert into testA(name) values('测试A3')");
		//if (true) throw new SQLException("哈哈哈"); //因为没定义rollbackfor，所以不会抛出
		return null;
	}
	
	
}
