package br.com.unopay.api.notification.filter;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

public class CnpjFormat implements Filter{

    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        if(var instanceof String && ((String) var).length() >= 14) {
            return ((String) var).replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return null;
    }

    @Override
    public String getName() {
        return "cnpjformat";
    }
}
