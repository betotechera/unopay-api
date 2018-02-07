package br.com.unopay.api.bacen.model;

import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Reference;
import br.com.unopay.api.model.validation.group.Update;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "authorized_member")
public class AuthorizedMember implements Serializable{

    public static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @NotNull(groups = {Reference.class})
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="birth_date")
    @NotNull(groups = {Create.class, Update.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date birthDate;

    @Column(name="name")
    @NotNull(groups = {Create.class, Update.class})
    @Size(max=256)
    private String name;

    @Column(name="gender")
    @NotNull(groups = {Create.class, Update.class})
    @Size(max=50)
    private String gender;

    @Column(name="relatedness")
    @NotNull(groups = {Create.class, Update.class})
    @Size(max=50)
    private String relatedness;

    @Column(name="email")
    @Size(max=256)
    private String email;

    @Valid
    @Embedded
    private Document document;
}
