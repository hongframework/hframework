package com.hframework.generator.web.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hframework.beans.class0.Constructor;
import com.hframework.beans.class0.Table;
import com.hframework.beans.class0.Class;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.generator.web.container.CreatorContainer;
import com.hframework.generator.web.container.CreatorContainerBuilder;


/**
 *
 * @author zqh
 *
 */
@Deprecated
public class DAOCreator {

	public static String createDAOFile(String companyName, String projectName,
									   List<Table> tableList) throws Exception {

		for (Table table : tableList) {

			CreatorContainer container = CreatorContainerBuilder.getCreatorContainer(
					companyName, projectName, null, table.getTableName());
			Class daoClass = container.Dao;

			String content=getDAOContent(container, table);
			FileUtils.writeFile(daoClass.getFilePath(), content);
		}
		return null;
	}


	private static String getDAOContent(CreatorContainer container, Table table) {

		Map  map=new HashMap();
		map.put("CLASS", container.Dao);

		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/dao.vm", map);
		return resultStr;
	}


	public static String createDAOImplFile(String companyName, String projectName,
										   List<Table> tableList) throws Exception {

		for (Table table : tableList) {

			CreatorContainer container = CreatorContainerBuilder.getCreatorContainer(
					companyName, projectName,null, table.getTableName());
			Class daoImplClass = container.DaoImpl;
			Class daoClass = container.Dao;
			Class poClass = container.Po;

			String content=getDAOImplContent(daoImplClass,poClass,daoClass , table);
			FileUtils.writeFile(daoImplClass.getFilePath(), content);
		}

		return null;
	}

	private static String getDAOImplContent(Class daoImplClass,
											Class poClass, Class daoClass, Table table) {

		StringBuffer sb=new StringBuffer();

		daoImplClass.addImportClass("org.springframework.stereotype.Component;");
		daoImplClass.addImportClass("com.hframework.common.ssh.dao.AbstractDAOSupport;");
		daoImplClass.addImportClass(poClass.getClassPath());
		daoImplClass.addImportClass(daoClass.getClassPath());

		daoImplClass.setAnnotation("@Component");
		daoImplClass.setSuperClass("AbstractDAOSupport");

		daoImplClass.addInterface(daoClass.getClassPath());

		Constructor constructor = daoImplClass.addConstructor();
		constructor.addCodeLn("super("+poClass.getClassName()+".class);");

		Map  map=new HashMap();
		map.put("CLASS", daoImplClass);

		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/daoImpl.vm", map);

		return resultStr;
	}
}
