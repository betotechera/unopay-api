package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.NegotiationBilling
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
    @Autowired
    private ContractInstallmentService installmentService

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
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        installmentService.create(contract)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findByHirer(negotiation.hirerId())

        then:
        found
    }

    def "given valid negotiation with first billing process should create billing with firs installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        installmentService.create(contract)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findByHirer(negotiation.hirerId())

        then:
        found.installmentNumber == 1
    }


    def "given valid negotiation when process should create billing with negotiation payment day as installment expiration"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        installmentService.create(contract)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findByHirer(negotiation.hirerId())

        then:
        def expectedDate = new DateTime().withDayOfMonth(negotiation.paymentDay).minusDays(hirerBillingToleranceDays)
        timeComparator.compare(found.installmentExpiration, expectedDate) == 0
    }

    def """given negotiation and contract with installments when process
            should create billing with negotiation payment day as installment expiration"""(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        installmentService.create(contract)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findByHirer(negotiation.hirerId())

        then:
        def memberSum = negotiation.defaultMemberCreditValue + negotiation.installmentValueByMember
        def total = negotiation.defaultCreditValue + negotiation.installmentValue + memberSum
        found.value == Rounder.round(total)
    }

    def """given negotiation and contract with installments when process
            should create billing with billing details"""(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        installmentService.create(contract)

        when:
        service.process(negotiation.hirerId())
        NegotiationBilling found = service.findByHirer(negotiation.hirerId())
        def details = billingDetailService.findByBillingId(found.id)

        then:
        that details, hasSize(1)
    }

    def "given negotiation without contract should not be processed"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()

        when:
        service.process(negotiation.hirerId())
        service.findByHirer(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }

    def "given negotiation and contract without installments should not be processed"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)

        when:
        service.process(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENTS_NOT_FOUND'
    }
}
