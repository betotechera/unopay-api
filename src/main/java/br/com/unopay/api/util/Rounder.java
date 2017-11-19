package br.com.unopay.api.util;

import java.math.BigDecimal;

public class Rounder {

    private Rounder(){}

    public static BigDecimal round(BigDecimal value){
        return value.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal zero(){
        return round(BigDecimal.ZERO);
    }

    public static String roundToString(BigDecimal value){
        return value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
    }
}
