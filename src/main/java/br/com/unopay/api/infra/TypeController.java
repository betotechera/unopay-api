package br.com.unopay.api.infra;

import br.com.unopay.api.bacen.model.GatheringChannel;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.Segment;
import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.billing.creditcard.model.CardBrand;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.remittance.model.RemittanceSituation;
import br.com.unopay.api.credit.model.ContractorInstrumentCreditType;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.market.model.BonusSituation;
import br.com.unopay.api.model.AuthorizationSituation;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.CompanyActivity;
import br.com.unopay.api.model.CompanyType;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.DocumentCaveat;
import br.com.unopay.api.model.DocumentSituation;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.IssueInvoiceType;
import br.com.unopay.api.model.PaymentInstrumentSituation;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.PaymentSource;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.ProductSituation;
import br.com.unopay.api.model.ReasonReceiptSituation;
import br.com.unopay.api.model.ReceiptSituation;
import br.com.unopay.api.model.ReceiptStep;
import br.com.unopay.api.model.RegistryEntity;
import br.com.unopay.api.model.Relatedness;
import br.com.unopay.api.model.State;
import br.com.unopay.api.network.model.BranchSituation;
import br.com.unopay.api.network.model.EstablishmentType;
import br.com.unopay.api.network.model.ServicePeriodSituation;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.network.model.Weekday;
import br.com.unopay.api.order.model.OrderType;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.uaa.model.RequestOrigin;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.unopay.api.infra.Endpoint.COMPANY_ACTIVITIES;
import static br.com.unopay.api.infra.Endpoint.COMPANY_TYPES;
import static br.com.unopay.api.infra.Endpoint.CONTRACT_SITUATIONS_TYPE;
import static br.com.unopay.api.infra.Endpoint.CREDIT_INSERTION_TYPES;
import static br.com.unopay.api.infra.Endpoint.ESTABLISHMENT_TYPES;
import static br.com.unopay.api.infra.Endpoint.PAYMENT_INSTRUMENT_SITUATION_TYPES;
import static br.com.unopay.api.infra.Endpoint.PERSON_TYPES;
import static br.com.unopay.api.infra.Endpoint.REGISTRY_ENTITIES;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
@Timed(prefix = "api")
public class TypeController {

    @RequestMapping(value = PERSON_TYPES, method = GET)
    PersonType[] getPersonTypes() {
        return PersonType.values();
    }

    @RequestMapping(value = COMPANY_ACTIVITIES, method = GET)
    CompanyActivity[] getCompanyActivities() {
        return CompanyActivity.values();
    }

    @RequestMapping(value = COMPANY_TYPES, method = GET)
    CompanyType[] getCompanyTypes() {
        return CompanyType.values();
    }

    @RequestMapping(value = REGISTRY_ENTITIES, method = GET)
    RegistryEntity[] getRegistryEntities() {
        return RegistryEntity.values();
    }

    @RequestMapping(value = Endpoint.STATES, method = GET)
    State[] getStates() {
        return State.values();
    }

    @RequestMapping(value = Endpoint.DOCUMENT_TYPES, method = GET)
    DocumentType[] getDocumentTypes() {
        return DocumentType.values();
    }

    @RequestMapping(value = Endpoint.PURPOSES, method = GET)
    Purpose[] getPurposes() {
        return Purpose.values();
    }

    @RequestMapping(value = Endpoint.SCOPES, method = GET)
    Scope[] getScopes() {
        return Scope.values();
    }

    @RequestMapping(value = Endpoint.USER_RELATIONSHIPS, method = GET)
    UserRelationship[] getUserRelationships() {
        return UserRelationship.values();
    }

    @RequestMapping(value = Endpoint.SERVICE_TYPES, method = GET)
    ServiceType[] getServiceTypes() {
        return Stream.of(ServiceType.values())
                .filter(type-> Segment.HEALTH.equals(type.getSegment())).toArray(ServiceType[]::new);
    }

    @RequestMapping(value = ESTABLISHMENT_TYPES, method = GET)
    EstablishmentType[] getEstablishmentTypes() {
        return Stream.of(EstablishmentType.values())
                .filter(type-> Segment.HEALTH.equals(type.getSegment())).toArray(EstablishmentType[]::new);
    }

    @RequestMapping(value = CONTRACT_SITUATIONS_TYPE, method = GET)
    ContractSituation[] getContractSituations() {
        return ContractSituation.values();
    }

    @RequestMapping(value = CREDIT_INSERTION_TYPES, method = GET)
    CreditInsertionType[] getCreditInsertionTypes() {
        return CreditInsertionType.values();
    }

    @RequestMapping(value = PAYMENT_INSTRUMENT_SITUATION_TYPES, method = GET)
    PaymentInstrumentSituation[] getPaymentInstrumentSituations() {
        return PaymentInstrumentSituation.values();
    }

    @RequestMapping(value = Endpoint.PAYMENT_INSTRUMENT_TYPES, method = GET)
    PaymentInstrumentType[] getPaymentInstrumentTypes() {
        return PaymentInstrumentType.values();
    }

    @RequestMapping(value = Endpoint.PRODUCT_SITUATIONS_TYPES, method = GET)
    ProductSituation[] getProductSituations() {
        return ProductSituation.values();
    }

    @RequestMapping(value = Endpoint.CREDIT_SITUATIONS_TYPES, method = GET)
    CreditSituation[] getCreditSituations() {
        return CreditSituation.values();
    }

    @RequestMapping(value = Endpoint.RECEIPT_STEP_TYPES, method = GET)
    ReceiptStep[] getReceiptSteps() {
        return ReceiptStep.values();
    }

    @RequestMapping(value = Endpoint.RECEIPT_SITUATION_TYPES, method = GET)
    ReceiptSituation[] getReceiptSituations() {
        return ReceiptSituation.values();
    }

    @RequestMapping(value = Endpoint.REASON_RECEIPT_SITUATION_TYPES, method = GET)
    ReasonReceiptSituation[] getReasonReceiptSituations() {
        return ReasonReceiptSituation.values();
    }

    @RequestMapping(value = Endpoint.PAYMENT_SOURCE_TYPES, method = GET)
    PaymentSource[] getPaymentSources() {
        return PaymentSource.values();
    }

    @RequestMapping(value = Endpoint.DOCUMENT_CAVEAT_TYPES, method = GET)
    DocumentCaveat[] getDocumentCaveats() {
        return DocumentCaveat.values();
    }

    @RequestMapping(value = Endpoint.GATHERING_CHANNELS_TYPES, method = GET)
    GatheringChannel[] gatheringChannels() {
        return GatheringChannel.values();
    }

    @RequestMapping(value = Endpoint.DOCUMENT_SITUATION_TYPES, method = GET)
    DocumentSituation[] documentSituations() {
        return DocumentSituation.values();
    }

    @RequestMapping(value = Endpoint.BATCH_CLOSING_SITUATION_TYPES, method = GET)
    BatchClosingSituation[] batchClosingSituation() {
        return BatchClosingSituation.values();
    }

    @RequestMapping(value = Endpoint.ISSUE_INVOICE_TYPES, method = GET)
    IssueInvoiceType[] issueInvoiceType() {
        return IssueInvoiceType.values();
    }

    @RequestMapping(value = Endpoint.TRANSACTION_SITUATION_TYPES, method = GET)
    AuthorizationSituation[] authorizationSituation() {
        return AuthorizationSituation.values();
    }

    @RequestMapping(value = Endpoint.REMITTANCE_SITUATION_TYPES, method = GET)
    RemittanceSituation[] remittanceSituations() {
        return RemittanceSituation.values();
    }

    @RequestMapping(value = "/types/segments", method = GET)
    Segment[] segment() {
        return Segment.values();
    }

    @RequestMapping(value = "/types/genders", method = GET)
    Gender[] gender() {
        return Gender.values();
    }

    @RequestMapping(value = "/types/orders/status", method = GET)
    PaymentStatus[] orderStatus() {
        return PaymentStatus.values();
    }

    @RequestMapping(value = "/types/orders/types", method = GET)
    OrderType[] orderType() {
        return OrderType.values();
    }

    @RequestMapping(value = "/types/billing/payment-methods", method = GET)
    PaymentMethod[] paymentMethodsType() {
        return PaymentMethod.values();
    }

    @RequestMapping(value = "/types/card-brands", method = GET)
    CardBrand[] cardBrand() {
        return CardBrand.values();
    }

    @RequestMapping(value = "/types/request-origins", method = GET)
    RequestOrigin[] requestOrigin() {
        return RequestOrigin.values();
    }


    @RequestMapping(value = "/types/relatednesss", method = GET)
    Relatedness[] relatedness() {
        return Relatedness.values();
    }

    @RequestMapping(value = "/types/hirer-negotiation-billings/status", method = GET)
    PaymentStatus[] hirerNegotiationBillingStatus() {
        return PaymentStatus.values();
    }

    @RequestMapping(value = "/types/contractor-credits", method = GET)
    ContractorInstrumentCreditType[] contractorInstrumentCreditTypes() {
        return ContractorInstrumentCreditType.values();
    }

    @RequestMapping(value = "/types/contractor-bonuses/situations", method = GET)
    BonusSituation[] bonusSituations() {
        return BonusSituation.values();
    }

    @RequestMapping(value = "/types/branches/situations", method = GET)
    BranchSituation[] branchSituation() {
        return BranchSituation.values();
    }

    @RequestMapping(value = "/types/service-period/situations", method = GET)
    ServicePeriodSituation[] servicePeriodSituation() {
        return ServicePeriodSituation.values();
    }

    @RequestMapping(value = "/types/weekdays", method = GET)
    Weekday[] weekday() {
        return Weekday.values();
    }
}
