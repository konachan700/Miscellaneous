package jneko;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JNekoSupportClass {
    
    
    public static String GetCurrentFormattedTime() {
        final Date d = new Date();
        final SimpleDateFormat DF = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return DF.format(d);
    }
    
    public static String GetCurrentFormattedTime(long time) {
        final Date d = new Date(time);
        final SimpleDateFormat DF = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return DF.format(d);
    }

    private static int __GetDayCount(int y, int m) {
        final Calendar c = new GregorianCalendar();
        c.setTime(new Date(y-1900, m - 1, 1));
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    public static int GetDayCountOnThisMonth() {
        final Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    public static String GetPayTime(int cpm, int money) {
        final Date d = new Date();
        final Calendar c = new GregorianCalendar();
        final int current_year = c.get(Calendar.YEAR);
        final int current_month = c.get(Calendar.MONTH) + 1;
        final int current_day = c.get(Calendar.DAY_OF_MONTH);
        
        int money_buf = money;
        for (int year=current_year; year<=(current_year+3); year++) {
            for (int month=1; month<=12; month++) {
                if ((year > current_year) || (month >= current_month)) {
                    int daysInMonth = __GetDayCount(year, month);
                    for (int day=1; day<=daysInMonth; day++) {
                        if ((year > current_year) || (month > current_month) || (day > current_day)) {
                            money_buf = money_buf - (cpm / daysInMonth);
                            if (money_buf <= 0) {
                                final Date retd = new Date(year-1900, month-1, day);
                                final SimpleDateFormat DF = new SimpleDateFormat("dd.MM.yyyy");
                                return DF.format(retd);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
    
    
}
