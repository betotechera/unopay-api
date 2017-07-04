package br.com.unopay.api.payment;

import static br.com.unopay.api.payment.RemittanceLayout.getTrailer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemittanceFileHeader implements RemittanceLine {

    private final List<RemittanceColumn> columns = new ArrayList<>();

    public void add(String key, String value) {
        columns.add(new RemittanceColumn(getTrailer().get(key),value));
    }

    public Collection<RemittanceColumn> getValues(){
        return columns;
    }
}
