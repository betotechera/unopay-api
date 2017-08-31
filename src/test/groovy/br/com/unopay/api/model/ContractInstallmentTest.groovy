package br.com.unopay.api.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.util.Rounder

class ContractInstallmentTest extends FixtureApplicationTest {

    def 'when create from contract the value should be the annuity by payment installments'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        installment.value == Rounder.round(contract.annuity / contract.paymentInstallments)
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

    def 'when create from contract the expiration should be after one month'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def installment = new ContractInstallment(contract)

        then:
        installment.expiration > instant("one month from now at 00:01 am")
        installment.expiration < instant("one month from now at 23:59 pm")
    }

    def 'when increment expiration should be incremented with one month'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

        when:
        def contractInstallment = new ContractInstallment(contract)

        then:
        Date expiration = contractInstallment.expiration
        (0..contract.paymentInstallments).every {
            def installment = new ContractInstallment(contract);
            installment.expiration >= instant("${it +1} month from now at 00:00 am") &&
            installment.expiration <= instant("${it +1} month from now at 23:59 pm");
            installment.plusExpiration(expiration);
            expiration = installment.expiration;
        }
    }

    def 'installment number should be incremented'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")

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