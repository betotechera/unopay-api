package br.com.unopay.api.infra;

import java.util.Date;
import org.springframework.data.repository.CrudRepository;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class NumberGenerator {

    private static final int DEFAULT_SIZE = 12;
    private CrudRepository repository;
    public static final String ZERO = "0";

    public NumberGenerator(CrudRepository repository) {
        this.repository = repository;
    }

    public synchronized String createNumber() {
        return createNumber(DEFAULT_SIZE);
    }

    public synchronized String createNumber(int size) {
        long count = repository.count();
        String number = format("%s%s",valueOf(count),valueOf(new Date().getTime()));
        return getNumberWithoutLeftPad(number.substring(0, Math.min(number.length(), size)));
    }

    public String getNumberWithoutLeftPad(String number) {
        return Long.valueOf(number.replaceAll("[^\\d]", "")).toString();
    }
}
