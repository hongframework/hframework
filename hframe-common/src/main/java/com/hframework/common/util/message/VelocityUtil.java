package com.hframework.common.util.message;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityUtil {
	
	
	public static String produceTemplateContent(String templateName,Map dataMap){
		
		VelocityContext context=new VelocityContext();
		context.put("MAP", dataMap);
		
		Properties properties=new Properties();
		properties.put("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//		properties.setProperty("resource.loader", "file");
		//modify by zqh 2016-10-18
//		properties.setProperty("userdirective", "org.apache.velocity.tools.generic.directive.Ifnull");
//		properties.setProperty("userdirective", "org.apache.velocity.tools.generic.directive.Ifnotnull");

//		String basePath = "D:/tomcat6.0/Tomcat6.0_AutoSystem/webapps/Hframe/WEB-INF/classes";//???????????д·????????
//		String basePath = "D:\\my_workspace\\hframe-trunk\\hframe-generator-web\\src\\main\\resources";//???????????д·????????
//		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);

		if(templateName==null||"".equals(templateName)){
			
			return "";
		}
		
		StringWriter sw=new StringWriter();

		try {
			Velocity.init(properties);
			
			
			Template template=null;
			
			template=Velocity.getTemplate(templateName,"UTF-8");
			
			template.merge(context, sw);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sw.toString();
	}
	public static void produceTemplateContent(String templateName,Map dataMap,Writer writer){
		
		VelocityContext context=new VelocityContext();
		context.put("MAP", dataMap);
		
		Properties properties=new Properties();
		properties.setProperty("resource.loader", "file");
		properties.setProperty("userdirective", "org.apache.velocity.tools.generic.directive.Ifnull");
		properties.setProperty("userdirective", "org.apache.velocity.tools.generic.directive.Ifnotnull");

//		String basePath = "D:/tomcat6.0/Tomcat6.0_AutoSystem/webapps/Hframe/WEB-INF/classes";//???????????д·????????
//		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
//		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, ".");
		properties.put("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		if(templateName==null||"".equals(templateName)){
			return ;
		}

		try {
			Velocity.init(properties);
			
			
			Template template=null;
			
			template=Velocity.getTemplate(templateName,"UTF-8");
			
			template.merge(context, writer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ;
	}

	public static void main(String[] args) {
		
//		Map  map=new HashMap();
//
//		map.put("colNum", 4);
//		map.put("info", "???????");
//		System.out.println("--->" + produceTemplateContent("com/hframe/tag/vm/autoformtemplate.vm", map));

		Map map = new HashMap();
		map.put("companyCode", "aa");
		map.put("programCode", "bb");
		String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/compileBat.vm", map);
		System.out.println(content);
	}
	
}
