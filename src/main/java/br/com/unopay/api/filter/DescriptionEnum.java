package br.com.unopay.api.filter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = GenericEnumDeserializer.class)
@JsonSerialize(using = GenericEnumSerializer.class)
public interface DescriptionEnum {

    String getDescription();

    String name();
}
