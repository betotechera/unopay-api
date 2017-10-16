package br.com.unopay.api.uaa.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GroupFilter {

    @SearchableField(field = "userType.id")
    private String userTypeId;

}
