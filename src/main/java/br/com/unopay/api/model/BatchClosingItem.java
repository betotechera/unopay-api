package br.com.unopay.api.model;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@EqualsAndHashCode
@Table(name = "batch_closing_item")
public class BatchClosingItem implements Serializable, Updatable {

    public static final long serialVersionUID = 1L;

    public BatchClosingItem(){}

    public BatchClosingItem(ServiceAuthorize serviceAuthorize){
        this.serviceAuthorize = serviceAuthorize;
        this.documentNumberInvoice = serviceAuthorize.getContract().getHirerDocumentNumber();
        this.invoiceDocumentSituation = DocumentSituation.PENDING;
        this.issueInvoiceType = serviceAuthorize.establishmentIssueInvoiceType();
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator="system-uuid")
    @JsonView({Views.Public.class,Views.List.class})
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String id;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="batch_closing_id")
    @JsonView({Views.Public.class,Views.List.class})
    private BatchClosing batchClosing;

    @ManyToOne
    @NotNull(groups = {Create.class, Update.class})
    @JoinColumn(name="service_authorize_id")
    @JsonView({Views.Public.class,Views.List.class})
    private ServiceAuthorize serviceAuthorize;

    @Column(name = "document_number_invoice")
    @NotNull(groups = {Create.class, Update.class})
    @JsonView({Views.Public.class})
    private String documentNumberInvoice;

    @Column(name = "invoice_number")
    @JsonView({Views.Public.class})
    private String invoiceNumber;

    @Column(name = "invoice_document_situation")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class})
    private DocumentSituation invoiceDocumentSituation;

    @Column(name = "invoice_document_uri")
    @JsonView({Views.Public.class})
    private String invoiceDocumentUri;

    @Column(name = "issue_invoice_type")
    @NotNull(groups = {Create.class, Update.class})
    @Enumerated(EnumType.STRING)
    @JsonView({Views.Public.class})
    private IssueInvoiceType issueInvoiceType;

    @JsonIgnore
    @Version
    private Integer version;

    public BigDecimal eventValue(){
        if(getServiceAuthorize() != null) {
            return getServiceAuthorize().getEventValue();
        }
        return null;
    }

    public BatchClosingItem cancelDocumentInvoice(){
        setInvoiceDocumentSituation(DocumentSituation.CANCELED);
        return this;
    }

    public ServiceAuthorize resetAuthorizeBatchClosingDate(){
        if(getServiceAuthorize() != null){
            return getServiceAuthorize().resetBatchClosingDate();
        }
        return null;
    }

}
