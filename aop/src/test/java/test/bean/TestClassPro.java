package test.bean;

public class TestClassPro {
	private Class serviceClass;
	
	public Class getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class serviceClass) {
		//System.out.println("注入::" + serviceClass.getName());
		this.serviceClass = serviceClass;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
