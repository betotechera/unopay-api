package br.com.unopay.api.uaa.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserFilter {

    @SearchableField
    private String name;

    @SearchableField
    private String email;

    @SearchableField(field = "groups.name")
    private String groupName;
}
