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
        String count = valueOf(repository.count());
        String timestamp = valueOf(new Date().getTime());
        String number = format("%s%s",count.substring(count.length()-Math.min(2, count.length())),timestamp.substring(timestamp.length()-10));
        return getNumberWithoutLeftPad(number.substring(0, Math.min(number.length(), size)));
    }

    public String getNumberWithoutLeftPad(String number) {
        return Long.valueOf(number.replaceAll("[^\\d]", "")).toString();
    }
}
