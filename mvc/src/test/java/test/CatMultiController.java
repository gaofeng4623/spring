package test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.context.WebApplicationContext;
import com.spring.context.WebApplicationContextUtils;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.RequestContextUtils;
import com.spring.mvc.controller.MultiActionController;

public class CatMultiController extends MultiActionController {
	private CatService catService;

	public CatService getCatService() {
		return catService;
	}

	public void setCatService(CatService catService) {
		this.catService = catService;
	}

	public ModelAndView run(HttpServletRequest request,
			HttpServletResponse response) {
		catService.run();
		return new ModelAndView("success");
	}

	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		WebApplicationContext mvcContext 
			= RequestContextUtils.getWebApplicationContext(request); //mvc上下文
		Cat mvcCat = (Cat) mvcContext.getBean("mvcCat");
		catService.addCat(mvcCat);
		WebApplicationContext context = WebApplicationContextUtils
			.getWebApplicationContext(request.getSession().getServletContext()); //Spring上下文
		Cat globalCat = (Cat) mvcContext.getBean("globalCat");
		Cat globalCat2 = (Cat) context.getBean("globalCat");
		System.out.println(globalCat + " -- " + globalCat2);
		CatService cs = (CatService) context.getBean("catService");
		CatService cs2 = (CatService) mvcContext.getBean("catService");
		System.out.println(cs + " -- " + cs2);
		catService.addCat(globalCat);
		return new ModelAndView("success");
	}
	
	public ModelAndView dance(HttpServletRequest request,
			HttpServletResponse response) {
		catService.dance();
		return new ModelAndView("success");
	}
}
