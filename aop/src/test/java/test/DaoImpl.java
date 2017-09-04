package test;

public class DaoImpl implements Dao {
	private String name;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void print(String word) {
		System.out.println("注入DaoImpl对象>>>" + word);
		System.out.println("name = " + name);
		System.out.println("url = " + url);
	}

}
