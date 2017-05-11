package com.hframework.generator.util;


public class TransforUtil {

//	/**
//	 * 该方法暂弃
//	 * @param coreFieldList
//	 * @param fieldList
//	 */
//	public static  void transforCoreFieldToField(List<CoreField>  coreFieldList,List<Field> fieldList){
//		for (CoreField coreField : coreFieldList) {
//			fieldList.add(new Field(coreField.getId(),coreField.getTitle(),coreField.getShowExp(),coreField.getShowExp(),coreField.getHiddenExp()));
//		}
//
//	}
//
//	public static  List<Field> getFieldListFromCoreFieldList(List<CoreField>  coreFieldList){
//		List<Field> fieldList=new ArrayList<Field>();
//
//		for (CoreField coreField : coreFieldList) {
//			fieldList.add(new Field(coreField.getId(),coreField.getTitle(),coreField.getType(),coreField.getShowExp(),coreField.getHiddenExp()));
//		}
//		return fieldList;
//	}
//
//	/**
//	 * 该方法暂弃
//	 *
//	 */
//	public static void transforCoreColumnToColumn(
//			List<com.hframe.po.Column> coreColumnList, List<Column> columnList) {
//
//		for (com.hframe.po.Column coreColumn : coreColumnList) {
//			Column column=getColumnFromCoreColumn(coreColumn);
//			columnList.add(column);
//		}
//	}
//
//	public static List<Column> getColumnListFromCoreColumnList(List<com.hframe.po.Column> coreColumnList){
//
//		List<Column> columnList=new ArrayList<Column>();
//
//		for (com.hframe.po.Column coreColumn : coreColumnList) {
//			Column column=getColumnFromCoreColumn(coreColumn);
//			columnList.add(column);
//		}
//		return columnList;
//
//	}
//
//	public static Column getColumnFromCoreColumn(com.hframe.po.Column coreColumn) {
//		Column column=new Column();
//		column.setId(coreColumn.getId());
//		column.setName(coreColumn.getColumnName());
//		column.setJavaVarName(JavaUtil.getJavaVarName(coreColumn.getColumnName()));
//		column.setDisplayName(coreColumn.getShowName());
//		column.setNullable(coreColumn.getNullable());
//		///column.setDefaultValue(coreColumn.get)  //TODO
//
//		//设置showType----------------------------一个column可以配置多个showType
//
//		CoreShowType coreShowType=coreColumn.getCoreShowType();
//		List<CoreShowType> coreShowTypeList=coreColumn.getCoreShowTypeList();
//
//		//如果column没有配置showType 则返回
//		if(coreShowType==null&&(coreShowTypeList==null||coreShowTypeList.size()==0)){
//
//			if(coreColumn.getShowable()==0){
//				column.setShowType(new ShowType("hidden"));
//			}else{
//				column.setShowType(new ShowType("input"));
//			}
//			return column;
//		}
//		//如果配置了一个ShowType
//		if(coreShowType!=null){
//			ShowType showType=getShowTypeFromCoreShowType(coreShowType);
//			if(coreColumn.getShowable()==0){
//				column.setShowType(new ShowType("hidden"));
//			}else{
//				column.setShowType(showType);
//			}
//		}
//		//如果配置了多个ShowType
//		if(coreShowTypeList!=null&&coreShowTypeList.size()>0){
//			List<ShowType> showTypeList=new ArrayList<ShowType>();
//			for (CoreShowType cst : coreShowTypeList) {
//				ShowType showType=getShowTypeFromCoreShowType(cst);
//				showTypeList.add(showType);
//			}
//			column.setShowTypes(showTypeList);
//
//		}
//		return column;
//	}
////
////	public static ShowType getShowTypeFromCoreShowType(
////			CoreShowType coreShowType) {
////
////		if("radio".equals(coreShowType.getType())){
////			System.out.println();
////		}
////
////		ShowType showType=new ShowType(coreShowType.getType());
////		showType.setAfterStr(coreShowType.getAfterStr());
////		showType.setColSpan(coreShowType.getColSpan());
////		showType.setId(Integer.parseInt(coreShowType.getId()));
////		showType.setPreStr(coreShowType.getPreStr());
////		showType.setElementId(coreShowType.getCoreElementId());//为tipinput,openwin使用
////		if(coreShowType.getWidth()!=null){
////			showType.setWidth(coreShowType.getWidth());
////		}
////		if(coreShowType.getHeight()!=null){
////			showType.setHeight(coreShowType.getHeight());
////		}
////
////		if(coreShowType.getCoreShowTypeAttr()!=null){
////			CoreShowTypeAttr coreShowTypeAttr = coreShowType.getCoreShowTypeAttr();
////			ShowTypeAttr showTypeAttr=new ShowTypeAttr();
////			showTypeAttr.setCondition(coreShowTypeAttr.getCondition());
////			showTypeAttr.setTitle(coreShowTypeAttr.getTitle());
////			showTypeAttr.setType(coreShowTypeAttr.getType());
////			showTypeAttr.setView(coreShowTypeAttr.getView());
////			showTypeAttr.setSrc(coreShowTypeAttr.getSrc()==null||"".equals(coreShowTypeAttr.getSrc())?"myDialog.jsp":coreShowTypeAttr.getSrc());
////			showType.setShowTypeAttr(showTypeAttr);
////		}
////
////		//设置showType中enum枚举值-------------------一个showType可以有optionList也可以没有optionList
////		List optionList=getOptionsFromCoreShowType(coreShowType);
////		showType.setOptionList(optionList);
////		showType.setCoreEnumDyn(getCoreEnumDynFromCoreShowType(coreShowType));
////		return showType;
////	}
////
////	public static List getOptionsFromCoreShowType(CoreShowType coreShowType) {
////		List optionList=new ArrayList();
////		CoreEnumGroup coreEnumGroup=coreShowType.getCoreEnumGroup();
////		if(coreEnumGroup==null)
////			return null;
////
////		List<CoreEnum> coreEnumList=coreEnumGroup.getCoreEnumList();
////		for (CoreEnum coreEnum : coreEnumList) {
////			optionList.add(new Option(coreEnum.getCoreEnumValue(),coreEnum.getCoreEnumDisplayValue()));
////		}
////		return optionList;
////	}
////	public static CoreEnumDyn getCoreEnumDynFromCoreShowType(CoreShowType coreShowType) {
////		List optionList=new ArrayList();
////		CoreEnumGroup coreEnumGroup=coreShowType.getCoreEnumGroup();
////		if(coreEnumGroup==null)
////			return null;
////
////
////		return coreEnumGroup.getCoreEnumDyn();
////	}



}
