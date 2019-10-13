package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.market.model.HirerNegotiation
import br.com.unopay.api.util.Rounder
import br.com.unopay.api.util.Time
import org.joda.time.DateTime

class ContractInstallmentTest extends FixtureApplicationTest {

    Date currentDate

    def setup(){
        currentDate = new DateTime().withDayOfMonth(20).toDate()
    }

    def 'when create from contract and negotiation the value should be the annuity by payment installments from negotiation'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        installment.value == Rounder.round(negotiation.installmentValue)
    }

    def 'when create from contract and negotiation  the contract should be the contract param'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        installment.contract.id == contract.id
    }


    def 'when create from contract and negotiation with free installment should be created with free installment'(){
        given:
        def freeInstallments = value
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("freeInstallmentQuantity", freeInstallments)
        }})

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        installment.value == 0.0

        where:
        _ | value
        _ | 1
        _ | 2
        _ | 20
    }

    def """"when create from contract and negotiation with past effective date
        should be created with one installment nummber"""(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("freeInstallmentQuantity", 0)
            add("effectiveDate", instant("5 months ago"))
        }})

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        installment.installmentNumber == ContractInstallment.ONE_INSTALLMENT
    }

    def """"when create from contract and negotiation with effective date in the future
        should be created with effective date as expiration date """(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("freeInstallmentQuantity", 0)
            add("effectiveDate", instant("one month from now"))
        }})

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        timeComparator.compare(installment.expiration, instant("one month from now")) == 0
    }

    def """"when create from contract and negotiation with past effective date
        should be created with current date as expiration date """(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        HirerNegotiation negotiation = Fixture.from(HirerNegotiation.class).gimme("valid", new Rule(){{
            add("freeInstallmentQuantity", 0)
            add("effectiveDate", instant("2 months ago"))
        }})

        when:
        def installment = new ContractInstallment(contract, negotiation,currentDate)

        then:
        timeComparator.compare(installment.expiration, currentDate) == 0
    }

    def 'when create from contract the value should be the annuity by payment installments'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        installment.value == Rounder.round(contract.annuityTotal() / contract.paymentInstallments)
    }

    def 'when create from contract the contract should be the contract param'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        installment.contract.id == contract.id
    }

    def 'when create from contract the number should be one'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        installment.installmentNumber == 1
    }

    def 'when create from contract the expiration should be now'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        timeComparator.compare(installment.expiration, contract.begin)
    }

    def 'when increment expiration and today is after day 27 should be incremented with one month and at the end of the month'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        def currentDate =  new DateTime().withDayOfMonth(28).toDate()
        contract.setBegin(currentDate)

        when:
        def contractInstallment = new ContractInstallment(contract)
        Date expiration = contractInstallment.expiration

        then:
        def excitations = (1..contract.paymentInstallments).collect {

            def installment = new ContractInstallment(contract)
            installment.plusOneMonthInExpiration(expiration)
            expiration = installment.expiration
            return expiration
        }
        excitations.add(0, contractInstallment.expiration)
        int number = 0
        excitations.every {
            number++;
            it == Time.createDateTime().plusMonths(number).dayOfMonth().withMaximumValue().toDate()
        }
    }

    def 'when increment expiration and today is before day 28 should be incremented with one month'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        def currentDate =  new DateTime().withDayOfMonth(27).toDate()
        contract.setBegin(currentDate)
        when:
        def contractInstallment = new ContractInstallment(contract)
        Date expiration = contractInstallment.expiration

        then:
        def excitations = (1..contract.paymentInstallments).collect {
            def installment = new ContractInstallment(contract)
            installment.plusOneMonthInExpiration(expiration)
            expiration = installment.expiration
            return expiration
        }
        excitations.add(0, contractInstallment.expiration)
        int number = 0
        excitations.every {
            number++;
            timeComparator.compare(it,Time.createDateTime(currentDate).plusMonths(number)) == 0
        }
    }

    def 'given a contract without membership fee when increment should create first installment with now expiration'(){
        given:
        BigDecimal membershipFee = null
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("membershipFee", membershipFee)
            add("begin", currentDate)
        }})

        when:
        def contractInstallment = new ContractInstallment(contract)

        then:
        timeComparator.compare(contractInstallment.expiration, currentDate) == 0
    }

    def 'given a contract with membership fee when increment should create first installment with one month expiration'(){
        given:
        BigDecimal membershipFee = 20.0
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("membershipFee", membershipFee)
            add("begin", currentDate)
        }})

        when:
        def contractInstallment = new ContractInstallment(contract)

        then:
        timeComparator.compare(contractInstallment.expiration,
                Time.createDateTime(currentDate).plusMonths(1)) == 0
    }

    def 'installment number should be incremented'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("begin", currentDate)
        }})

        when:
        def contractInstallment = new ContractInstallment(contract)

        then:
        int nextNumber = contractInstallment.installmentNumber
        (0..contract.paymentInstallments).every {
            def installment = new ContractInstallment(contract)
            installment.incrementNumber(nextNumber)
            nextNumber++
            installment.installmentNumber == nextNumber
        }
    }

    def 'should be equals'(){
        given:
        ContractInstallment a = Fixture.from(ContractInstallment.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'(){
        List list = Fixture.from(ContractInstallment.class).gimme(2,"valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals
    }

}