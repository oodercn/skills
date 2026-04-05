package net.ooder.bpm.graph;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-7-17
 * Time: 14:18:09
 * To change this template use File | Settings | File Templates.
 */
public class XmlBuilder {

  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  //com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl

  private DocumentBuilder builder;

  private Document doc;


  private DocumentBuilder getBuilder() throws ParserConfigurationException {
    if (builder == null) {
      builder = factory.newDocumentBuilder();
    }
    return builder;
  }


  public Document newDocument() throws Exception {
    Document d = getBuilder().newDocument();
    return d;
  }

  public static void writeXml(OutputStream os, Document doc,
                              String encoding) throws IOException {
    Properties outputProp = null;
//    Writer w = null;
    try {
      if (encoding != null && !encoding.equals("")) {
        outputProp = new Properties();
        outputProp.put(OutputKeys.ENCODING, encoding);
      }
      StreamResult sr = new StreamResult(os);
      writeStreamResult(sr, doc, outputProp);
    } catch (TransformerException e) {
      IOException ioe = new IOException();
      ioe.initCause(e);
      throw ioe;
    }
  }

  public static void writeXml(Writer os, Document doc, String encoding) throws IOException {
    Properties outputProp = null;
//    Writer w = null;
    try {
      if (encoding != null && !encoding.equals("")) {
        outputProp = new Properties();
        outputProp.put(OutputKeys.ENCODING, encoding);
      }
      StreamResult sr = new StreamResult(os);
      writeStreamResult(sr, doc, outputProp);
    } catch (TransformerException e) {
      IOException ioe = new IOException();
      ioe.initCause(e);
      throw ioe;
    }
  }

  private static void writeStreamResult(StreamResult sr, Document doc,
                                        Properties outputProp) throws TransformerException {
    DOMSource doms = new DOMSource(doc);

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = tf.newTransformer();

    //默认有缩进,是xml文本
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    // 如果有输出属性,则设置为传入的输出属性,此属性是控制输出文本的格式等,
    // 具体见 javax.xml.transform.OutputKeys 类
    if (outputProp != null) {
      t.setOutputProperties(outputProp);
    }

    t.transform(doms, sr);
  }
}

