package br.com.unopay.api.network.model.filter;

import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import java.io.Serializable;
import lombok.Data;

@Data
public class EventFilter  implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "service.name")
    private String serviceName;

    @SearchableField(field = "service.type")
    private ServiceType serviceType;

    @SearchableField
    private String name;

    @SearchableField
    private String ncmCode;

}
