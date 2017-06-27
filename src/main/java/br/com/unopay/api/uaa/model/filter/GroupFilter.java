package br.com.unopay.api.uaa.model.filter;

import br.com.unopay.api.repository.filter.SearchableField;

public class GroupFilter {

    @SearchableField(field = "userType.id")
    private String userTypeId;

}
