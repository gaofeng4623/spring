package test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.controller.AbstractController;

public class CatController extends AbstractController{
	private CatService catService;
	
	public CatService getCatService() {
		return catService;
	}
	public void setCatService(CatService catService) {
		this.catService = catService;
	}
	
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		catService.run();
		return new ModelAndView("success");
	}

	
}
