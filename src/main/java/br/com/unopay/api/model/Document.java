package br.com.unopay.api.model;

import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class Document {

    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(STRING)
    @Column(name="document_type")
    @JsonView({Views.Public.class,Views.List.class})
    private DocumentType type;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name="document_number")
    @JsonView({Views.Public.class,Views.List.class})
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String number;

    @Enumerated(STRING)
    @Column(name="registry_entity")
    @JsonView({Views.Public.class})
    private RegistryEntity registryEntity;
}
