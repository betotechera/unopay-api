package br.com.unopay.api.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
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
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;
}
