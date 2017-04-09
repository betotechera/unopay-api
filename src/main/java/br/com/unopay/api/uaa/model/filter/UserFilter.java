package br.com.unopay.api.uaa.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;
import lombok.Data;

@Data
public class UserFilter {

    @SearchableField
    private String name;

    @SearchableField
    private String email;

    @SearchableField(field = "groups.name")
    private String groupName;
}
