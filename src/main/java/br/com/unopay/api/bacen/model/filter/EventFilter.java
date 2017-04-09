package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class EventFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "service.name")
    private String serviceName;

    @SearchableField
    private String name;

    @SearchableField
    private String ncmCode;

}
