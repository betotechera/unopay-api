package br.com.unopay.api.util;

import java.math.BigDecimal;

public class Rounder {

    public static final int ROUND_STRATEGY = BigDecimal.ROUND_HALF_UP;

    private Rounder(){}

    public static BigDecimal round(BigDecimal value){
        return value.setScale(2, ROUND_STRATEGY);
    }

    public static BigDecimal zero(){
        return round(BigDecimal.ZERO);
    }

    public static String roundToString(BigDecimal value){
        return value.setScale(2, ROUND_STRATEGY).toString();
    }
}
