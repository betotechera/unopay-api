package br.com.unopay.api.bacen.model;

import br.com.unopay.api.repository.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String code;

    @SearchableField
    private String name;
}
