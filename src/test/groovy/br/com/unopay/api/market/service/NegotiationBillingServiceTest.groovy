package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import static org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import static spock.util.matcher.HamcrestSupport.that

class NegotiationBillingServiceTest extends SpockApplicationTests{

    @Autowired
    private NegotiationBillingService service
    @Autowired
    private NegotiationBillingDetailService billingDetailService
    @Autowired
    private FixtureCreator fixtureCreator

    @Value("\${billing.hirer.tolerance.days}")
    private Integer hirerBillingToleranceDays

    def "given valid negotiation billing should be created"(){
        given:
        NegotiationBilling billing = Fixture.from(NegotiationBilling).gimme("valid", new Rule(){{
            add("hirerNegotiation", fixtureCreator.createNegotiation())
        }})

        when:
        NegotiationBilling created = service.save(billing)
        NegotiationBilling found = service.findById(created.id)

        then:
        found
    }

    def "given valid negotiation when process for hirer should create billing"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found
    }

    def "given valid negotiation with first billing process should create billing with firs installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.installmentNumber == 1
    }

    def "given previous paid billing when process should create billing with next installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)
        service.process(negotiation.hirerId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling next = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        next.installmentNumber == 2
    }


    def "given more one paid billing when process should create billing with next installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        service.process(negotiation.hirerId())
        paidBilling(negotiation.hirerId())

        service.process(negotiation.hirerId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling next = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        next.installmentNumber == 3
    }

    def "given last billing installment paid when process should not be processed"(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("effectiveDate", new Date())
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)
        service.process(negotiation.hirerId())
        paidBilling(negotiation.hirerId())

        service.process(negotiation.hirerId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.hirerId())

        service.findLastNotPaidByHirer(negotiation.hirerId())
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }


    def "given valid negotiation when process should create billing with negotiation payment day as installment expiration"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def expectedDate = new DateTime().withDayOfMonth(negotiation.paymentDay).minusDays(hirerBillingToleranceDays)
        timeComparator.compare(found.installmentExpiration, expectedDate) == 0
    }

    def """given negotiation and contract with installments when process
            should create billing with negotiation payment day as installment expiration"""(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def memberSum = negotiation.defaultMemberCreditValue + negotiation.installmentValueByMember
        def total = negotiation.defaultCreditValue + negotiation.installmentValue + memberSum
        found.value == Rounder.round(total)
    }

    def """given negotiation and contract with installments when process
            should create billing with billing details"""(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())
        def details = billingDetailService.findByBillingId(found.id)

        then:
        that details, hasSize(1)
    }

    def "given negotiation without contract should not be processed"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()

        when:
        service.process(negotiation.hirerId())
        service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }


    private void paidBilling(String  hirerId) {
        NegotiationBilling toPaid = service.findLastNotPaidByHirer(hirerId)
        toPaid.status = PaymentStatus.PAID
        service.save(toPaid)
    }


}
