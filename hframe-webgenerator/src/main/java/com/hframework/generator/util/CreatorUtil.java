package com.hframework.generator.util;

import com.hframework.common.springext.properties.PropertyConfigurerUtils;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.generator.web.constants.CreatorConst;
import com.hframework.beans.class0.Class;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class CreatorUtil {

//	public static final String projectBasePath =
//			PropertyConfigurerUtils.getProperty(CreatorConst.PROJECT_BASE_FILE_PATH);
//	public static final String projectTomcatBasePath =
//			PropertyConfigurerUtils.getProperty(CreatorConst.PROJECT_TOMCAT_BASE_FILE_PATH);

	public static String getJavaClassName(String tableName) {

		String returnName = "";

		tableName = tableName.toLowerCase();

		String[] parts = tableName.split("[_]+");
		for (String part : parts) {
			if (!"".equals(part)) {
				returnName += part.substring(0, 1).toUpperCase()
						+ part.substring(1);
			}
		}

		return returnName;
	}

	public static String getJavaVarName(String tableName) {

		String returnName="";

		tableName=tableName.toLowerCase();

		String[] parts=tableName.split("[_]+");
		for (String part : parts) {
			if(!"".equals(part)){
				returnName+=part.substring(0,1).toUpperCase()+part.substring(1);
			}
		}

		return returnName.substring(0,1).toLowerCase()+returnName.substring(1);
	}

	/**
	 * @param companyName
	 * @param projectName
	 * @param tableName
	 * @return 获取SQL文件在项目中存放的路径即名称
	 * @throws Exception
	 */
	public static String getSQLFilePath(String companyName,
										String projectName,String tableName) throws Exception {

		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		companyName = StringUtils.isBlank(companyName)?"":""+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":""+(projectName);

		String tmpDir = PropertyConfigurerUtils.containProperty(CreatorConst.GENERATOR_TMP_DIR)
				&& StringUtils.isNotBlank(PropertyConfigurerUtils.getProperty(CreatorConst.GENERATOR_TMP_DIR))?
				PropertyConfigurerUtils.getProperty(CreatorConst.GENERATOR_TMP_DIR) :
				CreatorUtil.class.getClassLoader().getResource("").getPath() + "/tmp";

		return tmpDir + File.separatorChar +
				PropertyConfigurerUtils.getProperty(CreatorConst.SQL_FILE_PATH,
				companyName.toLowerCase(),projectName.toLowerCase(),tableName.toLowerCase());
	}

	/**
	 * @param companyName
	 * @param projectName
	 * @param tableName
	 * @return 目标项目的目录
	 * @throws Exception
	 */
	public static String getTargetProjectBasePath(String companyName,
										String projectName,String tableName) throws Exception {
//
//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}

		companyName = StringUtils.isBlank(companyName)?"":""+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":""+(projectName);

		String tmpDir = PropertyConfigurerUtils.containProperty(CreatorConst.TARGET_PROJECT_BASE_PATH)
				&& StringUtils.isNotBlank(PropertyConfigurerUtils.getProperty(CreatorConst.TARGET_PROJECT_BASE_PATH))?
				PropertyConfigurerUtils.getProperty(CreatorConst.TARGET_PROJECT_BASE_PATH) :
				CreatorUtil.class.getClassLoader().getResource("").getPath() + "/projects";

		return tmpDir  + File.separatorChar
				+ PropertyConfigurerUtils.getProperty("target_project_name",
				companyName.toLowerCase(),projectName.toLowerCase(),tableName == null ? "" : tableName.toLowerCase());
	}

	/**
	 * @param companyName
	 * @param projectName
	 * @param tableName
	 * @return 获取SQL文件在项目中存放的路径即名称
	 * @throws Exception
	 */
	public static String getGeneratorConfigFilePath(String companyName,
										String projectName,String tableName) throws Exception {

		companyName = StringUtils.isBlank(companyName)?"":""+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":""+(projectName);
		tableName = StringUtils.isBlank(tableName)?"":""+(tableName);

		String tmpDir = PropertyConfigurerUtils.containProperty(CreatorConst.GENERATOR_TMP_DIR)
				&& StringUtils.isNotBlank(PropertyConfigurerUtils.getProperty(CreatorConst.GENERATOR_TMP_DIR))?
				PropertyConfigurerUtils.getProperty(CreatorConst.GENERATOR_TMP_DIR) :
				CreatorUtil.class.getClassLoader().getResource("").getPath() + "/tmp";

		return tmpDir + File.separatorChar +
				PropertyConfigurerUtils.getProperty(CreatorConst.GENERATOR_CONFIG_PATH,
				companyName.toLowerCase(), projectName.toLowerCase(), tableName.toLowerCase());
	}




	/**
	 * @param companyName
	 * @param projectName
	 * @return 获取SQL文件在项目中存放的路径即名称
	 * @throws Exception
	 */
	public static String getSrcFilePath(String companyName,
										String projectName) throws Exception {

//		if(StringUtil.isBlank(companyName)) {
//			throw new Exception("公司名称为不能为空！");
//		}
		if("".equals(companyName) || companyName == null){
			companyName="zqh";
		}

		if("hframe".equals(projectName)) {
			projectName = "trunk";
		}


		if(StringUtils.isBlank(projectName)) {
			throw new Exception("项目名称为不能为空！");
		}

//		companyName.toLowerCase(),
//		projectName.toLowerCase(),
//		tableName.toLowerCase(),
//		getJavaClassName(tableName.toLowerCase())

//		PropertyConfigurerUtils.getProperty(CreatorConst.PROJECT_SRC_FILE_PATH+"." + projectName);
		String tmpDir = PropertyConfigurerUtils.containProperty(CreatorConst.TARGET_PROJECT_BASE_PATH)
				&& StringUtils.isNotBlank(PropertyConfigurerUtils.getProperty(CreatorConst.TARGET_PROJECT_BASE_PATH))?
				PropertyConfigurerUtils.getProperty(CreatorConst.TARGET_PROJECT_BASE_PATH) :
				CreatorUtil.class.getClassLoader().getResource("").getPath() + "/projects";

		return tmpDir  + File.separatorChar
				+ PropertyConfigurerUtils.getProperty("target_project_name",companyName, projectName)
				+ File.separatorChar
				+ PropertyConfigurerUtils.getProperty(CreatorConst.PROJECT_SRC_FILE_PATH, companyName, projectName);
	}

	/**
	 * @param companyName
	 * @param projectName
	 * @param tableName
	 * @return 获取SQL文件在项目中存放的路径即名称
	 * @throws Exception
	 */
	public static String getDAOClassPackage(String companyName,
											String projectName,String moduleName, String tableName) throws Exception {

//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		return PropertyConfigurerUtils.getProperty(
				CreatorConst.DAO_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""));
	}

	public static String getDAOImplClassPackage(String companyName,
												String projectName,String moduleName,String tableName) throws Exception {
//
//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);

		return PropertyConfigurerUtils.getProperty(
				CreatorConst.DAOIMPL_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""));
	}

	public static String getServiceClassPackage(String companyName,
												String projectName,String moduleName, String tableName) throws Exception {

//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);

		return PropertyConfigurerUtils.getProperty(
				CreatorConst.SERVICE_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""));
	}

	public static String getServiceImplClassPackage(String companyName,
													String projectName,String moduleName, String tableName) throws Exception {

//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);

		return PropertyConfigurerUtils.getProperty(
				CreatorConst.SERVICEIMPL_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""));
	}

	public static String getActionClassPackage(String companyName,
											   String projectName,String moduleName,String tableName) throws Exception {

//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);

		return PropertyConfigurerUtils.getProperty(
				CreatorConst.ACTION_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""));
	}

	/**
	 * @param companyName
	 * @param projectName
	 * @param tableName
	 * @return 获取SQL文件在项目中存放的路径即名称
	 * @throws Exception
	 */
	public static String getPoClassPackage(String companyName,
										   String projectName,String moduleName,String tableName) throws Exception {
//
//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
//		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);
		return PropertyConfigurerUtils.getProperty(
				CreatorConst.PO_CLASS_PACKAGE,
				companyName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				projectName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				moduleName.toLowerCase().replaceAll("(?<=(.))\\.",""),
				getJavaClassName(tableName.toLowerCase()));
	}



//	public static String getDAOClassPath(String companyName,
//										 String projectName,String tableName) throws Exception {
//
//		if(StringUtils.isBlank(tableName)) {
//			throw new Exception("表名称为不能为空！");
//		}
//
//		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
//		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
//
//		return PropertyConfigurerUtils.getProperty(
//				CreatorConst.DAO_CLASS_PACKAGE,
//				companyName.toLowerCase(),
//				projectName.toLowerCase(),
//				tableName.toLowerCase(),
//				getJavaClassName(tableName.toLowerCase()));
//	}

	public static Class getDefPoClass(String companyName,
									  String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getPoClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "");
		return class1;
	}


	public static Class getDefPoExampleClass(String companyName,
																String projectName,String moduleName, String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getPoClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "_Example");
		return class1;
	}

	public static Class getDefDaoClass(String companyName,
									   String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getDAOClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "DAO");
		return class1;
	}

	public static Class getDefMapperClass(String companyName,
									   String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}



		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getDAOClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "Mapper");
		return class1;
	}

	public static Class getDefDaoImplClass(String companyName,
										   String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getDAOImplClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "DAOImpl");
		return class1;
	}

	public static Class getDefServiceClass(String companyName,
										   String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}



		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getServiceClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName("I" + JavaUtil.getJavaClassName(tableName) + "SV");
		return class1;
	}

	public static Class getDefServiceImplClass(String companyName,
											   String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}

		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getServiceImplClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "SVImpl");
		return class1;
	}

	public static Class getDefActionClass(String companyName,
										  String projectName, String moduleName,String tableName) throws Exception {
		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}



		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getActionClassPackage(
				companyName, projectName, moduleName, tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "Action");
		return class1;
	}

	public static Class getDefControllerClass(String companyName,
										  String projectName, String moduleName,String tableName) throws Exception {

		if(StringUtils.isBlank(tableName)) {
			throw new Exception("表名称为不能为空！");
		}



		Class class1 = new Class();
		class1.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));

		companyName = StringUtils.isBlank(companyName)?"":"."+(companyName);
		projectName = StringUtils.isBlank(projectName)?"":"."+(projectName);
		moduleName = StringUtils.isBlank(moduleName)?"":"."+(moduleName);

		if(StringUtils.isNotBlank(moduleName) && moduleName.equals(projectName)) moduleName = "";
		if(StringUtils.isNotBlank(projectName) && projectName.equals(companyName)) projectName = "";

		class1.setClassPackage(CreatorUtil.getActionClassPackage(
				companyName, projectName, moduleName,tableName));
		class1.setClassName(JavaUtil.getJavaClassName(tableName) + "Controller");
		return class1;
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
