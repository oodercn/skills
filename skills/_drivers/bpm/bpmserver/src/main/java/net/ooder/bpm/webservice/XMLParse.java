package net.ooder.bpm.webservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 基于DOM的XML解析工具类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * <p>
 * Created on 2004-2-4
 * </p>
 * 
 * @author Lizhy
 * @version 1.0
 */
public class XMLParse {

    /**
     * 初始化一个空Document对象返回。
     * 
     * @return a Document
     */
    public static Document newXMLDocument() {
	try {
	    return newDocumentBuilder().newDocument();
	} catch (ParserConfigurationException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * 初始化一个DocumentBuilder
     * 
     * @return a DocumentBuilder
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
	return newDocumentBuilderFactory().newDocumentBuilder();
    }

    /**
     * 初始化一个DocumentBuilderFactory
     * 
     * @return a DocumentBuilderFactory
     */
    public static DocumentBuilderFactory newDocumentBuilderFactory() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	return dbf;
    }

    /**
     * 将传入的一个XML String转换成一个org.w3c.dom.Document对象返回。
     * 
     * @param xmlString
     *            一个符合XML规范的字符串表达。
     * @return a Document
     */
    public static Document parseXMLDocument(String xmlString) {
	if (xmlString == null) {
	    throw new IllegalArgumentException();
	}
	try {
	    return newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * 给定一个输入流，解析为一个org.w3c.dom.Document对象返回。
     * 
     * @param input
     * @return a org.w3c.dom.Document
     */
    public static Document parseXMLDocument(InputStream input) {
	if (input == null) {
	    throw new IllegalArgumentException("参数为null！");
	}
	try {
	    return newDocumentBuilder().parse(input);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * 给定一个文件名，获取该文件并解析为一个org.w3c.dom.Document对象返回。
     * 
     * @param fileName
     *            待解析文件的文件名
     * @return a org.w3c.dom.Document
     */
    public static Document loadXMLDocumentFromFile(String fileName) {
	if (fileName == null) {
	    throw new IllegalArgumentException("未指定文件名及其物理路径！");
	}
	try {
	    return newDocumentBuilder().parse(new File(fileName));
	} catch (SAXException e) {
	    throw new IllegalArgumentException("目标文件（" + fileName + "）不能被正确解析为XML！\n" + e.getMessage());
	} catch (IOException e) {
	    throw new IllegalArgumentException("不能获取目标文件（" + fileName + "）！\n" + e.getMessage());
	} catch (ParserConfigurationException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * 给定一个节点，将该节点加入新构造的Document中。
     * 
     * @param node
     *            a Document node
     * @return a new Document
     */
    public static Document newXMLDocument(Node node) {
	Document doc = newXMLDocument();
	doc.appendChild(doc.importNode(node, true));
	return doc;
    }

    /**
     * 将传入的一个DOM Node对象输出成字符串。如果失败则返回一个空字符串""。
     * 
     * @param node
     *            DOM Node 对象。
     * @return a XML String from node
     */
    public static String toString(Node node) {
	if (node == null) {
	    throw new IllegalArgumentException();
	}
	Transformer transformer = newTransformer();
	if (transformer != null) {
	    try {
		StringWriter sw = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(sw));
		return sw.toString();
	    } catch (TransformerException te) {
		throw new RuntimeException(te.getMessage());

	    }
	}
	return errXMLString("不能生成XML信息！");
    }

    /**
     * 获取一个Transformer对象，由于使用时都做相同的初始化，所以提取出来作为公共方法。
     * 
     * @return a Transformer encoding utf-8
     */
    public static Transformer newTransformer() {
	try {
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    Properties properties = transformer.getOutputProperties();
	    properties.setProperty(OutputKeys.ENCODING, "utf-8");
	    properties.setProperty(OutputKeys.METHOD, "xml");
	    properties.setProperty(OutputKeys.VERSION, "1.0");
	    properties.setProperty(OutputKeys.INDENT, "no");
	    transformer.setOutputProperties(properties);
	    return transformer;
	} catch (TransformerConfigurationException tce) {
	    throw new RuntimeException(tce.getMessage());
	}
    }

    /**
     * 返回一段XML表述的错误信息。提示信息的TITLE为：系统错误。之所以使用字符串拼装，主要是这样做一般 不会有异常出现。
     * 
     * @param errMsg
     *            提示错误信息
     * @return a XML String show err msg
     */
    public static String errXMLString(String errMsg) {
	StringBuffer msg = new StringBuffer(100);
	msg.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
	msg.append("<errNode title=\"系统错误\" errMsg=\"" + errMsg + "\"/>");
	return msg.toString();
    }

    /**
     * 返回一段XML表述的错误信息。提示信息的TITLE为：系统错误
     * 
     * @param errMsg
     *            提示错误信息
     * @param errClass
     *            抛出该错误的类，用于提取错误来源信息。
     * @return a XML String show err msg
     */
    public static String errXMLString(String errMsg, Class errClass) {
	StringBuffer msg = new StringBuffer(100);
	msg.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
	msg.append("<errNode title=\"系统错误\" errMsg=\"" + errMsg + "\" errSource=\"" + errClass.getName() + "\"/>");
	return msg.toString();
    }

    /**
     * 返回一段XML表述的错误信息。
     * 
     * @param title
     *            提示的title
     * @param errMsg
     *            提示错误信息
     * @param errClass
     *            抛出该错误的类，用于提取错误来源信息。
     * @return a XML String show err msg
     */
    public static String errXMLString(String title, String errMsg, Class errClass) {
	StringBuffer msg = new StringBuffer(100);
	msg.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
	msg.append("<errNode title=\"" + title + "\" errMsg=\"" + errMsg + "\" errSource=\"" + errClass.getName() + "\"/>");
	return msg.toString();
    }

    /**
     * 获得传入元素节点的值
     * 
     * @param node
     *            DOM Node 对象。
     * @return a String
     */
    public static String getNodeValue(Node node) {
	String nodeValue = "";
	if (node.getFirstChild() != null) {
	    nodeValue = node.getFirstChild().getNodeValue();
	}
	return nodeValue;
    }

    /**
     * 获得传入元素节点的指定属性值
     * 
     * @param node
     *            DOM Node 对象。
     * @param attrName
     *            Attribute Name
     * @return a String
     */
    public static String getAttributeValue(Node node, String attrName) {
	String attrValue = "";
	if (node.hasAttributes()) {
	    NamedNodeMap nodeValue = node.getAttributes();
	    Node attrNode = nodeValue.getNamedItem(attrName);
	    if (attrNode != null) {
		attrValue = getNodeValue(attrNode);
	    }
	}
	return attrValue;
    }

    /**
     * 通过父元素以及子元素名称获得一组子元素集合
     * 
     * @param parent
     *            DOM Element 对象
     * @param childName
     *            元素的Tag标签名
     * @return a NodeList
     */
    public static List<Node> getChildNodesByName(Node parent, String childName) {
	List<Node> childNodeList = new ArrayList<Node>();
	if (parent.hasChildNodes()) {
	    int count = parent.getChildNodes().getLength();
	    for (int i = 0; i < count; i++) {
		String nodeName = parent.getChildNodes().item(i).getNodeName();
		if (nodeName.equals(childName)) {
		    childNodeList.add(parent.getChildNodes().item(i));
		}
	    }
	}
	return childNodeList;
    }

    /**
     * 为一个元素节点创建属性
     * 
     * @param element
     *            DOM Element 对象
     * @param attrName
     *            属性名称
     * @param attrValue
     *            属性值
     * @return a Attr
     */
    public static Attr createAttributeForElement(Element element, String attrName, String attrValue) {
	Attr attrNode = element.getOwnerDocument().createAttribute(attrName);
	attrNode.setValue(attrValue);
	return attrNode;
    }

    /**
     * 为一个元素节点创建子元素
     * 
     * @param parentElement
     *            DOM Element 对象
     * @param childName
     *            子元素名称
     * @return a Element
     */
    public static Element createChildElement(Element parentElement, String childName, String childValue) {
	Document doc = parentElement.getOwnerDocument();
	Element childElement = doc.createElement(childName);
	if (childValue != null && !childValue.equals("")) {
	    Node textNode = doc.createTextNode(childValue);
	    childElement.appendChild(textNode);
	}
	return childElement;
    }

}

