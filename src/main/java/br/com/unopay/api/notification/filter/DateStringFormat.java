package br.com.unopay.api.notification.filter;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;
import java.text.SimpleDateFormat;
import lombok.SneakyThrows;

public class DateStringFormat implements Filter{

    @Override
    @SneakyThrows
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        if(args.length > 0 ) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat newFormat = new SimpleDateFormat(args[0]);
            return newFormat.format(simpleDateFormat.parse((String) var));
        }
        return null;
    }

    @Override
    public String getName() {
        return "dateformat";
    }
}
