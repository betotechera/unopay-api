package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.util.Rounder
import br.com.unopay.api.util.Time
import br.com.unopay.bootcommons.exception.NotFoundException
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class ContractInstallmentServiceTest extends SpockApplicationTests {

    @Autowired
    ContractInstallmentService service


    @Autowired
    FixtureCreator fixtureCreator


    def 'given a contract with installments should mark as paid'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        service.create(contract)
        BigDecimal paid = 500.00

        when:
        service.markAsPaid(contract.id, paid)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        def installment = result.sort { it.installmentNumber }.find()
        installment.paymentDateTime == Time.create()
        installment.paymentValue == paid
    }

    def 'given a contract with previous installment paid should mark next as paid'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        service.create(contract)
        BigDecimal paid = 500.00
        service.markAsPaid(contract.id, paid)

        when:
        service.markAsPaid(contract.id, paid)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        def installment = result.find { it.installmentNumber == 2 }
        installment.paymentDateTime == Time.create()
        installment.paymentValue == paid
    }


    def 'when try mark as paid a unknown contract should return error'(){
        given:
        BigDecimal paid = 500.00

        when:
        service.markAsPaid('', paid)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENTS_NOT_FOUND'
    }

    def 'given a valid contract should create installment'(){
        given:
        def contract = fixtureCreator.createPersistedContract()

        when:
        service.create(contract)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        !result.isEmpty()
    }

    def 'given a valid contract should create one installment by installment number'(){
        given:
        def contract = fixtureCreator.createPersistedContract()

        when:
        service.create(contract)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        that result, hasSize(contract.paymentInstallments)
    }

    def 'given a valid contract should create the installments with annuity by installment number'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        def installmentExpected = Rounder.round(contract.annuity / contract.paymentInstallments)
        when:
        service.create(contract)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        !result.isEmpty()
        result.every { it.value == installmentExpected }
    }

    def 'given a valid contract should create the installments with sequential number'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        when:
        service.create(contract)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        int expectedInstallmentNumber = 0
        !result.isEmpty()
        result.sort { it.installmentNumber }.every {
            expectedInstallmentNumber++
            it.installmentNumber == expectedInstallmentNumber
        }

    }

    def 'given a valid contract should create the installments with expiration date with 30 days after the previous'(){
        given:
        def contract = fixtureCreator.createPersistedContract()

        when:
        service.create(contract)
        Set<ContractInstallment> result = service.findByContractId(contract.id)

        then:
        !result.isEmpty()
        result.sort { it.installmentNumber }.every {
            it.expiration == Time.createDateTime()
                    .plusMonths(it.installmentNumber - 1).dayOfMonth().withMaximumValue().toDate()
        }
    }


    def 'a valid contract installment should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        ContractInstallment installment = Fixture.from(ContractInstallment.class).gimme("valid", new Rule(){{
            add("contract", contract)
        }})
        when:
        ContractInstallment created = service.save(installment)

        then:
        created != null
    }

    def 'should delete all by contract'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        Fixture.from(ContractInstallment.class).uses(jpaProcessor).gimme(3,"valid", new Rule(){{
            add("contract", contract)
        }})
        def installments = service.findByContractId(contract.id)
        when:
        service.deleteByContract(contract.id)
        service.findByContractId(contract.id)

        then:
        that installments, hasSize(3)
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENTS_NOT_FOUND'
    }

    def 'a valid contract installment should be updated'(){
        given:
        def created = create()
        def newField = 99999.9
        created.value = newField

        when:
        service.update(created.id, created)
        ContractInstallment result = service.findById(created.id)

        then:
        result.value == newField
    }

    def 'a known contract installment should be found'(){
        given:
        def created = create()

        when:
        ContractInstallment result = service.findById(created.id)

        then:
        result != null
    }

    def 'should return installments by contract'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        create(contract)
        create(contract)

        when:
        def result = service.findByContractId(contract.id)

        then:
        result != null
        that result, hasSize(2)
    }

    def 'a unknown contract installment should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENT_NOT_FOUND'
    }

    def 'a known contract installment should be deleted'(){
        given:
        def created = create()

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENT_NOT_FOUND'
    }

    def 'a unknown contract installment should not be deleted'(){
        when:
        service.delete('')
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTRACT_INSTALLMENT_NOT_FOUND'
    }

    private ContractInstallment create(contract = fixtureCreator.createPersistedContract()){
        return Fixture.from(ContractInstallment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
        }})
    }
}
