package br.com.unopay.api.market.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.Updatable;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import java.io.Serializable;

@Data
@Entity
@Table(name = "contractor_bonus_billing")
public class ContractorBonusBilling implements Serializable, Updatable {

    private static final long serialVersionUID = 2732233885546623588L;

    public ContractorBonusBilling() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Valid
    @ManyToOne
    @JoinColumn(name = "bonus_billing_id")
    private BonusBilling bonusBilling;


    @Valid
    @ManyToOne
    @JoinColumn(name = "contractor_bonus_id")
    private Contractor contractor;
}
