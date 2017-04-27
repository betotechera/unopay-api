package br.com.unopay.api.filter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = GenericEnumDeserializer.class)
@JsonSerialize(using = GenericEnumSerializer.class)
public interface DescriptibleEnum {

    String getDescription();

    String name();
}
