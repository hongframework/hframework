package com.hframework.generator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class ReportUtils {

//	private static ReportUtils reportUtils = new ReportUtils();
//
//	private static long lastModifyTime = 0l;
//
//	private final static String reportMappingSqlFile = "com/hframe/creator/template/template.xml";
//
//	private static Map<String, ReportMappingSql> catchReportMappingSql = null;
//
//	private ReportUtils() {
//	}
//
//	public static ReportUtils getInstance() {
//		return reportUtils;
//	}
//
//	public ReportMappingSql getMappingSql(String mappingSqlKey) {
//
//		//这里dom读文件的时候，如果路径中包含空格，那么class.getResource找到路径后就成为了%20，所以我们需要转换回去，不然会报文件找不到错误
//		File reportFile = new File(ReportUtils.class.getResource(reportMappingSqlFile).getPath().replace("%20", " "));
//		System.out.println("what happened here!");
//		if (null == catchReportMappingSql) {
//			catchReportMappingSql = new HashMap<String, ReportMappingSql>();
//			loadReportProperties(catchReportMappingSql, reportFile);
//			lastModifyTime = reportFile.lastModified();
//		} else if (reportFile.lastModified() > lastModifyTime) {
//			loadReportProperties(catchReportMappingSql, reportFile);
//			lastModifyTime = reportFile.lastModified();
//		}
//		ReportMappingSql rms = catchReportMappingSql.get(mappingSqlKey);
//		if (null != rms)
//		{
//			ReportMappingSql nrms = new ReportMappingSql();
//			nrms.setSqlText(rms.getSqlText());
//			for (int i = 0; null != rms.getSqlParams() && i < rms.getSqlParams().size(); i++)
//			{
//				ReportSqlParams rsp = rms.getSqlParams().get(i);
//				ReportSqlParams nrsp = new ReportSqlParams();
//				nrsp.setParamAlias(rsp.getParamAlias());
//				nrsp.setParamIndex(rsp.getParamIndex());
//				nrsp.setParamList(rsp.isParamList());
//				nrsp.setParamReplace(rsp.getParamReplace());
//				nrsp.setParamType(rsp.getParamType());
//				nrsp.setParamValue(rsp.getParamValue());
//				nrms.addSqlParams(nrsp);
//			}
//			return nrms;
//		}
//		return null;
//	}
//
//	public void loadReportProperties(Map<String, ReportMappingSql> catchReportMappingSql, File reportMappingSqlFile) {
//		catchReportMappingSql.clear();
//		SAXReader saxReader = new SAXReader(false);
//		try {
//			Document document = saxReader.read(reportMappingSqlFile);
//			Element root = document.getRootElement();
//			Iterator<Element> elements = root.elementIterator();
//			while (elements.hasNext()) {
//				Element element = elements.next();
//				String sqlkey = element.attributeValue("key");
//				Element sqltext = element.element("methodText");
//				Iterator<Element> sqlparams = element.elementIterator("methodParam");
//
//				ReportMappingSql ms = new ReportMappingSql();
//
//
//				ms.setSqlText(sqltext.getText());
//				while (sqlparams.hasNext()) {
//					Element sqlparam = sqlparams.next();
//					ReportSqlParams rsparam = new ReportSqlParams();
//					String pl = sqlparam.attributeValue("isParamList");
//
//					if(pl==null&&"".equals(pl)){
//						rsparam.setParamList(Boolean.parseBoolean(pl));
//					}
//					rsparam.setParamAlias(sqlparam.attributeValue("paramAlias"));
//					rsparam.setParamIndex(sqlparam.attributeValue("paramIndex"));
//					rsparam.setParamReplace(sqlparam.attributeValue("paramReplace"));
//					String pt = sqlparam.attributeValue("paramType");
//					if(pl==null&&"".equals(pl)){
//						try {
//							rsparam.setParamType((Type)ReportUtils.class.getClassLoader().loadClass(pt.trim()).newInstance());
//						} catch (InstantiationException e) {
//							e.printStackTrace();
//						} catch (IllegalAccessException e) {
//							e.printStackTrace();
//						} catch (ClassNotFoundException e) {
//							e.printStackTrace();
//						}
//					}
//					ms.addSqlParams(rsparam);
//				}
//				catchReportMappingSql.put(sqlkey, ms);
//			}
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static String replaceMappingSql(ReportMappingSql mappSql,Map<String, String> inputParams){
//
//		//System.out.println("ReportUtils.getInstance().getMappingSql(\"findByParam\")---"+ReportUtils.getInstance().getMappingSql("findByParam"));
//
////		List<ReportSqlParams> sqlparams = ReportUtils.getInstance().getMappingSql("findByParamCascade").getSqlParams();
//		List<ReportSqlParams> sqlparams = mappSql.getSqlParams();
//		List<ReportSqlParams> delparams = new ArrayList<ReportSqlParams>();
//		for (int i = 0; i < sqlparams.size(); i++)
//		{
//			ReportSqlParams sqlParams = sqlparams.get(i);
//			String parmValue = inputParams.get(sqlParams.getParamAlias());
//			System.out.println(sqlParams.getParamAlias()+"___"+parmValue);
//
//			if (null!=parmValue&&!"".equals(parmValue))
//			{
//				if (sqlParams.getParamType() instanceof DoubleType)
//				{
//					sqlParams.setParamValue(Double.parseDouble(parmValue));
//				} else if (sqlParams.getParamType() instanceof IntegerType)
//				{
//					sqlParams.setParamValue(Integer.parseInt(parmValue));
//				} else if (sqlParams.getParamType() instanceof DateType)
//				{
//				} else
//				{
//					sqlParams.setParamValue(parmValue);
//				}
//				mappSql.setSqlText(mappSql.getSqlText().replace(sqlParams.getParamIndex(), parmValue));
//			} else
//			{
//				mappSql.setSqlText(mappSql.getSqlText().replace(sqlParams.getParamIndex(), ""));
//				delparams.add(sqlParams);
//			}
//		}
//
//		sqlparams.removeAll(delparams);
//
//
//		return mappSql.getSqlText();
//
//	}
//
//
//	public static void main(String[] args) {
//
//		ReportMappingSql mappSql=ReportUtils.getInstance().getMappingSql("findByParamCascade");
//
//		Map<String, String> inputParam=new HashMap<String, String>();
//
//		inputParam.put("ClassName", "SysUser");
//		inputParam.put("VarName", "sysUser");
//		inputParam.put("HQL1", " from fdsfdsfdsafdsafdsa");
//
//
//		System.out.println(replaceMappingSql(mappSql,inputParam));
//
//	}
//	class ReportMappingSql {
//
//		private String sqlText;
//
//		private List<ReportSqlParams> sqlParams;
//
//		public String getSqlText() {
//			return sqlText;
//		}
//
//		public void setSqlText(String sqlText) {
//			this.sqlText = sqlText;
//		}
//
//		public List<ReportSqlParams> getSqlParams() {
//			if (null == sqlParams) {
//				sqlParams = new ArrayList<ReportSqlParams>();
//			}
//			return sqlParams;
//		}
//
//		public void addSqlParams(ReportSqlParams sqlParam) {
//			if (null == sqlParams) {
//				sqlParams = new ArrayList<ReportSqlParams>();
//			}
//			this.sqlParams.add(sqlParam);
//		}
//
//	}
//
//	class ReportSqlParams {
//
//		private Type paramType;
//
//		private String paramIndex;
//
//		private String paramAlias;
//
//		private String paramReplace;
//
//		private boolean isParamList;
//
//		private Object paramValue;
//
//		public Type getParamType() {
//			return paramType;
//		}
//
//		public void setParamType(Type paramType) {
//			this.paramType = paramType;
//		}
//
//		public String getParamAlias() {
//			return paramAlias;
//		}
//
//		public void setParamAlias(String paramAlias) {
//			this.paramAlias = paramAlias;
//		}
//
//		public boolean isParamList() {
//			return isParamList;
//		}
//
//		public void setParamList(boolean isParamList) {
//			this.isParamList = isParamList;
//		}
//
//		public Object getParamValue() {
//			return paramValue;
//		}
//
//		public void setParamValue(Object paramValue) {
//			this.paramValue = paramValue;
//		}
//
//		public String getParamReplace() {
//			return paramReplace;
//		}
//
//		public void setParamReplace(String paramReplace) {
//			this.paramReplace = paramReplace;
//		}
//
//		public String getParamIndex() {
//			return paramIndex;
//		}
//
//		public void setParamIndex(String paramIndex) {
//			this.paramIndex = paramIndex;
//		}
//
//	}

}
