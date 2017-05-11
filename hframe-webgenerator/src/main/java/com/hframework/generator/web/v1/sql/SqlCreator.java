package com.hframework.generator.web.v1.sql;

import java.util.List;

import com.hframework.common.util.file.FileUtils;
import com.hframework.beans.class0.Column;
import com.hframework.beans.class0.Table;
import com.hframework.generator.util.CreatorUtil;


/**
 *
 * @author zqh
 *
 */
public class SqlCreator {


	/**
	 * 通过拥有者，数据库名称，所有表，生成的sql
	 * @param username
	 * @param tableList
	 * @return
	 * @throws Exception
	 */
	public static String createSqlFile(String username,
									   String dbName, List<Table> tableList) throws Exception{


		//如果没有拥有者，默认为zqh用户
		if("".equals(username)){
			username="zqh";
		}

		//每张表生成一个sql文件,通过getSqlContent(table)获取文件内容
		for (Table table : tableList) {
			String sqlFileName = CreatorUtil.getSQLFilePath(
					username, dbName, table.getTableName());
			String content = getSqlContent(table);
			FileUtils.writeFile(sqlFileName, content);
		}
		return null;
	}


	private static String getSqlContent(Table table) {

		StringBuffer sb=new StringBuffer();

		sb.append("create table "+ table.getTableName().toLowerCase()+" (");
		sb.append("\n");
		List<Column> columnList= table.getColumnList();
		for (Column Column : columnList) {
			sb.append("  "+Column.getColumnName().toLowerCase());

			if("varchar".equals(Column.getColumnType().toLowerCase())||"char".equals(Column.getColumnType().toLowerCase())){

				sb.append("  "+Column.getColumnType().toLowerCase()+"("+Column.getColumnSize()+")");
			}else{
				sb.append("  "+Column.getColumnType().toLowerCase());
			}

			if(0==Column.getNullable()){
				sb.append(" not null ");
			}
			if(1==Column.getIspk()){
				sb.append(" primary key");
			}
			sb.append(",\n");
		}

		return sb.toString().substring(0,sb.toString().length()-2)+" \n);\n";
	}
}
