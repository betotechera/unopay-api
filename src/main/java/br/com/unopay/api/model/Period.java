package br.com.unopay.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class Period implements Serializable{

    public static final long serialVersionUID = 1L;

    public Period(){}

    private Date begin;
    private Date end;
}
