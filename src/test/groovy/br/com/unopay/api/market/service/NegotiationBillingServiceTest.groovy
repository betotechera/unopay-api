package br.com.unopay.api.market.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.credit.service.CreditService
import static br.com.unopay.api.function.FixtureFunctions.*
import br.com.unopay.api.function.FixtureFunctions
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.market.model.PaymentDayCalculator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.util.Rounder
import br.com.unopay.bootcommons.exception.NotFoundException
import static org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Months
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import static spock.util.matcher.HamcrestSupport.that

class NegotiationBillingServiceTest extends SpockApplicationTests{

    public static final int FIRST_PAYMENT_DAY_OF_NEX_TMONTH = 1
    @Autowired
    private NegotiationBillingService service
    @Autowired
    private NegotiationBillingDetailService billingDetailService
    @Autowired
    private CreditService creditService
    @Autowired
    private HirerNegotiationService hirerNegotiationService
    @Autowired
    private FixtureCreator fixtureCreator

    private Notifier notifierMock = Mock(Notifier)

    def setup(){
        service.notifier = notifierMock
    }

    @Value("\${unopay.boleto.deadline_in_days}")
    private Integer ticketDeadLineInDays

    def "given valid negotiation billing should be created"(){
        given:
        NegotiationBilling billing = Fixture.from(NegotiationBilling).gimme("valid", new Rule(){{
            add("hirerNegotiation", fixtureCreator.createNegotiation())
            add("number", regex("\\d{20}"))
        }})

        when:
        NegotiationBilling created = service.save(billing)
        NegotiationBilling found = service.findById(created.id)

        then:
        found
    }


    def "given known negotiations when process should create billings"(){
        given:
        def negotiationA = fixtureCreator.createNegotiation()
        def negotiationB = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiationA.product,negotiationA.hirer)
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiationB.product,negotiationB.hirer)

        when:
        service.process()
        NegotiationBilling foundA = service.findLastNotPaidByHirer(negotiationA.hirerId())
        NegotiationBilling foundB = service.findLastNotPaidByHirer(negotiationB.hirerId())

        then:
        foundA
        foundB
    }

    def "given known negotiations with known billing for current installment when process should not create billing"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)
        service.process(negotiation.getId())

        when:
        service.process()
        def billings = service.findByHirerId(negotiation.hirerId())

        then:
        that billings, hasSize(1)
    }


    def "given known negotiation with effective date in the future when process should not create billings"(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("effectiveDate", instant("one day from now"))
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)

        when:
        service.process()
        service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }

    def """given known negotiation with payment day after ticket deadline and past effective date
            when process should not create billings"""(){
        given:
        def delay = 1
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("paymentDay", getNear(delay))
            add("effectiveDate", instant("one day ago"))
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)

        when:
        service.process()
        service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }

    def """given known negotiation with payment day equals ticket deadline and past effective date
            when process should create billings"""(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("paymentDay", getNear())
            add("effectiveDate", instant("one day ago"))
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)

        when:
        service.process()
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found
    }

    def """given known negotiation with payment day not equals ticket deadline and effective date equals ticket deadline
            when process should create billings"""(){
        given:
        def delay = 1
        def effectiveDate = getNear() == FIRST_PAYMENT_DAY_OF_NEX_TMONTH ? new DateTime()
                .plusMonths(1).withDayOfMonth(1).toDate() : instant("${ticketDeadLineInDays} days from now");
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("paymentDay", getNear(delay))
            add("effectiveDate", effectiveDate)
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)

        when:
        service.process()
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found
    }

    def """given known negotiation with payment day not equals ticket deadline and
            effective date not equals ticket deadline when process should not create billings"""(){
        given:
        def delay = 1
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("paymentDay", getNear(delay))
            add("effectiveDate", instant("${ticketDeadLineInDays + delay} days from now"))
            add("installments", 2)
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),negotiation.product,negotiation.hirer)

        when:
        service.process()
        service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }


    def "when process billing should create ticket"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())

        then:
        1 * notifierMock.notify(Queues.HIRER_BILLING_CREATED,_)
    }

    def "given valid negotiation when process for hirer should create billing"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found
    }


    def "given known negotiation when process as Paid should update status to paid"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)
        service.process(negotiation.getId())
        NegotiationBilling foundBefore = service.findLastNotPaidByHirer(negotiation.hirerId())

        when:
        service.processAsPaid(foundBefore.getId())
        NegotiationBilling foundAfter = service.findById(foundBefore.getId())

        then:
        foundBefore.status == PaymentStatus.WAITING_PAYMENT
        foundAfter.status == PaymentStatus.PAID
    }

    def "given known negotiation when process as Paid should update negotiation to active"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        negotiation.setActive(Boolean.FALSE)
        hirerNegotiationService.save(negotiation)
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)
        service.process(negotiation.getId())
        NegotiationBilling billing = service.findLastNotPaidByHirer(negotiation.hirerId())

        when:
        service.processAsPaid(billing.getId())

        HirerNegotiation negotiationFound = hirerNegotiationService.findById(negotiation.getId())

        then:
        negotiationFound.active
    }

    def "given valid negotiation when process for hirer should be created with number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.number
    }

    def "given valid negotiation with first billing process should create billing with firs installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.installmentNumber == 1
    }

    def "given previous paid billing when process should create billing with next installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)
        service.process(negotiation.getId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.getId())
        NegotiationBilling next = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        next.installmentNumber == 2
    }


    def "given more one paid billing when process should create billing with next installment number"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        service.process(negotiation.getId())
        paidBilling(negotiation.hirerId())

        service.process(negotiation.getId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.getId())
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
        service.process(negotiation.getId())
        paidBilling(negotiation.hirerId())

        service.process(negotiation.getId())
        paidBilling(negotiation.hirerId())

        when:
        service.process(negotiation.getId())

        service.findLastNotPaidByHirer(negotiation.hirerId())
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'HIRER_NEGOTIATION_BILLING_NOT_FOUND'
    }

    def "given effective date in the future when process should create billing effective date as installment expiration"(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("effectiveDate", new DateTime().plusMonths(1).withMillisOfDay(0).toDate())
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        def found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        timeComparator.compare(found.installmentExpiration, negotiation.effectiveDate) == 0
    }

    def """given negotiation with past effective date when process
        should create billing with negotiation payment day as installment expiration"""(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("effectiveDate", new DateTime().minusDays(1).toDate())
        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def expectedDate = new DateTime().withDayOfMonth(negotiation.paymentDay).toDate()
        timeComparator.compare(found.installmentExpiration, expectedDate) == 0
    }

    def "given negotiation and contract with installments when process should create billing right value"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        fixtureCreator.createAuthorizedMemberForContract(contract)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        def memberSum = negotiation.defaultMemberCreditValue + negotiation.installmentValueByMember
        def total = negotiation.defaultCreditValue + negotiation.installmentValue + memberSum
        found.value == Rounder.round(total)
    }

    def 'should negotiation with billing credits should be created with credit'(){
        given:
        BigDecimal memberCreditValueUnderTest = memberCreditValue
        BigDecimal installmentValueUnderTest = installmentValue
        BigDecimal installmentValueByMemberUnderTest = installmentValueByMember
        BigDecimal creditValueUnderTest = creditValue
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("defaultMemberCreditValue",memberCreditValueUnderTest)
            add("installmentValue",installmentValueUnderTest)
            add("installmentValueByMember",installmentValueByMemberUnderTest)
            add("defaultCreditValue",creditValueUnderTest)
            add("billingWithCredits", Boolean.TRUE)

        }})
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        (1..members).each {
            fixtureCreator.createAuthorizedMemberForContract(contract)
        }

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())
        def credits = creditService.findForHirer(negotiation.hirerId())

        then:
        that credits, hasSize(1)
        found.credit
        found.credit.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 7
        10                | 2       | 20                       | 3                | 5           | 25
        3                 | 8       | 24                       | 5                | 8           | 32
        5.3               | 4       | 21.2                     | 6                | 9           | 30.2
    }

    def 'should negotiation without billing credits should be created without credit'(){
        given:
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("freeInstallmentQuantity", 0)
            add("billingWithCredits", Boolean.FALSE)

        }})
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())
        def credits = creditService.findForHirer(negotiation.hirerId())
        then:
        found
        !found.credit
        that credits, hasSize(0)
    }

    def 'should negotiation without free installments and with billing credits should be created with full value'(){
        given:
        BigDecimal memberCreditValueUnderTest = memberCreditValue
        BigDecimal installmentValueUnderTest = installmentValue
        BigDecimal installmentValueByMemberUnderTest = installmentValueByMember
        BigDecimal creditValueUnderTest = creditValue
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("defaultMemberCreditValue",memberCreditValueUnderTest)
            add("installmentValue",installmentValueUnderTest)
            add("installmentValueByMember",installmentValueByMemberUnderTest)
            add("defaultCreditValue",creditValueUnderTest)
            add("billingWithCredits", Boolean.TRUE)

        }})
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        (1..members).each {
            fixtureCreator.createAuthorizedMemberForContract(contract)
        }

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 16
        10                | 2       | 20                       | 3                | 5           | 68
        3                 | 8       | 24                       | 5                | 8           | 229
        5.3               | 4       | 21.2                     | 6                | 9           | 121
    }

    def 'should negotiation with free installments and with billing credits should be created without installment value'(){
        BigDecimal memberCreditValueUnderTest = memberCreditValue
        BigDecimal installmentValueUnderTest = installmentValue
        BigDecimal installmentValueByMemberUnderTest = installmentValueByMember
        BigDecimal creditValueUnderTest = creditValue
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("defaultMemberCreditValue",memberCreditValueUnderTest)
            add("installmentValue",installmentValueUnderTest)
            add("installmentValueByMember",installmentValueByMemberUnderTest)
            add("defaultCreditValue",creditValueUnderTest)
            add("freeInstallmentQuantity", 20)
            add("billingWithCredits", Boolean.TRUE)

        }})
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        (1..members).each {
            fixtureCreator.createAuthorizedMemberForContract(contract)
        }

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 7
        10                | 2       | 20                       | 3                | 5           | 25
        3                 | 8       | 24                       | 5                | 8           | 32
        5.3               | 4       | 21.2                     | 6                | 9           | 30.2
    }

    def 'should negotiation without free installments and without billing credits should be created without credit value'(){
        BigDecimal memberCreditValueUnderTest = memberCreditValue
        BigDecimal installmentValueUnderTest = installmentValue
        BigDecimal installmentValueByMemberUnderTest = installmentValueByMember
        BigDecimal creditValueUnderTest = creditValue
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirer", fixtureCreator.createHirer())
            add("product", fixtureCreator.createProduct())
            add("defaultMemberCreditValue",memberCreditValueUnderTest)
            add("installmentValue",installmentValueUnderTest)
            add("installmentValueByMember",installmentValueByMemberUnderTest)
            add("defaultCreditValue",creditValueUnderTest)
            add("freeInstallmentQuantity", 0)
            add("billingWithCredits", Boolean.FALSE)

        }})
        def contract = fixtureCreator
                .createPersistedContract(fixtureCreator.createContractor(), negotiation.product, negotiation.hirer)
        (1..members).each {
            fixtureCreator.createAuthorizedMemberForContract(contract)
        }

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())

        then:
        found.value == value as BigDecimal

        where:
        memberCreditValue | members | installmentValueByMember | installmentValue | creditValue | value
        5                 | 1       | 5                        | 4                | 2           | 9
        10                | 2       | 20                       | 3                | 5           | 43
        3                 | 8       | 24                       | 5                | 8           | 197
        5.3               | 4       | 21.2                     | 6                | 9           | 90.8
    }

    def """given negotiation and contract with installments when process
            should create billing with billing details"""(){
        given:
        def negotiation = fixtureCreator.createNegotiation()
        fixtureCreator.createPersistedContract(fixtureCreator.createContractor(), negotiation.product,negotiation.hirer)

        when:
        service.process(negotiation.getId())
        NegotiationBilling found = service.findLastNotPaidByHirer(negotiation.hirerId())
        def details = billingDetailService.findByBillingId(found.id)

        then:
        that details, hasSize(1)
    }

    def "given negotiation without contract should not be processed"(){
        given:
        def negotiation = fixtureCreator.createNegotiation()

        when:
        service.process(negotiation.getId())
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


    private getNear(delay=0){
        def maxPaymentDay = 28
        def currentDay = new DateTime().dayOfMonth().get()
        def nearPaymentDay = currentDay + ticketDeadLineInDays + delay
        return  nearPaymentDay > maxPaymentDay ? FIRST_PAYMENT_DAY_OF_NEX_TMONTH + delay : nearPaymentDay
    }


}
