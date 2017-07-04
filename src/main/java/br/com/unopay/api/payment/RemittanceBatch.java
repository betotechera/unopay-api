package br.com.unopay.api.payment;

import static br.com.unopay.api.payment.RemittanceLayout.getSegment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemittanceBatch implements RemittanceLine{


    private final List<RemittanceColumn> columns = new ArrayList<>();

    public void add(String key, String value) {
        columns.add(new RemittanceColumn(getSegment().get(key),value));
    }

    public Collection<RemittanceColumn> getValues(){
        return columns;
    }
}
