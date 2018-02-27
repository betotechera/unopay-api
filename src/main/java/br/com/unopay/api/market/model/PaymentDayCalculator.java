package br.com.unopay.api.market.model;

import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentDayCalculator {

    public static final int MAX_PAYMENT_DAY = 28;
    public static final int ONE_MONTH = 1;
    @Value("${unopay.boleto.deadline_in_days}")
    private Integer ticketDeadLineInDays;

    public Integer getNearDay(){
        Integer firstPaymentDayOfNextMonth = 1;
        Integer nearPaymentDay = nearDay();
        return  nearPaymentDay > MAX_PAYMENT_DAY ? firstPaymentDayOfNextMonth : nearPaymentDay;
    }

    private Integer nearDay() {
        Integer currentDay = new DateTime().dayOfMonth().get();
        return currentDay + ticketDeadLineInDays;
    }

    public Date getNearDate(){
        if(nearDay() > MAX_PAYMENT_DAY){
            return new DateTime().plusMonths(ONE_MONTH).withDayOfMonth(getNearDay()).toDate();
        }
        return new DateTime().withDayOfMonth(getNearDay()).toDate();
    }
}
