package br.com.unopay.api.market.model;

import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentDayCalculator {

    @Value("${unopay.boleto.deadline_in_days}")
    private Integer ticketDeadLineInDays;

    public Integer getNearDay(){
        Integer maxPaymentDay = 28;
        Integer firstPaymentDayOfNextMonth = 1;
        Integer currentDay = new DateTime().dayOfMonth().get();
        Integer nearPaymentDay = currentDay + ticketDeadLineInDays;
        return  nearPaymentDay > maxPaymentDay ? firstPaymentDayOfNextMonth : nearPaymentDay;
    }

    public Date getNearDate(){
        return new DateTime().withDayOfMonth(getNearDay()).toDate();
    }
}
