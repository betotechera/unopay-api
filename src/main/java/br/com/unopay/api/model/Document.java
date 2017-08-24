package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

import static javax.persistence.EnumType.STRING;

@Data
@Embeddable
public class Document implements Serializable {

    public static final long serialVersionUID = 1L;

    public Document(){}

    @Enumerated(STRING)
    @Column(name="document_type")
    private DocumentType type;

    @NotNull(groups = {Create.class, Update.class})
    @Column(name="document_number")
    @Size(min=2, max = 50, groups = {Create.class, Update.class})
    private String number;

    @Enumerated(STRING)
    @Column(name="registry_entity")
    @JsonView({Views.Person.class})
    private RegistryEntity registryEntity;
}
