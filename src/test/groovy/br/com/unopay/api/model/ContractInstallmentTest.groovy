package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.util.Rounder
import br.com.unopay.api.util.Time
import org.joda.time.DateTime

class ContractInstallmentTest extends FixtureApplicationTest {

    Date currentDate

    def setup(){
        currentDate = new DateTime().withDayOfMonth(20).toDate()
    }

    def 'when create from contract the value should be the annuity by payment installments'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract,currentDate)

        then:
        installment.value == Rounder.round(contract.annuity / contract.paymentInstallments)
    }

    def 'when create from contract the contract should be the contract param'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract,currentDate)

        then:
        installment.contract.id == contract.id
    }

    def 'when create from contract the number should be one'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract,currentDate)

        then:
        installment.installmentNumber == 1
    }

    def 'when create from contract the expiration should be now'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract,currentDate)

        then:
        timeComparator.compare(installment.expiration, new Date())
    }

    def 'when increment expiration and today is after day 27 should be incremented with one month and at the end of the month'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        def currentDate =  new DateTime().withDayOfMonth(28).toDate()
        when:
        def contractInstallment = new ContractInstallment(contract, currentDate)
        Date expiration = contractInstallment.expiration

        then:
        def excitations = (1..contract.paymentInstallments).collect {
            def installment = new ContractInstallment(contract, expiration)
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

        when:
        def contractInstallment = new ContractInstallment(contract,currentDate)
        Date expiration = contractInstallment.expiration

        then:
        def excitations = (1..contract.paymentInstallments).collect {
            def installment = new ContractInstallment(contract, expiration)
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
        }})

        when:
        def contractInstallment = new ContractInstallment(contract,currentDate)

        then:
        timeComparator.compare(contractInstallment.expiration, currentDate) == 0
    }

    def 'given a contract with membership fee when increment should create first installment with one month expiration'(){
        given:
        BigDecimal membershipFee = 20.0
        Contract contract = Fixture.from(Contract.class).gimme("valid", new Rule(){{
            add("membershipFee", membershipFee)
        }})

        when:
        def contractInstallment = new ContractInstallment(contract,currentDate)

        then:
        timeComparator.compare(contractInstallment.expiration,
                Time.createDateTime(currentDate).plusMonths(1)) == 0
    }

    def 'installment number should be incremented'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def contractInstallment = new ContractInstallment(contract,currentDate)

        then:
        int nextNumber = contractInstallment.installmentNumber
        (0..contract.paymentInstallments).every {
            def installment = new ContractInstallment(contract,currentDate)
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