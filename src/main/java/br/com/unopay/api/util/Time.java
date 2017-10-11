package br.com.unopay.api.util;

import java.util.Date;
import org.joda.time.DateTime;


public class Time {

    private Time(){}

    public static Date create(){
        return create(null);
    }

    public static Date create(Integer days){
        if(days != null){
            return createDateTime().plusDays(days).toDate();
        }
        return createDateTime().toDate();
    }

    public static DateTime createDateTime(Date initialDate){
        DateTime dateTime = new DateTime(initialDate);
        return dateTime.withHourOfDay(1)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    public static DateTime createDateTime(){
        return createDateTime(new Date());
    }

}
