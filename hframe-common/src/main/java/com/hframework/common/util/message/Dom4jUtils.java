package com.hframework.common.util.message;

import com.hframework.common.util.file.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

public class Dom4jUtils {
	public static Document getDocument(String fileName){
		
		SAXReader reader=new SAXReader();
		Document doc = null;
		
		try {
			doc=reader.read(new File(fileName));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return doc;
	}

	public static Document getDocumentByContent(String content){
		try {
			return DocumentHelper.parseText(content);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void writeToFile(Document doc,String fileName){
		FileUtils.newFile(fileName);
		try {
			Writer writer=new FileWriter(new File(fileName));
			
			OutputFormat format=OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			
			XMLWriter xmlWriter=new XMLWriter(writer, format);
			
			xmlWriter.write(doc);
			xmlWriter.flush();
			xmlWriter.close();
			
			System.out.println("�ļ����£�");
			
			} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DOMElement createElement(String name,Map<String, String> attrMap){
		
		DOMElement element=new DOMElement(name);
		
		Set<String> keyset=attrMap.keySet();
		
		for (String key : keyset) {
			element.setAttribute(key, attrMap.get(key));
		}
		
		return element;
	}
	
}
