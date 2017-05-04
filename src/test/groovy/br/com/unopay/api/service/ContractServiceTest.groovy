package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractEstablishment
import br.com.unopay.api.model.ContractOrigin
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.Product
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class ContractServiceTest extends SpockApplicationTests {

    @Autowired
    ContractService service

    @Autowired
    SetupCreator setupCreator

    Hirer hirerUnderTest
    Contractor contractorUnderTest
    Product productUnderTest
    Establishment establishmentUnderTest


    void setup(){
        hirerUnderTest = setupCreator.createHirer()
        contractorUnderTest = setupCreator.createContractor()
        productUnderTest = setupCreator.createSimpleProduct()
        establishmentUnderTest = setupCreator.createHeadOffice()
    }

    void 'new contract should be created'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
                contract = contract.with {
                    hirer = hirerUnderTest
                    contractor = contractorUnderTest
                    product = productUnderTest
                    serviceType = productUnderTest.serviceType
                    it }

        when:
        def result  = service.save(contract)

        then:
        assert result.id != null
    }

    void 'given contract with null and default values should create with default values'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            documentNumberInvoice = null
            origin = null
            situation = null
            it }

        when:
        def result  = service.save(contract)

        then:
        assert result.id != null
        assert result.documentNumberInvoice == hirerUnderTest.documentNumber
        assert result.situation == ContractSituation.ACTIVE
        assert result.origin == ContractOrigin.UNOPAY
    }

    void 'given contract with same code should not be created'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        when:
        service.save(contract)
        service.save(contract.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        assert ex.errors.first().logref == 'CONTRACT_ALREADY_EXISTS'
    }

    void 'given contract with unknown hirer should not be created'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest.with {id = '112222233' ; it}
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        when:
        service.save(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    void 'given contract with unknown contractor should not be created'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest.with {id = '112222233' ; it}
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }
        when:
        service.save(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    void 'given contract with unknown product should not be created'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest.with {id = '112222233' ; it}
            serviceType = productUnderTest.serviceType
            it }

        when:
        service.save(contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }




    void 'known contract should be updated'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def created  = service.save(contract)
        def newName = 'ContractNew'
        contract.name = newName

        when:
        service.update(created.id, contract)
        def result = service.findById(created.id)

        then:
        assert result.name == newName
    }

    void 'unknown contract should not be updated'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def newName = 'ContractNew'
        contract.name = newName

        when:
        service.update('', contract)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }


    void 'given contract with unknown hirer should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def created = service.save(contract)

        when:
        service.update(created.id, contract.with { name = knownName
                                                  hirer = hirerUnderTest.with { id = '223455'; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'HIRER_NOT_FOUND'
    }

    void 'given contract with unknown contractor should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def created = service.save(contract)

        when:
        service.update(created.id, contract.with { name = knownName; contractor = contractorUnderTest.with { id = '1122345'; it }; it })

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    void 'given contract with unknown product should not be updated'(){
        given:
        def knownName = 'myName'
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def created = service.save(contract)

        when:
        service.update(created.id, contract.with {
                                                    name = knownName
                                                    product = productUnderTest.with { id = '11223345'; it }
                                                it })
        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'PRODUCT_NOT_FOUND'
    }
    void 'known contract should be found'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }

        def created  = service.save(contract)
        when:
        def result = service.findById(created.id)

        then:
        assert result != null
    }

    void 'unknown contract should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'known contract should be deleted'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }
        def created  = service.save(contract)
        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'unknown contract should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'add establishment in contract'(){
        given:
        Contract contract = Fixture.from(Contract.class).gimme("valid")
        ContractEstablishment contractEstablishment = Fixture.from(ContractEstablishment.class).gimme("valid")
        contract = contract.with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceType
            it }
        contract = service.save(contract)
        when:
        contractEstablishment = contractEstablishment.with {establishment = establishmentUnderTest; it}
        service.addEstablishments(contract.id,contractEstablishment)
        contract = service.findById(contract.id)
        then:
        assert contract.contractEstablishments.size() > 0
    }



}
