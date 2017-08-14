package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class ContractInstallmentServiceTest extends SpockApplicationTests {

    @Autowired
    ContractInstallmentService service


    @Autowired
    FixtureCreator fixtureCreator


    def 'a valid contract installment should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        ContractInstallment installment = Fixture.from(ContractInstallment.class).gimme("valid", new Rule(){{
            add("contract", contract)
        }})
        when:
        ContractInstallment created = service.create(installment)

        then:
        created != null
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

    private ContractInstallment create(){
        def contract = fixtureCreator.createPersistedContract()
        return Fixture.from(ContractInstallment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
        }})
    }
}
