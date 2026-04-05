/**
 * $RCSfile: TimeUtility.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:55 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.ooder.common.util.DateUtility;

/**
 * <p>
 * Title: JDS系统管理系统 Description: 根据时间的值（int型）及其单位取得时间（Date型）。 Copyright: itjds Copyright (c) 2008 Company:
 * www.justdos.net
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class TimeUtility {

    // 时间单位
    public static final String DURATION_YEAR = "Y";

    public static final String DURATION_MONTH = "M";

    public static final String DURATION_DAY = "D";

    public static final String DURATION_HOUR = "H";

    public static final String DURATION_MINUTE = "m";

    public static final String DURATION_SECOND = "s";

    public static final String DURATION_WORKDAY = "W";

    public static final long SECOND = 1000;

    public static final long MINUTE = SECOND * 60;

    public static final long HOUR = MINUTE * 60;

    public static final long DAY = HOUR * 24;

    /**
     * 调整时间的函数。根据数值及其单位解释成时间，再根据原来的时间进行调整后返回。
     * 
     * @param start
     *            起始时间
     * @param offset
     *            需要调整的数量
     * @param duration
     *            时间单位
     * @return 调整后的时间
     */
    public static Date roll(Date start, int offset, String duration) {
	if (offset == 0) {
	    return start;
	}
	if (duration == null) {
	    return null;
	}
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(start);
	roll(calendar, offset, duration);

	return calendar.getTime();
    }

    /**
     * @param calendar
     * @param offset
     * @param duration
     */
    private static void roll(Calendar calendar, int offset, String duration) {
	long result = 0;
	long start = 0;
	if (duration.equals(DURATION_YEAR)) {
	    StringBuffer start_y = new StringBuffer();
	    start = calendar.getTime().getTime();
	    start_y.append(DateUtility.formatDate(new Date(start), "yyyy-MM-dd"));
	    String next_y = start_y.toString();
	    for (int i = 0; i < offset * 12; i++) {
		next_y = DateUtility.getDateAfterMonth(next_y);
	    }
	    int defDay = (int) DateUtility.compareDate(start_y.toString(), next_y);
	    result = start + (defDay * DAY) - DAY;
	    calendar.setTimeInMillis(result);
	    return;
	}
	if (duration.equals(DURATION_MONTH)) {
	    StringBuffer start_y = new StringBuffer();
	    start = calendar.getTime().getTime();
	    start_y.append(DateUtility.formatDate(new Date(start), "yyyy-MM-dd"));
	    String next_y = start_y.toString();
	    for (int i = 0; i < offset; i++) {
		next_y = DateUtility.getDateAfterMonth(next_y);
	    }
	    int defDay = (int) DateUtility.compareDate(start_y.toString(), next_y);
	    result = start + (defDay * DAY) - DAY;
	    calendar.setTimeInMillis(result);
	    return;
	}
	if (duration.equals(DURATION_DAY)) {
	    calendar.add(Calendar.DATE, offset - 1);
	    return;
	}
	if (duration.equals(DURATION_HOUR)) {
	    calendar.add(Calendar.HOUR, offset);
	}
	if (duration.equals(DURATION_MINUTE)) {
	    calendar.add(Calendar.MINUTE, offset);
	}
	if (duration.equals(DURATION_SECOND)) {
	    calendar.add(Calendar.SECOND, offset);
	}
	// if (duration.equals(DURATION_WORKDAY)) {
	// String end= null;
	// String start_w = DateUtility.formatDate(calendar.getTime(), "yyyy-MM-dd");
	// try {
	// end = getEndDay(start_w, offset);
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// result = DateUtility.getDayD(end).getTime();
	//
	// calendar.setTimeInMillis(result);
	// }
    }

    public static void main(String[] args) {
	Date d = new Date(System.currentTimeMillis());
	Calendar c = Calendar.getInstance();

	Date dd = roll(d, 11, DURATION_WORKDAY);
	String bbb = "888";
	String ddd = "ddd";
	Map ccc = new HashMap();
	ccc.put(ddd, bbb);
	Object[] ff = new Object[] { "5555" };
	// ccc.put("end",ff);
	String aaa = (String) ccc.keySet().iterator().next();
	boolean vvv = ccc.containsKey(ddd);
	boolean fff = ccc.containsKey("end");

	boolean ggg = ccc.containsValue(null);
	// boolean ccc.ccc
	if (ccc.get(aaa) instanceof String) {
	    System.out.println(aaa);
	}
	;

    }

    // /**
    // * 计算当月工作日方法，返回为当月工作日数组
    // * @param year
    // * @param month
    // * @param day
    // * @return
    // * @throws DAOException
    // * @throws SQLException
    // */
    // public static List getCurrentMonthWork(String year,String month,String day) throws DAOException, SQLException{
    //
    //
    // DBBeanBase dbbase = new DBBeanBase("bpm");
    //
    // CalendarworkDAO cw = new CalendarworkDAO();
    // DAOFactory factory = new DAOFactory(dbbase.getConn());
    // factory.setDAO(cw);
    // Set workdate = new HashSet();
    // StringBuffer parm = new StringBuffer();
    // /***************将当月人工设置的工作日放入SET**************************/
    // parm.append(" year = ");
    // parm.append(year);
    // parm.append(" and month = ");
    // parm.append(month);
    // parm.append(" and flag = 1");
    // cw.setWhereClause(parm.toString());
    // List rs = factory.find();
    // for(int i = 0 ; i < rs.size();i++){
    // CalendarworkDAO tcw = (CalendarworkDAO)rs.get(i);
    // workdate.add(tcw.getDay());
    // }
    //
    // /****************将当月正常正常工作日放入SET**************************/
    // int last = DateUtility.getLastDate(year+"-"+month+"-"+day);
    // for(int j =1 ; j <= last; j++){
    // Date tdate = DateUtility.getDayD(year+"-"+month+"-"+j);
    // Calendar cal = Calendar.getInstance();
    // cal.setTime(tdate);
    // int week = cal.get(cal.DAY_OF_WEEK);
    // if(week > 1 && week < 7 ){
    // workdate.add(j);
    // }
    // //System.out.println(tdate+"|"+week+"|");//1,7为周未
    // }
    // /****************将当月人工设置的假日从SET中过滤**********************************/
    // parm = new StringBuffer();
    // parm.append(" year = ");
    // parm.append(year);
    // parm.append(" and month = ");
    // parm.append(month);
    // parm.append(" and flag = -1");
    // cw.setWhereClause(parm.toString());
    // rs = factory.find();
    // for(int i = 0 ; i < rs.size();i++){
    // CalendarworkDAO tcw = (CalendarworkDAO)rs.get(i);
    // workdate.remove(tcw.getDay());
    // }
    // /**************显示关闭数据源*******************/
    // dbbase.getConn().close();
    // Iterator it = workdate.iterator();
    // List list = new ArrayList();
    //
    // while(it.hasNext()){
    // Integer key = (Integer)it.next();
    // list.add(key);
    // }
    // workdate.clear();
    // Collections.sort(list,new Comparator() {
    // public int compare(Object o1, Object o2) {
    // if(((Integer)o1).intValue() > ((Integer)o2).intValue())
    // return 1;
    // else
    // return 0 ;
    // }
    //
    // });
    // return list;
    // }
    // /**
    // * begDate=日期格式必须为："yyyy-MM-dd"
    // * interval=int必须大于等1
    // * @param begDate
    // * @param interval
    // * @return
    // * @throws IOException
    // * @throws SQLException
    // * @throws DAOException
    // */
    // public static String getEndDay(String begDate,int interval) throws IOException, DAOException, SQLException{
    // if(interval < 1)return begDate;//工作日容错
    // String b_year = begDate.split("-")[0];
    // String b_month = begDate.split("-")[1];
    // String b_day = begDate.split("-")[2];
    // //response.getWriter().print(b_year+"-"+b_month+"-"+b_day);
    // Integer y = Integer.parseInt(b_year);
    // Integer m = Integer.parseInt(b_month);
    // Integer d = Integer.parseInt(b_day);
    // String result = null;
    // List rs = new ArrayList();
    // int control = 0;
    // boolean flag = false;
    // while(rs.size() < interval){
    // if(flag){
    // String next = DateUtility.getDateAfterMonth(y+"-"+m+"-"+d);
    // //response.getWriter().print("<br>------------"+next+"----------------<br>");
    // y = Integer.parseInt(next.split("-")[0]);
    // m = Integer.parseInt(next.split("-")[1]);
    // d = Integer.parseInt(next.split("-")[2]);
    // }
    // List l = getCurrentMonthWork(y.toString(), m.toString(), d.toString());
    // if(!flag){
    // for(int i = 0 ; i < l.size();i++){
    // Integer val = (Integer)l.get(i);
    // int last = ((Integer)l.get(l.size()-1)).intValue();
    // if( last == d.intValue()){
    // if(rs.size() < interval){
    // rs.add(y+"-"+m+"-"+d.intValue());
    // }
    // break;
    // }else if(val.intValue() >= d.intValue()){
    // if(rs.size() < interval){
    // rs.add(y+"-"+m+"-"+val.intValue());
    // }
    // }
    //
    // }
    // }else{
    // for(int i = 0 ; i < l.size();i++){
    // Integer val = (Integer)l.get(i);
    // if(rs.size() < interval){
    // rs.add(y+"-"+m+"-"+val.intValue());
    // }
    // }
    // }
    // flag = true;
    // }
    // String rst = (String)rs.get(rs.size()-1);
    // return rst;
    // }
    //
    //
    //
    // /**
    // * 0 工作日
    // * 1 秒
    // * 2 分
    // * 3 小时
    // * 4 天
    // * 5 月
    // * 6 年
    // * @param startTime开始时间
    // * @param defTime定义时间
    // * @param warningTime预警时间
    // * @param unit单位
    // * @return
    // * @throws SQLException
    // * @throws IOException
    // * @throws DAOException
    // */
    // public String getState(long startTime,int defTime,int warningTime,int unit) throws DAOException, IOException,
    // SQLException{
    // long currTime = 0;
    // long finished = 0;
    // long warring = 0;
    // Date tempDate ;
    // Date cur = Calendar.getInstance().getTime();
    // currTime = cur.getTime();
    // //正常，预警，超时
    // String result = "正常";
    // int flag = defTime - warningTime;
    // if(flag < 0)// 预警>时限
    // return result;
    // if(defTime == 0 && warningTime == 0)//预警，时限为0
    // return result;
    //
    // switch(unit){
    // case 0:
    // /*************************************************************************************************************
    // ******************************************** 工作日期 ********************************************
    // *************************************************************************************************************/
    // String end= null;
    // String start = null;
    // //**********************时限
    // if(defTime != 0){
    // start = DateUtility.formatDate(new Date(startTime), "yyyy-MM-dd");
    // end = getEndDay(start, defTime+1);//加1，为大于那天日期，便于时间判断
    // finished = DateUtility.getDayD(end).getTime();
    // }else{
    // finished = cur.getTime()+DAY;
    // }
    // //**********************预警
    // if(warningTime != 0 && flag != 0){
    // start = DateUtility.formatDate(new Date(startTime), "yyyy-MM-dd");
    // end = getEndDay(start, warningTime+1);//加1，为大于那天日期,便于时间判断
    // warring = DateUtility.getDayD(end).getTime();
    // }else{
    // warring = cur.getTime() + DAY;
    // }
    //
    // break;
    // case 1:
    // /*************************************************************************************************************
    // ******************************************** 秒 ********************************************
    // *************************************************************************************************************/
    // finished = startTime + (defTime * SECOND);
    // warring = startTime + (warningTime * SECOND);
    // break;
    // case 2:
    // /*************************************************************************************************************
    // ******************************************** 分 ********************************************
    // *************************************************************************************************************/
    // finished = startTime + (defTime * MINUTE);
    // warring = startTime + (warningTime * MINUTE);
    // break;
    // case 3:
    // /*************************************************************************************************************
    // ******************************************** 小时 ********************************************
    // *************************************************************************************************************/
    // finished = startTime + (defTime * HOUR);
    // warring = startTime + (warningTime * HOUR);
    // break;
    // case 4:
    // /*************************************************************************************************************
    // ******************************************** 天 ********************************************
    // *************************************************************************************************************/
    //// ********************************************时限
    // if(defTime != 0){
    // finished = startTime + (defTime * DAY) ;
    // }else{
    // finished = cur.getTime()+DAY;
    // }
    //// ********************************************预警
    // if(warningTime != 0 && flag != 0){
    // warring = startTime + (warningTime * DAY) ;
    // }else{
    // warring = cur.getTime()+DAY;
    // }
    // break;
    // case 5:
    // /*************************************************************************************************************
    // ******************************************** 月 ********************************************
    // *************************************************************************************************************/
    // StringBuffer start_m = new StringBuffer();
    // start_m.append(DateUtility.formatDate(new Date(startTime),"yyyy-MM-dd"));
    // String next_m = start_m.toString();
    //// ********************************************时限
    // if(defTime != 0){
    // for(int i = 0 ; i < defTime;i++){
    // next_m = DateUtility.getDateAfterMonth(next_m);
    // }
    // int defDay = (int)DateUtility.compareDate(start_m.toString(), next_m);
    // finished = startTime + (defDay * DAY) ;
    // System.out.println("*****************"+defDay);
    // }else{
    // finished = cur.getTime()+DAY;
    // }
    //// ********************************************预警
    // next_m =start_m.toString();
    // if(warningTime != 0 && flag != 0){
    // for(int i = 0 ; i < warningTime;i++){
    // next_m = DateUtility.getDateAfterMonth(next_m);
    // }
    // int defDay = (int)DateUtility.compareDate(start_m.toString(), next_m);
    // System.out.println(start_m.toString()+"***"+next_m+"*****************"+defDay);
    // warring = startTime + (defDay * DAY) ;
    // }else{
    // warring = cur.getTime()+DAY;
    // }
    //
    // break;
    // case 6:
    // /*************************************************************************************************************
    // ******************************************** 年 ********************************************
    // *************************************************************************************************************/
    // StringBuffer start_y = new StringBuffer();
    // start_y.append(DateUtility.formatDate(new Date(startTime),"yyyy-MM-dd"));
    // String next_y = start_y.toString();
    //// ********************************************时限
    // if(defTime != 0){
    // for(int i = 0 ; i < defTime*12;i++){
    // next_y = DateUtility.getDateAfterMonth(next_y);
    // }
    // int defDay = (int)DateUtility.compareDate(start_y.toString(), next_y);
    // finished = startTime + (defDay * DAY) ;
    // System.out.println("*****************"+defDay);
    // }else{
    // finished = cur.getTime()+DAY;
    // }
    //// ********************************************预警
    // next_y =start_y.toString();
    // if(warningTime != 0 && flag != 0){
    // for(int i = 0 ; i < warningTime*12;i++){
    // next_y = DateUtility.getDateAfterMonth(next_y);
    // }
    // int defDay = (int)DateUtility.compareDate(start_y.toString(), next_y);
    // System.out.println(start_y.toString()+"***"+next_y+"*****************"+defDay);
    // warring = startTime + (defDay * DAY) ;
    // }else{
    // warring = cur.getTime()+DAY;
    // }
    //
    // break;
    // }
    // if(currTime > warring && currTime < finished){
    // result = "预警";
    // }else if(currTime > finished){
    // result = "超时";
    // }
    //
    // /*System.out.println("str:"+startTime+"\ncur:"+currTime+"\nwar:"+warring+"\nfin:"+finished);
    // SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:dd");
    // Date tt =new Date(startTime);
    // System.out.println(f.format(tt)+"开始时间****");
    // tt =new Date(warring);
    // System.out.println(f.format(cur)+"当前****");
    // System.out.println(f.format(tt)+"预警****");
    // tt =new Date(finished);
    // System.out.println(f.format(tt)+"结束****");*/
    // return result;
    // }

}


