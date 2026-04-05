package net.ooder.bpm.graph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-7-17
 * Time: 13:41:10
 * To change this template use File | Settings | File Templates.
 */
public class XMLGraph {
  private List<Vertex> vertexs = new ArrayList<Vertex>();
  private List<Edge> edges = new ArrayList<Edge>();
  private List<GraphNode> roots = new ArrayList<GraphNode>();
  public static final String defaultParentId = "1";
  public static final String rootId = "0";

//  private boolean isSub=false;
//
//  public boolean isSub() {
//    return isSub;
//  }
//
//  public void setSub(boolean sub) {
//    isSub = sub;
//  }

  public List<Vertex> getVertexs() {
    return vertexs;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void addVertexs(Vertex v) {
    this.vertexs.add(v);
  }

  public void addEdge(Edge e) {
    this.edges.add(e);
  }

  public void addRoot(GraphNode n) {
    n.setParent(rootId);
    roots.add(n);
  }

  public XMLGraph() {
    Vertex v1 = new Vertex(defaultParentId);
    addRoot(v1);
  }

  /**
   * 转成utf8格式的xml格式字符串
   * @return
   * @throws Exception
   */
  public String getXmlStr() throws Exception {
    return getXmlStr("utf8");
  }

  /**
   * 转成指定编码的xml格式字符串
   * @param encoding
   * @return
   * @throws Exception
   */
  public String getXmlStr(String encoding) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    writeXmlTo(bos, encoding);
    return new String(bos.toByteArray(), encoding);
  }

  /**
   * 以xml格式,按指定的编码,写到指定的输出流
   * @param os
   * @param encoding
   * @throws Exception
   */
  public void writeXmlTo(OutputStream os, String encoding) throws Exception {
    Document doc = getXmlDocument();
    XmlBuilder.writeXml(os, doc, encoding);
  }

  /**
   * 以xml格式,按指定的编码,写到指定的Writer
   * @param os
   * @param encoding
   * @throws Exception
   */
  public void writeXmlTo(Writer os, String encoding) throws Exception {
    Document doc = getXmlDocument();
    XmlBuilder.writeXml(os, doc, encoding);
  }

  /**
   * 取得此对象对应的xml 对象
   * @return
   * @throws Exception
   */
  public Document getXmlDocument() throws Exception {
    Document doc = new XmlBuilder().newDocument();
    Element root0 = doc.createElement("mxGraphModel");
    Element root = doc.createElement("root");
    root0.appendChild(root);
    doc.appendChild(root0);

    Element graphRoot = doc.createElement("mxCell");
    graphRoot.setAttribute("id", rootId);
    root.appendChild(graphRoot);

    for (int i = 0, c = roots.size(); i < c; i++) {
      GraphNode n = roots.get(i);
      Element cell = doc.createElement("mxCell");
      cell.setAttribute("id", n.getId());
      String parent = n.getParent();
      if (parent != null && !parent.equals("")) {
        cell.setAttribute("parent", parent);
      } else {
        cell.setAttribute("parent", rootId);
      }
      root.appendChild(cell);
    }
    for (int i = 0, c = vertexs.size(); i < c; i++) {
      Vertex v = vertexs.get(i);
      Element cell = doc.createElement("mxCell");
      cell.setAttribute("id", v.getId());
      String pId=v.getParent();
      if (pId == null || pId.equals("")) {
        pId=XMLGraph.defaultParentId;
      }
      cell.setAttribute("parent", pId);
      cell.setAttribute("value", v.getValue());
      cell.setAttribute("vertex", "1");
      String image = v.getShape();
      String style = v.getStyle();
      if (style == null) {
        style="";
      }
      if (image != null) {
        style += "shape=" + image+";";
      }
      String color=v.getColor();
      if(color!=null&&!color.equals("")){
        style+=Vertex.colorName+"="+color+";";
      }
      String gcolor=v.getGradientColor();
      if(gcolor!=null&&!gcolor.equals("")){
        style+=Vertex.gradientColorName+"="+gcolor+";";
      }

      cell.setAttribute("style", style);
      String status=v.getStatus();
      if (status != null && !status.equals("")) {
        cell.setAttribute("status",status);
      }

      cell.setAttribute("statusMessage",v.getStatusMessage());
      Element geom = doc.createElement("mxGeometry");
      geom.setAttribute("x", v.getX() + "");
      geom.setAttribute("y", v.getY() + "");
      geom.setAttribute("width", v.getWidth() + "");
      geom.setAttribute("height", v.getHeight() + "");
      geom.setAttribute("as", "geometry");
      cell.appendChild(geom);
      root.appendChild(cell);
    }
    for (int i = 0, c = edges.size(); i < c; i++) {
      Edge edge = edges.get(i);
      Element cell = doc.createElement("mxCell");

      cell.setAttribute("edge", "1");
      cell.setAttribute("id", edge.getId());
      cell.setAttribute("value", edge.getValue());
      String style=edge.getStyle();
      if(style==null){
        style="";
      }
      String status=edge.getEdgeStatus();
      if(status!=null&&!status.equals("")){
        style+=Edge.edgeStatusMap.get(status);
      }else{
        if(edge.isDashed()){
          style+="dashed=true;";
        }
        if(edge.getColor()!=null){
          style+=Edge.colorName+"="+edge.getColor()+";";
        }
      }
      cell.setAttribute("style", style);
      cell.setAttribute("parent", edge.getParent());
      cell.setAttribute("source", edge.getSourceId());
      cell.setAttribute("target", edge.getTargetId());
      Element geom = doc.createElement("mxGeometry");
      geom.setAttribute("relative", "1");
      geom.setAttribute("as", "geometry");

      List<Point>ps=edge.getPoints();
      if(ps!=null&&ps.size()>0){
        Element a=doc.createElement("Array");
        a.setAttribute("as","points");
        for (int j = 0; j < ps.size(); j++) {
          Point point =  ps.get(j);
          Element p=doc.createElement("mxPoint");
          p.setAttribute("x",point.getX()+"");
          p.setAttribute("y",point.getY()+"");
          a.appendChild(p);
        }
        geom.appendChild(a);
      }

      cell.appendChild(geom);
      root.appendChild(cell);
    }
    return doc;
  }
}

