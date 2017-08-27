package br.com.unopay.api.uaa.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_type")
@Data
public class UserType implements Serializable {

    public static final long serialVersionUID = 1L;

    public UserType(){}

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    @NotNull(groups = Update.class)
    @Column(name="id")
    private String id;

    @NotNull(groups = Create.class)
    @Column(name="name", unique = true)
    private String name;

    @Column(name="description")
    private String description;
}
