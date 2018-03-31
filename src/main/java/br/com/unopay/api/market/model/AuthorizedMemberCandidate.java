package br.com.unopay.api.market.model;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.Relatedness;
import br.com.unopay.api.model.Updatable;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "authorized_member_candidate")
public class AuthorizedMemberCandidate implements Serializable, Updatable{

    public static final long serialVersionUID = 1L;
    public static final int YEAR_LIMIT = 150;

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="birth_date")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    private Date birthDate;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.List.class})
    @Size(max=256)
    private String name;

    @Enumerated(STRING)
    @Column(name="gender")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.Detail.class})
    private Gender gender;

    @Enumerated(STRING)
    @Column(name="relatedness")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.AuthorizedMember.Detail.class})
    private Relatedness relatedness;

    @Column(name="email")
    @JsonView({Views.AuthorizedMember.List.class})
    @Size(max=256)
    private String email;

    @Valid
    @JsonView({Views.AuthorizedMember.List.class})
    @Embedded
    private Document document;

    @Version
    @JsonIgnore
    private Integer version;

    public AuthorizedMember toAuthorizedMember(Contract contract){
        AuthorizedMember authorizedMember = new AuthorizedMember();
        authorizedMember.setName(this.getName());
        authorizedMember.setEmail(this.getEmail());
        authorizedMember.setBirthDate(this.getBirthDate());
        authorizedMember.setContract(contract);
        authorizedMember.setDocument(this.getDocument());
        authorizedMember.setGender(this.getGender());
        authorizedMember.setRelatedness(this.getRelatedness());
        return authorizedMember;
    }
}
