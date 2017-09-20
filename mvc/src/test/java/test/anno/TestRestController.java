package test.anno;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import test.Dao;

import com.spring.beans.common.annotation.RestController;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.ModelMap;
import com.spring.mvc.common.RequestMethod;
import com.spring.mvc.common.annotation.ModelAttribute;
import com.spring.mvc.common.annotation.PathVariable;
import com.spring.mvc.common.annotation.RequestMapping;
import com.spring.mvc.common.annotation.RequestParam;
import com.spring.mvc.common.annotation.SessionAttributes;

//构建基于restful的webservice http://localhost:8080/Spring/restful.jsp
@RestController
@RequestMapping("rest")
public class TestRestController {
	@Resource
	private Dao impl;
	
	/***
	 * 测试注解
	 * @return
	 * http://localhost:8080/Spring/rest/base-instance/D2913-2B32-8734-9898/98.do?name2=123&age=28&love=loveyou&user=test
	 * @throws Exception 
	 */
	@RequestMapping(value="base-instance/{baseInstanceId}/{itemId}", method = RequestMethod.GET, produces = "application/json")
	public User getName(@RequestParam("name2") String name, @PathVariable int itemId,
			@PathVariable("baseInstanceId") String testNum, @RequestParam("age") int age,@SessionAttributes("love") String love,
			HttpServletRequest request, @ModelAttribute("user") User user) throws Exception {
		//impl.print("测试");
		System.out.println(" baseInstanceId ==" + testNum + ",itemId ==" + itemId);
		System.out.println("name = " + name + " age=" + age);
		System.out.println("reqAttr = " +request.getAttribute("age"));
		System.out.println("sessionAttr = " + request.getSession().getAttribute("love"));
		System.out.println("user_salary = " + user.getSalary());
		System.out.println("user_name = " + user.getName());
		System.out.println("user_age = " + user.getAge());
		//if (1 == 1) throw new SQLException("测试异常.............");
		return user;
	}

	@RequestMapping(value="process-instance/{processInstanceId}", method = RequestMethod.GET, produces = "application/json")
	public User getName1(@PathVariable("processInstanceId") int id) {
		System.out.println("执行查询" + id);
		return new User(id);
	}
	
	@RequestMapping(value="process-instance/{processInstanceId}", method = RequestMethod.POST, produces = "application/json")
	public User getName2(@PathVariable("processInstanceId") int id) {
		System.out.println("执行插入" + id);
		return new User(id);
	}
	
	@RequestMapping(value="process-instance/{processInstanceId}", method = RequestMethod.PUT, produces = "application/json")
	public User getName3(@PathVariable("processInstanceId") int id) {
		System.out.println("执行更新" + id);
		return new User(id);
	}
	
	@RequestMapping(value="process-instance/{processInstanceId}", method = RequestMethod.DELETE, produces = "application/json")
	public User getName4(@PathVariable("processInstanceId") int id) {
		System.out.println("执行删除" + id);
		return new User(id);
	}
	
	@ModelAttribute("user")
	public User getUser(@PathVariable("processInstanceId") int id, HttpServletRequest request) throws Exception {
		System.out.println("执行Model >>>" + id);
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
	public ModelAndView test(@RequestParam("annoName") String anno, int age, Date birthday, File[] attach,
			@ModelAttribute("user")User user, HttpServletRequest request, ModelMap modelMap) {
		System.out.println("注解参数 == " + anno);
		System.out.println("requestAttr == " + request.getAttribute("user"));
		System.out.println("name=" + user.getName() + " -- passWord" + user.getPassWord());
		System.out.println("birthday = " + birthday);
		System.out.println("salary = " + user.getSalary());
		System.out.println("modelMap-user = " + modelMap.getAttribute("user"));
		System.out.println("内置" + user.getAttach());
		return new ModelAndView("jsonView", user);  //jsonView为视图转换器的beanId
	}
	
}
