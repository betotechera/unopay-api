package br.com.unopay.api.function

import br.com.six2six.fixturefactory.function.impl.ChronicFunction

class FixtureFunctions {

    static Date instant(String pattern){
        new ChronicFunction(pattern).generateValue().getTime()
    }
}
