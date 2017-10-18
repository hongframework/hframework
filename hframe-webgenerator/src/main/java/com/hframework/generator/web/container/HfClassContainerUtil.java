package com.hframework.generator.web.container;

import com.google.common.collect.Lists;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.generator.enums.HfmdEntityAttr1AttrTypeEnum;
import com.hframework.generator.web.container.bean.Entity;
import com.hframework.generator.web.container.bean.EntityAttr;
import com.hframework.generator.web.container.bean.Module;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 模型数据容器
 */
public class HfClassContainerUtil {

	public static HfClassContainer fromClassPath(String classRootPath, List<String> classPackages,String programCode,
												 String programName) throws MalformedURLException, ClassNotFoundException {
		HfClassContainer hfClassContainer = HfClassContainer.getInstance(programCode, programName);

		URLClassLoader loader = new URLClassLoader(
				new URL[] { new URL("file:" + classRootPath) },Thread.currentThread().getContextClassLoader());

		for (String classPackage : classPackages) {
			File[] fileList = FileUtils.getFileList(new File(classRootPath + classPackage.replace(".", "/")));
			if(fileList == null) continue;
			for (File file : fileList) {
				if(!file.getName().contains("_Example")) {
					String className = file.getName().substring(0,file.getName().length()-6);

					Class<?> clazz = loader.loadClass(classPackage + className);
					HfClassContainer.HfClassDescriptor classInfo = hfClassContainer.getClassInfo(clazz.getSimpleName());

					Field[] declaredFields = clazz.getDeclaredFields();
					for (Field field : declaredFields) {
						classInfo.addField(null, field.getName(), field.getType(), null);
					}
				}
			}
		}
		return hfClassContainer;
	}

	public static HfClassContainer fromClassPath(String classRootPath, String classPackage,String programCode,
												 String programName) throws MalformedURLException, ClassNotFoundException {
		return fromClassPath(classRootPath, Lists.newArrayList(classPackage), programCode, programName);
	}

	public static HfClassContainer getClassInfoContainer(HfModelContainer container) {
		HfClassContainer hfClassContainer = HfClassContainer.getInstance(
				container.getProgram().getHfpmProgramCode(), container.getProgram().getHfpmProgramName());

		Collection<Entity> hfmdEntitys = container.getEntityMap().values();
		for (Entity hfmdEntity : hfmdEntitys) {
			String hfmdEntityCode = hfmdEntity.getHfmdEntityCode();
			String className = JavaUtil.getJavaClassName(hfmdEntityCode);

			HfClassContainer.HfClassDescriptor classInfo = hfClassContainer.getClassInfo(className);
			classInfo.setEntityCode(hfmdEntityCode);
			classInfo.setClassDesc(hfmdEntity.getHfmdEntityName());

			Module module = container.getModuleMap().get(hfmdEntity.getHfpmModuleId());
			if(module != null) classInfo.setModuleCode(module.getHfpmModuleCode());
		}

		Map<String, EntityAttr> entityAttrMap = container.getEntityAttrMap();
		for (String key : entityAttrMap.keySet()) {
			String entityCode = key.substring(0, key.indexOf("."));
			String className = JavaUtil.getJavaClassName(entityCode);
			HfClassContainer.HfClassDescriptor classInfo = hfClassContainer.getClassInfo(className);
			EntityAttr entityAttr = entityAttrMap.get(key);
			classInfo.addField(entityAttr.getHfmdEntityAttrCode(),
					JavaUtil.getJavaVarName(entityAttr.getHfmdEntityAttrCode()),
					HfmdEntityAttr1AttrTypeEnum.getJavaTypeClass(entityAttr.getAttrType()),
					entityAttr.getHfmdEntityAttrDesc());
		}

		return hfClassContainer;
	}


	private static List<Map<String, String>> compare(Set<String> classNameSet, HfClassContainer targetContainer) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (String className : classNameSet) {
			HfClassContainer.HfClassDescriptor classDescriptor = targetContainer.getClasss().get(className);
			Map<String, HfClassContainer.HfClassFieldDescriptor> fields = classDescriptor.getFields();
			for (HfClassContainer.HfClassFieldDescriptor fieldDescriptor : fields.values()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("fieldName", fieldDescriptor.getFieldName());
				map.put("fieldType", fieldDescriptor.getFieldType().toString());
				map.put("fieldDesc", fieldDescriptor.getFieldDesc());
				map.put("entityAttrCode", fieldDescriptor.getEntityAttrCode());
				map.put("className",classDescriptor.getClassName());
				map.put("classDesc",classDescriptor.getClassDesc());
				map.put("entityCode",classDescriptor.getEntityCode());
				map.put("originFieldType","-");
				result.add(map);
			}
		}

		return result;
	}

	private static List<Map<String, String>> compareClass(Set<String> filedNameSet, HfClassContainer.HfClassDescriptor classDescriptor) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (String fieldName : filedNameSet) {
			HfClassContainer.HfClassFieldDescriptor fieldDescriptor = classDescriptor.getFields().get(fieldName);
			Map<String, String> map = new HashMap<String, String>();
			map.put("fieldName", fieldDescriptor.getFieldName());
			map.put("fieldType", fieldDescriptor.getFieldType().toString());
			map.put("fieldDesc", fieldDescriptor.getFieldDesc());
			map.put("entityAttrCode", fieldDescriptor.getEntityAttrCode());
			map.put("className",classDescriptor.getClassName());
			map.put("classDesc",classDescriptor.getClassDesc());
			map.put("entityCode",classDescriptor.getEntityCode());
			result.add(map);
		}

		return result;
	}

	private static List<Map<String, String>> compareClass(Set<String> filedNameSet, HfClassContainer.HfClassDescriptor originClassDescriptor, HfClassContainer.HfClassDescriptor targetClassDescriptor) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (String fieldName : filedNameSet) {
			HfClassContainer.HfClassFieldDescriptor fieldDescriptor = targetClassDescriptor.getFields().get(fieldName);
			HfClassContainer.HfClassFieldDescriptor originFieldDescriptor = originClassDescriptor.getFields().get(fieldName);
			System.out.println("fieldName=" + fieldName + ";fieldDescriptor=" + fieldDescriptor + ";originFieldDescriptor=" + originFieldDescriptor);
			if(fieldDescriptor.getFieldType() != originFieldDescriptor.getFieldType()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("fieldName", fieldDescriptor.getFieldName());
				map.put("fieldType", fieldDescriptor.getFieldType().toString());
				map.put("fieldDesc", StringUtils.isNotBlank(fieldDescriptor.getFieldDesc()) ? fieldDescriptor.getFieldDesc() : originFieldDescriptor.getFieldDesc());
				map.put("entityAttrCode", StringUtils.isNotBlank(fieldDescriptor.getEntityAttrCode()) ? fieldDescriptor.getEntityAttrCode(): originFieldDescriptor.getEntityAttrCode());
				map.put("className", targetClassDescriptor.getClassName());
				map.put("classDesc", StringUtils.isNotBlank(targetClassDescriptor.getClassDesc()) ? targetClassDescriptor.getClassDesc() : originClassDescriptor.getClassDesc());
				map.put("entityCode",StringUtils.isNotBlank(targetClassDescriptor.getEntityCode()) ? targetClassDescriptor.getEntityCode() : originClassDescriptor.getEntityCode());
				map.put("originFieldType", originFieldDescriptor.getFieldType().toString());
				result.add(map);
			}
		}
		return result;
	}

	private static List<Map<String, String>> compare(Set<String> classNameSet, HfClassContainer originContainer, HfClassContainer targetContainer, int type) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (String className : classNameSet) {
			HfClassContainer.HfClassDescriptor targetClassDescriptor = targetContainer.getClasss().get(className);
			HfClassContainer.HfClassDescriptor originClassDescriptor = originContainer.getClasss().get(className);

			Set<String> targetFieldNames = targetClassDescriptor.getFields().keySet();
			Set<String> originFieldNames = originClassDescriptor.getFields().keySet();;

			if(type == 1) {
				Set<String> addFieldNameSet = new HashSet<String>();
				addFieldNameSet.addAll(targetFieldNames);
				addFieldNameSet.removeAll(originFieldNames);
				List<Map<String, String>> addResult = compareClass(addFieldNameSet, targetClassDescriptor);
				result.addAll(addResult);
			}

			if(type == 3) {
				Set<String> delFieldNameSet = new HashSet<String>();
				delFieldNameSet.addAll(originFieldNames);
				delFieldNameSet.removeAll(targetFieldNames);
				List<Map<String, String>> delResult = compareClass(delFieldNameSet, originClassDescriptor);
				result.addAll(delResult);
			}

			if(type == 2) {
				Set<String> modFieldNameSet = new HashSet<String>();
				modFieldNameSet.addAll(originFieldNames);
				modFieldNameSet.retainAll(targetFieldNames);

				List<Map<String, String>> modResult = compareClass(modFieldNameSet, originClassDescriptor, targetClassDescriptor);
				result.addAll(modResult);
			}
		}
		return result;
	}

	public static List<Map<String, String>>[] compare(HfClassContainer originContainer, HfClassContainer targetContainer) {


		Set<String> originClassNameSet = originContainer.getClasss().keySet();
		Set<String> targetClassNameSet = targetContainer.getClasss().keySet();
		if("hframe".equals(targetContainer.getProgramCode())) {
			Iterator<String> originIterator = originClassNameSet.iterator();
			while (originIterator.hasNext()) {
				if(!originIterator.next().startsWith("Hf")){
					originIterator.remove();
				}
			}
			Iterator<String> targetIterator = targetClassNameSet.iterator();
			while (targetIterator.hasNext()) {
				if(!targetIterator.next().startsWith("Hf")){
					targetIterator.remove();
				}
			}
		}


		Set<String> addClassNameSet = new HashSet<String>();
		addClassNameSet.addAll(targetClassNameSet);
		addClassNameSet.removeAll(originClassNameSet);
		List<Map<String, String>> addResult = compare(addClassNameSet, targetContainer);


		Set<String> delClassNameSet = new HashSet<String>();
		delClassNameSet.addAll(originClassNameSet);
		delClassNameSet.removeAll(targetClassNameSet);
		List<Map<String, String>> delResult = compare(delClassNameSet, originContainer);

		Set<String> modClassNameSet = new HashSet<String>();
		modClassNameSet.addAll(originClassNameSet);
		modClassNameSet.retainAll(targetClassNameSet);
		List<Map<String, String>> modResult = compare(modClassNameSet, originContainer, targetContainer, 2);
		addResult.addAll(compare(modClassNameSet, originContainer, targetContainer, 1));
		delResult.addAll(compare(modClassNameSet, originContainer, targetContainer, 3));




		return new List[]{addResult,modResult,delResult};
	}
}
