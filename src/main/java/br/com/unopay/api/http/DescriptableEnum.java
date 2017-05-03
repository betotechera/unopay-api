package br.com.unopay.api.http;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = GenericEnumDeserializer.class)
@JsonSerialize(using = GenericEnumSerializer.class)
public interface DescriptableEnum {

    String getDescription();

    String name();
}
