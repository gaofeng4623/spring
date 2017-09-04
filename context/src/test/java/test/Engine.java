package test;

public class Engine {
	private Service server;
	
	public Service getServer() {
		return server;
	}

	public void setServer(Service server) {
		this.server = server;
	}

	public void todo() {
		server.getDao().print("测试>>>");
	}
	
}
