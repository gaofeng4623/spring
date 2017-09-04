package test.bean;

import java.lang.reflect.Method;

public class TT {

	public void test(Method method, String name) {
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Class[] classes = null;
		Object[] aa = new Object[2];
		Method[] mds = TT.class.getDeclaredMethods();
		System.out.println(mds.length);
		for (int i=0 ;i < mds.length; i++) {
			Method method = mds[i];
			classes = method.getParameterTypes();
			for (Class cl : classes) {
				System.err.println(cl.getName());
				
			}
		}

	}

}
