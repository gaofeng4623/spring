package testTrans.notsupport;

import java.sql.Connection;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import testTrans.TransactionInner;

import com.spring.beans.common.annotation.Component;
import com.spring.transaction.common.annotation.Transactional;

@Component
@Transactional
public class TransactionRequiredA implements TransactionInner{
	@Resource
	private DataSource dataSource;
	@Resource
	private TransactionInner transactionNotSupportB;

	public String test() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println("connA = " + conn);
		Statement stat = conn.createStatement();
		stat.execute("insert into testA(name) values('测试A1')");
		transactionNotSupportB.test();
		stat.execute("insert into testA(name) values('测试A2')");
		conn.close();
		//if (true) throw new SQLException("哈哈哈"); //因为没定义rollbackfor，所以不会抛出
		return null;
	}
	
	
}
