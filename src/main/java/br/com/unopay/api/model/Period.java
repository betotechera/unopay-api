package br.com.unopay.api.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class Period implements Serializable{

    public static final long serialVersionUID = 1L;

    public Period(){}

    public Period(Date begin, Date end){
        this.begin = ObjectUtils.clone(begin);
        this.end = ObjectUtils.clone(end);
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date begin;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date end;


    public void setBegin(Date dateTime){
        this.begin = ObjectUtils.clone(dateTime);
    }

    public Date getBegin(){
        return ObjectUtils.clone(this.begin);
    }

    public void setEnd(Date dateTime){
        this.end = ObjectUtils.clone(dateTime);
    }

    public Date getEnd(){
        return ObjectUtils.clone(this.end);
    }
}
