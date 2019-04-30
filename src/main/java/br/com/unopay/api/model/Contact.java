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
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "contact")
public class Contact  implements Serializable {

    public static final long serialVersionUID = 1L;

    public Contact(){}

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @Column(name="name")
    @JsonView({Views.Establishment.Contact.class})
    private String name;

    @Column(name="mail")
    @JsonView({Views.Establishment.Contact.class})
    private String mail;

    @Column(name = "cell_phone")
    @JsonView({Views.Establishment.Contact.class})
    private String cellPhone;

    @Column(name = "phone")
    @JsonView({Views.Establishment.Contact.class})
    private String phone;

    @Column(name = "office")
    @JsonView({Views.Establishment.Contact.class})
    private String office;

    @Column(name = "whatsapp")
    @JsonView({Views.Establishment.Contact.class})
    private String whatsapp;


}
