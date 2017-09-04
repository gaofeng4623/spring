package test.anno;

import java.sql.SQLException;

public interface Cat {
	public String runCat(String name) throws SQLException;
	public void init();
}
