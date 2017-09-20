package test.anno;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import test.Dao;

import com.spring.beans.common.annotation.Controller;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.ModelMap;
import com.spring.mvc.common.RequestMethod;
import com.spring.mvc.common.annotation.ExceptionHandler;
import com.spring.mvc.common.annotation.ModelAttribute;
import com.spring.mvc.common.annotation.PathVariable;
import com.spring.mvc.common.annotation.RequestMapping;
import com.spring.mvc.common.annotation.RequestParam;
import com.spring.mvc.common.annotation.SessionAttributes;

//http://blog.csdn.net/justyuze/article/details/53203630 hiddenhttpmethodfilter原理
@Controller
//@Scope("prototype")
@RequestMapping("user")
public class TestController extends BaseController{
	@Resource
	private Dao impl;

	
	/***
	 * 测试注解
	 * @return
	 * http://localhost:8080/Spring/user/getPage3/98/test.do?name2=123&age=28&love=loveyou&user=test
	 * @throws Exception 
	 */
	@RequestMapping(value = "getPage{pageNo}/{num}/test.do", method = RequestMethod.GET)
	public String getName(@RequestParam(value ="name2", defaultValue="moren") String name, @RequestParam(value ="def", defaultValue="moren")
	String def,@PathVariable int pageNo, @PathVariable("num") int testNum, @RequestParam("age") int age,@SessionAttributes("love") String love, 
			HttpServletRequest request, @ModelAttribute("user") User user) throws Exception {
		//impl.print("测试");
		System.out.println("pageNo ==" + pageNo + " num ==" + testNum);
		System.out.println("name = " + name + " age=" + age);
		System.out.println("reqAttr = " +request.getAttribute("age"));
		System.out.println("sessionAttr = " + request.getSession().getAttribute("love"));
		System.out.println("user_salary = " + user.getSalary());
		System.out.println("user_name = " + user.getName());
		System.out.println("user_age = " + user.getAge());
		System.out.println("default = " + def);
		System.out.println(user.getLoves());
		//if (1 == 1) throw new SQLException("测试异常.............");
		return "success";
	}
	
	
	@ModelAttribute("user")
	public User getUser(@RequestParam("name") String name, HttpServletRequest request) throws Exception {
		System.out.println("执行Model >>>" + name);
		User user = new User(1);
		user.setSalary(3920);
		//if (1 == 1) throw new Exception("异常啦");
		return user;
	}
	
	/***
	 * 测试上传注解
	 * @return
	 * http://localhost:8080/Spring/upload.jsp
	 */
	@RequestMapping(value = "/test.do", method = RequestMethod.POST)
	public ModelAndView test(@RequestParam("annoName") String anno, int[] age, Date birthday, File[] attach,
			@ModelAttribute("user")User user, HttpServletRequest request, ModelMap modelMap) {
		System.out.println("注解参数 == " + anno);
		System.out.println("requestAttr == " + request.getAttribute("user"));
		System.out.println("name=" + user.getName() + " -- passWord" + user.getPassWord());
		System.out.println("age = " + age);
		System.out.println("birthday = " + birthday);
		System.out.println("salary = " + user.getSalary());
		System.out.println("modelMap-user = " + modelMap.getAttribute("user"));
		System.out.println("内置" + user.getAttach());
		System.out.println("loves == " + user.getLoves()[0]);
		System.out.println("person.loves == " + user.getPerson().getLoves()[0]);
		return new ModelAndView("jsonView", user);  //jsonView为视图转换器的beanId
	}
	
	/**
	 * http://localhost:8080/Spring/user/test22/pa11th/33.do
	 * @param pageNo
	 * @param num
	 * @param what
	 * @return
	 */
	@RequestMapping(value = "/test{pageNo}/pa{num}th/{what}.do", method = RequestMethod.GET)
	public String testPath1(@PathVariable int pageNo, @PathVariable int num, @PathVariable int what) {
		System.out.println(pageNo + "--" + num + "--" + what);
		return "test";  //jsonView为视图转换器的beanId
	}

	/**
	 * http://localhost:8080/Spring/user/test22/11/user.do
	 * @param page_no
	 * @param n_um
	 * @return
	 */
	@RequestMapping(value = "/test{page_no}/{n_um}/user.do", method = RequestMethod.GET)
	public String testPath2(@PathVariable int page_no, @PathVariable int n_um) {
		System.out.println(page_no + "--" + n_um);
		return "test2";  //jsonView为视图转换器的beanId
	}
	
	@ExceptionHandler(value={IOException.class, SQLException.class})
	public String exp(Exception ex, HttpServletRequest request) {
		System.out.println("异常处理机制1>>>>>>>>>>>" + ex.getMessage());
		request.setAttribute("ex", ex);   
		return "erro";
	}
	
	@ExceptionHandler(value={IllegalArgumentException.class})
	public String exps(Exception ex, HttpServletRequest request) {
		System.out.println("异常处理机制2>>>>>>>>>>>" + ex);
		request.setAttribute("ex", ex);   
		return "erro2";
	}
	
	public void test2() {
		
	}
	
	public static void main(String[] args) {
		String test1 = "getPage{no}";
		System.out.println(Pattern.matches("getPage['{'].*['}']", "getPage{2111}"));
		System.out.println(test1.replaceAll("['{'].*['}']", "['{'].*['}']"));
		System.out.println(test1.contains("['{'].*['}']"));
		System.out.println("test{222}haha".replaceAll("\\{|\\}", ""));
		String url = "/test{pag-eNo}/uiy{nu_m}/user.do";
		String tempUrl = url.replaceAll("['{'][0-9a-zA-Z_-]*['}']", ",");
		System.out.println("tempUrl = "+ tempUrl);
		String[] tempModelPathArr = tempUrl.split(",");
		for (String s : tempModelPathArr) {
			System.out.println("s =" + s);
		}
		url = "dd.do";
		System.out.println(Pattern.matches("((?!/).)*", url));
		
	}
	
	public static Method getMethod(String name, Class cl) {
		Method[] methods = cl.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
}
