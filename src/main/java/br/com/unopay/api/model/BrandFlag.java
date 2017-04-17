package br.com.unopay.api.model;


import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;// NOSONAR
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "brand_flag")
public class BrandFlag  implements Serializable {

    public static final long serialVersionUID = 1L;
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
