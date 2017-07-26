package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "brand_flag")
public class BrandFlag  implements Serializable {

    public static final long serialVersionUID = 1L;

    public BrandFlag(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="name")
    @JsonView({Views.Public.class,Views.List.class})
    private String name;

    @Column(name="description")
    @JsonView({Views.Public.class})
    private String description;
}
