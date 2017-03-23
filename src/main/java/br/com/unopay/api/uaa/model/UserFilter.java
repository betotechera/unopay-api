package br.com.unopay.api.uaa.model;

import br.com.unopay.api.repository.SearchableField;
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
