package test.anno;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.spring.beans.factory.config.WebDataBinder;
import com.spring.beans.propertyeditors.CustomDateEditor;
import com.spring.beans.propertyeditors.CustomNumberEditor;
import com.spring.beans.propertyeditors.DoubleEditor;
import com.spring.beans.propertyeditors.FloatEditor;
import com.spring.beans.propertyeditors.IntegerEditor;
import com.spring.beans.propertyeditors.LongEditor;
import com.spring.beans.propertyeditors.PropertiesEditorSupport;
import com.spring.mvc.common.annotation.InitBinder;

public class BaseController {
	
	/**
	 * 初始化数据绑定
	 * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
	 * 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder    
	public void initBinder(WebDataBinder binder) {
       //binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true)); 
	   //注册匿名类
       binder.registerCustomEditor(Date.class, new PropertiesEditorSupport() {
		@Override
		public void setAsText(String text) throws Exception {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(text);
			setValue(date);
		}
       });
       binder.registerCustomEditor(int.class, new PropertiesEditorSupport(){

		@Override
		public void setAsText(String text) throws Exception {
			String value = text == null || "".equals(text) ? "0" : text;
			setValue(Integer.parseInt(value));
		}
       });    
       //binder.registerCustomEditor(int.class, new IntegerEditor());    
       binder.registerCustomEditor(long.class, new CustomNumberEditor(long.class, true));  
      // binder.registerCustomEditor(long.class, new LongEditor());    
       binder.registerCustomEditor(double.class, new DoubleEditor());    
       binder.registerCustomEditor(float.class, new FloatEditor());    
	 } 
	
	public static void main(String[] args) {
		System.out.println(int.class.getName());
	}
}
