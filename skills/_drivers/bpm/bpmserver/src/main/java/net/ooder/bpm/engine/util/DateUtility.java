/**
 * $RCSfile: DateUtility.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/08/11 14:40:16 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * Title: 审批程序演示系统
 * </p>
 * <p>
 * Description: 日期常用的方法
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-12-5
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @version 1.0
 * @author wenzhang li
 */
public class DateUtility {

	/**
	 * 比较两个日期 返回值为两个日期相差的天数
	 * 
	 * @return int
	 * @param sDate1
	 *            java.lang.String
	 * @param sDate2
	 *            java.lang.String
	 */
	public static int compareDate(String sDate1, String sDate2) {
		DateFormat dateFormat = DateFormat.getDateInstance();
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = dateFormat.parse(sDate1);
			date2 = dateFormat.parse(sDate2);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		long dif = 0;
		if (date2.after(date1))
			dif = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24;
		else
			dif = (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;

		return (int) dif;
	}

	/**
	 * 比较两个日期 返回值为两个日期相差的天数
	 * 
	 * @return int
	 * @param sDate1
	 *            java.lang.String
	 * @param sDate2
	 *            java.lang.String
	 */
	public static int simpleCompareDate(String sDate1, String sDate2) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = dateFormat.parse(sDate1);
			date2 = dateFormat.parse(sDate2);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		long dif = 0;
		if (date2.after(date1))
			dif = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24;
		else
			dif = (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;

		return (int) dif;
	}

	/**
	 * 获取当前日期字符串 格式为YYYY-MM-DD
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String s = df.format(new Date());
		return s;
	}

	/**
	 * 获取当前日期及时间字符串 格式为YYYY-MM-DD HH:mm:ss
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = df.format(new Date());
		return s;
	}

	/**
	 * 获取当前时间字符串 格式为HH:mm:ss
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(new Date());
	}

	/**
	 * 获取当前日期中的日
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentDay() {
		String day;
		SimpleDateFormat df = new SimpleDateFormat("d");
		day = df.format(new Date());
		return day;
	}

	/**
	 * 获取当前日期中的月
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentMonth() {
		String month;
		SimpleDateFormat df = new SimpleDateFormat("MM");
		month = df.format(new Date());
		return month;
	}

	/**
	 * 获取当前日期中的年
	 * 
	 * @return java.lang.String
	 */
	public static String getCurrentYear() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		return df.format(new Date());
	}

	/**
	 * 将字符串格式日期转换成long型
	 * 
	 * @param strDate
	 *            type:YYYY-MM-DD
	 * @return java.lang.Long
	 */
	public static Long getLongDate(String strDate) {
		Date date = java.sql.Date.valueOf(strDate);
		Long lDate = new Long(date.getTime());
		return (lDate);
	}

	/**
	 * 将字符串格式日期转换成long型 *
	 * 
	 * @param iType
	 *            value strDate type 0 :YYYY-MM-DD 1 :YYYY-MM-DD hh:mm:ss
	 * @return java.lang.Long
	 */
	public static Long getLongDate(String strDate, int iType) {
		Long retDate = null;
		switch (iType) {
		case 0:
			retDate = getLongDate(strDate);
			break;
		case 1:
			retDate = new Long(java.sql.Timestamp.valueOf(strDate).getTime());
			break;
		}
		return retDate;
	}

	/**
	 * 将Long格式日期转换成字符串型
	 * 
	 * @param lDate
	 *            iType value output 0 YYYY-MM-DD 1 YYYY-MM-DD hh 2 YYYY-MM-DD
	 *            hh:ss 3 YYYY-MM-DD hh:ss:mm
	 * @return java.lang.String
	 */

	public static String getStrDate(java.lang.Long lDate, int iType) {
		Date date = new Date(lDate.longValue());
		SimpleDateFormat simpleDateFormat = null;
		if (lDate == null) {
			return "";
		}
		switch (iType) {
		case 0:
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			break;
		case 1:
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
			break;
		case 2:
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			break;
		case 3:
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		}

		String strDate = simpleDateFormat.format(date);
		return (strDate);
	}

	/**
	 * 将字符型日期加入指定天数(add in 2003.11.21
	 * 
	 * @author windy)
	 *         <p>
	 *         for example:
	 *         </p>
	 *         <p>
	 *         getDate("1970-1-1",2),so this will return "1970-1-3"
	 *         </p>
	 * @param String
	 *            aDate
	 * @param int
	 *            dif
	 * @return java.lang.String
	 */
	public static String getDate(String aDate, int dif) {
		java.sql.Date date = null;
		try {
			date = java.sql.Date.valueOf(aDate);
			System.out.println();
		} catch (Exception e) {
			System.err.println("Application log:Catch Exception in getDate()");
			System.err.println("aDate:" + aDate);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5, dif);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d");
		String s = df.format(calendar.getTime());
		return s;
	}// eof getDate(String,int)

	/**
	 * 获取字符型日期的下一个月时间(add in 2003.11.21
	 * 
	 * @author windy)
	 *         <p>
	 *         for example:
	 *         </p>
	 *         <p>
	 *         getDateAfterMonth("1970-1-1"),so this will return "1970-2-1"
	 *         </p>
	 * @param String
	 *            aDate
	 * @return java.lang.String
	 */
	public static String getDateAfterMonth(String aDate) {
		java.sql.Date date1 = null;
		try {
			date1 = java.sql.Date.valueOf(aDate);
		} catch (Exception e) {
			System.err
					.println("Application log:Catch Exception in getDateBeforeAMonth(String)");
			System.err.println("aDate:" + aDate);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(2, 1);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String s = df.format(calendar.getTime());
		return s;
	}// eof getDateAfterMonth(aDate)

	/**
	 * 获取字符型日期的下一个月的指定时间(add in 2003.11.21
	 * 
	 * @author windy)
	 *         <p>
	 *         for example:
	 *         </p>
	 *         <p>
	 *         getDateAfterMonth("1970-1-1",12),so this will return "1970-2-12"
	 *         </p>
	 * @param String
	 *            aDate
	 * @return java.lang.String
	 */
	public static String getDateAfterMonth(String aDate, int n) {
		DateFormat dateFormat = DateFormat.getDateInstance();
		Date date1 = null;
		try {
			date1 = dateFormat.parse(aDate);
		} catch (ParseException e) {
			System.err
					.println("Application log:Catch Exception in getDateBeforeAMonth(String)");
			System.err.println("aDate:" + aDate);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(2, 1);
		calendar.set(5, n);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String s = df.format(calendar.getTime());
		return s;
	}// eof getDateAfterMonth(String,int)

	/**
	 * 获取字符型日期的当月的最后一天(add in 2003.11.21
	 * 
	 * @author windy)
	 *         <p>
	 *         for example:
	 *         </p>
	 *         <p>
	 *         getLastDate("1970-1-1"),so this will return 31
	 *         </p>
	 * @param String
	 *            aDate
	 * @return int 当月最后一天的号码
	 */
	public static int getLastDate(String selectDate) {
		int dates = 0;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(DateFormat.getDateInstance().parse(selectDate));
		} catch (ParseException e) {
		}
		int year = calendar.get(1);
		switch (calendar.get(2) + 1) {
		default:
			break;

		case 1: // '\001'
			dates = 31;
			break;

		case 2: // '\002'
			if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0)
				dates = 29;
			else
				dates = 28;
			break;

		case 3: // '\003'
			dates = 31;
			break;

		case 4: // '\004'
			dates = 30;
			break;

		case 5: // '\005'
			dates = 31;
			break;

		case 6: // '\006'
			dates = 30;
			break;

		case 7: // '\007'
			dates = 31;
			break;

		case 8: // '\b'
			dates = 31;
			break;

		case 9: // '\t'
			dates = 30;
			break;

		case 10: // '\n'
			dates = 31;
			break;

		case 11: // '\013'
			dates = 30;
			break;

		case 12: // '\f'
			dates = 31;
			break;
		}
		return dates;
	}// eof getLastDate(String)

	/**
	 * ???????¨?????ò????????????
	 * 
	 * @return java.lang.String
	 * @param periodType
	 *            int 0±í?????? 1±í????·?
	 * @param year
	 *            java.lang.String
	 * @param period
	 *            java.lang.String
	 */
	public static String[] getDate(int periodType, String year, String period) {

		String[] dates = { getCurrentDate(), getCurrentDate() };

		// Validate
		if (periodType != 0 && periodType != 1) {
			// Error period type
			System.err.println("Error period type in DateUtil.getDate().");
			System.err.println("Period type(0-1):" + periodType);
			return dates;
		}

		int intYear = 2000;
		try {
			intYear = Integer.parseInt(year);
			if (intYear < 1900 || intYear > 3000) {
				System.err.println("Invalid year in DateUtil.getDate().");
				System.err.println("Year(1900-3000):" + year);
				return dates;
			}
		} catch (NumberFormatException e) {
			System.err.println("Invalid year in DateUtil.getDate().");
			System.err.println("Year:" + year);
			return dates;
		}

		int intPeriod = 1;
		try {
			intPeriod = Integer.parseInt(period);
			if (periodType == 0) {
				// Season
				if (intPeriod < 1 || intPeriod > 4) {
					System.err.println("Invalid season in DateUtil.getDate().");
					System.err.println("Season(1-4):" + period);
					return dates;
				}
			} else {
				// Month
				if (intPeriod < 1 || intPeriod > 12) {
					System.err.println("Invalid month in DateUtil.getDate().");
					System.err.println("Month(1-12):" + period);
					return dates;
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("Invalid period in DateUtil.getDate().");
			System.err.println("Period:" + period);
			return dates;
		}

		if (periodType == 0) {
			// Season
			switch (intPeriod) {
			case 1:
				dates[0] = year + "-1-1";
				dates[1] = year + "-3-31";
				break;
			case 2:
				dates[0] = year + "-4-1";
				dates[1] = year + "-6-30";
				break;
			case 3:
				dates[0] = year + "-7-1";
				dates[1] = year + "-9-30";
				break;
			case 4:
				dates[0] = year + "-10-1";
				dates[1] = year + "-12-31";
				break;
			}
		} else {
			// Month
			switch (intPeriod) {
			case 1:
				dates[0] = year + "-1-1";
				dates[1] = year + "-1-31";
				break;
			case 2:
				dates[0] = year + "-2-1";
				if ((intYear % 400 == 0)
						|| ((intYear % 4 == 0) && (intYear % 100 != 0))) {
					dates[1] = year + "-2-29";
				} else {
					dates[1] = year + "-2-28";
				}
				break;
			case 3:
				dates[0] = year + "-3-1";
				dates[1] = year + "-3-31";
				break;
			case 4:
				dates[0] = year + "-4-1";
				dates[1] = year + "-4-30";
				break;
			case 5:
				dates[0] = year + "-5-1";
				dates[1] = year + "-5-31";
				break;
			case 6:
				dates[0] = year + "-6-1";
				dates[1] = year + "-6-30";
				break;
			case 7:
				dates[0] = year + "-7-1";
				dates[1] = year + "-7-31";
				break;
			case 8:
				dates[0] = year + "-8-1";
				dates[1] = year + "-8-31";
				break;
			case 9:
				dates[0] = year + "-9-1";
				dates[1] = year + "-9-30";
				break;
			case 10:
				dates[0] = year + "-10-1";
				dates[1] = year + "-10-31";
				break;
			case 11:
				dates[0] = year + "-11-1";
				dates[1] = year + "-11-30";
				break;
			case 12:
				dates[0] = year + "-12-1";
				dates[1] = year + "-12-31";
				break;
			}
		}

		return dates;
	}// eof getDate(int,String,String)

	/*
	 * 获取ORACLE的默认日期格式时间(add in 2003-11-21 @author:windy) @param String aDate
	 * 如："2003-1-3" @return String 如："03-一月-2003 12:00:00 AM"
	 */
	public static String getOracleDefaultDate(String aDate) {
		String returnDate = new String();
		DateFormat dateFormat = DateFormat.getDateInstance();
		Date date1 = null;
		try {
			String[] dateArray = aDate.split("-");
			String thisYear = dateArray[0];
			int date1Month = Integer.parseInt(dateArray[1]);
			int date1Date = Integer.parseInt(dateArray[2]);
			String thisDate = new String();
			if (date1Date < 10) {
				thisDate = "0".concat(String.valueOf(date1Date));
			} else {
				thisDate = String.valueOf(date1Date);
			}
			String thisMonth = new String();
			switch (date1Month) {
			case 1:
				thisMonth = "一月";
				break;
			case 2:
				thisMonth = "二月";
				break;
			case 3:
				thisMonth = "三月";
				break;
			case 4:
				thisMonth = "四月";
				break;
			case 5:
				thisMonth = "五月";
				break;
			case 6:
				thisMonth = "六月";
				break;
			case 7:
				thisMonth = "七月";
				break;
			case 8:
				thisMonth = "八月";
				break;
			case 9:
				thisMonth = "九月";
				break;
			case 10:
				thisMonth = "十月";
				break;
			case 11:
				thisMonth = "十一月";
				break;
			case 12:
				thisMonth = "十二月";
				break;
			}
			returnDate = thisDate.concat("-").concat(thisMonth).concat("-")
					.concat(thisYear).concat(" 12:00:00 AM");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnDate;
	}// eof getOracleDefaultDate(String)

	/*
	 * 获取中国地区的星期几(add in 2004-1-13 @author:windy) @param int intNum @return
	 * String 如："星期一"
	 */
	public static String getDayDesc(int intNum) {
		String rn = "";
		if (intNum < 0 || intNum > 6) {
			rn = "";
		}
		switch (intNum) {
		case 0:
			rn = "星期一";
			break;
		case 1:
			rn = "星期二";
			break;
		case 2:
			rn = "星期三";
			break;
		case 3:
			rn = "星期四";
			break;
		case 4:
			rn = "星期五";
			break;
		case 5:
			rn = "星期六";
			break;
		case 6:
			rn = "星期日";
			break;
		}
		return rn;
	}

	/**
	 * 将日期由字符串转成日期型
	 * 
	 * @param s
	 *            yyyy-MM-dd HH:mm:ss
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static Date getDateD(String s) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		return dateFormatter.parse(s, pos);
	}

	/**
	 * 比较两个日期 返回值为第一个日期是否在第二个日期之后
	 * 
	 * @return boolean
	 * @param sDate1
	 *            java.lang.String
	 * @param sDate2
	 *            java.lang.String
	 */
	public static boolean afterDate(String sDate1, String sDate2) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = dateFormat.parse(sDate1);
			date2 = dateFormat.parse(sDate2);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		boolean afterThis = true;
		if (date2.after(date1))
			afterThis = false;
		return afterThis;
	}

	
	
	
	// test
	/*
	 * public static void main(String[] args){ String testDate = "2003-11-22";
	 * //System.out.println(DateUtility.getOracleDefaultDate("2003-11-22"));
	 * //System.out.println(DateUtility.getDate(testDate,-1));
	 * //System.out.println(DateUtility.getDate("2003-4-5",-1)); String
	 * currentTime = DateUtility.getCurrentDate();
	 * System.out.println(DateUtility.compareDate("2003-4-5","2004-3-3"));
	 * System.out.println(DateUtility.afterDate("2003-4-5","2003-4-5")); }
	 */// eof main
}
