package br.com.unopay.api.payment;

import java.util.Collection;

public interface RemittanceLine {

    Collection<RemittanceColumn> getValues();
}
