package br.com.unopay.api.infra;

import br.com.unopay.api.model.Billable;
import org.springframework.data.repository.CrudRepository;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class NumberGenerator {

    private static final int DEFAULT_SIZE = 16;
    private CrudRepository repository;
    public static final String EMPTY = "";
    public static final String ZERO = "0";

    public NumberGenerator(CrudRepository repository) {
        this.repository = repository;
    }

    public synchronized String createNumber(Billable order) {
        return createNumber(order, DEFAULT_SIZE);
    }

    public synchronized String createNumber(Billable order, int size) {
        long count = repository.count();
        String number = format("%s%s%s",valueOf(count),valueOf(order.getCreateDateTime().getTime()),
                order.getNumber().replace(ZERO, EMPTY));
        return getNumberWithoutLeftPad(number.substring(0, Math.min(number.length(), size)));
    }

    public String getNumberWithoutLeftPad(String number) {
        return Long.valueOf(number.replaceAll("[^\\d]", "")).toString();
    }
}
