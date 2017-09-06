package test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.controller.AbstractController;

public class WelcomeController extends AbstractController{

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("welcome >>>>>>>>>>>>>");
		return null;
	}

}
