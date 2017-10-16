package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServiceFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private Integer code;

    @SearchableField
    private String name;

    public ServiceFilter() {}
}
