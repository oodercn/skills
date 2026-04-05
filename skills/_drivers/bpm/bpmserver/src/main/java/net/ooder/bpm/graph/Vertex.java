package net.ooder.bpm.graph;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-7-17
 * Time: 13:42:10
 * To change this template use File | Settings | File Templates.
 */
public class Vertex extends GraphNode {
  private String shape;
  private String status;
  private String message;
  private String gradientColor;
  private String statusMessage;

//  private boolean display=true;

  //各显示图形
  public static final String SHAPE_RHOMBUS="rhombus";
  public static final String SHAPE_ELLIPSE="ellipse";
  public static final String SHAPE_TRIANGLE="triangle";
  public static final String SHAPE_RECTANGLE="rectangle";

  public static final String colorName="fillColor";
  public static final String gradientColorName="gradientColor";

  public static final String COLOR_RED="#F00";
  public static final String COLOR_GREEN="#0F0";
  public static final String COLOR_BLUE="#00F";

  //所有状态
//  public static final String STATUS_WARNING="warning";
  public static final String STATE_OPEN = "open";
  public static final String STATE_RUNNING = "running";
  public static final String STATE_NOTRUNNING = "notRunning";
  public static final String STATE_NOTSTARTED = "notStarted";
  public static final String STATE_SUSPENDED = "suspended";
  public static final String STATE_CLOSED = "closed";
  public static final String STATE_ABORTED = "aborted";
  public static final String STATE_TERMINATED = "terminated";
  public static final String STATE_COMPLETED = "completed";
  public static final String STATUS_NORMAL = "NORMAL";
  public static final String STATUS_READ = "READ";
  public static final String STATUS_PROCESSNOTSTARTED = "processNotStarted";
  public static final String STATUS_ENDREAD = "ENDREAD";
  public static final String STATUS_DELAY = "DELAY";
  public static final String STATUS_URGENCY = "URGENCY";
  public static final String STATUS_ALERT = "ALERT";



  public String getShape() {
    return shape;
  }

  /**
   * 节点显示的图形,默认为矩形
   * @param shape
   */
  public void setShape(String shape) {
    this.shape = shape;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStatus() {
    return status;
  }

  public String getGradientColor() {
    return gradientColor;
  }

  public void setGradientColor(String gradientColor) {
    this.gradientColor = gradientColor;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }
  /**
   * 此节点的状态,即显示在右下角的小图标
   * @param status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  public Vertex(String id) {
    this.id = id;
    this.type = GraphNode.VERTEX;
    this.width=80;
    this.height=40;
  }
}

