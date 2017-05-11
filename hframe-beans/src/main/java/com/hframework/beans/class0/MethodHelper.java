package com.hframework.beans.class0;

public class MethodHelper {

	public static Method getGetMethod(Field field) {
		Method method = new Method();
		method.setName("get" + field.getUcName());
		method.setReturnType(field.getType());
		method.addCodeLn("return " + field.getName() + ";");
		return method;
	}

	public static Method getSetMethod(Field field) {
		Method method = new Method();
		method.setName("set" + field.getUcName());
		method.addParameter(field);
		method.addCodeLn("this." + field.getName() + " = " + field.getName() + ";");
		return method;
	}
}
