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
    private DateTime date;

    public PaymentDayCalculator(){
        this.date = new DateTime();
    }

    public PaymentDayCalculator(DateTime date, Integer deadLineInDays){
        this.date = date;
        this.ticketDeadLineInDays = deadLineInDays;
    }

    public Integer getNearDay(){
        Integer firstPaymentDayOfNextMonth = 1;
        Integer nearPaymentDay = nearDay();
        return  nearPaymentDay > MAX_PAYMENT_DAY ? firstPaymentDayOfNextMonth : nearPaymentDay;
    }

    private Integer nearDay() {
        Integer currentDay = date.dayOfMonth().get();
        return currentDay + ticketDeadLineInDays;
    }

    public Date getNearDate(){
        if(nearDay() > MAX_PAYMENT_DAY){
            return date.plusMonths(ONE_MONTH).withDayOfMonth(getNearDay()).toDate();
        }
        return date.withDayOfMonth(getNearDay()).toDate();
    }
}
