package br.com.unopay.api.billing.boleto.santander.cobrancaonline.ymb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "faultInfo", propOrder = {
    "code",
    "message"
},namespace = "faultInfo")
public class FaultInfo implements Serializable{

    private static final long serialVersionUID = -4781206094318139949L;
    protected Integer code;
    protected String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer value) {
        this.code = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

}
