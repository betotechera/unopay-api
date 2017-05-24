package br.com.unopay.api.repository

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.filter.HirerFilter
import br.com.unopay.api.bacen.repository.HirerRepository
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.Period
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.filter.ContractFilter
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.hasSize
import static spock.util.matcher.HamcrestSupport.that

class FilterTest extends SpockApplicationTests {

    @Autowired
    ContractRepository repository

    @Autowired
    HirerRepository hirerRepository

    @Autowired
    SetupCreator setupCreator

    Hirer hirerUnderTest
    Contractor contractorUnderTest
    Product productUnderTest

    void setup(){
        Integer.mixin(TimeCategory)
        hirerUnderTest = setupCreator.createHirer()
        contractorUnderTest = setupCreator.createContractor()
        productUnderTest = setupCreator.createSimpleProduct()
    }

    def 'should return contracts in period'(){
        given:
        Contract contractA = createContract().with { begin = 5.day.ago; end = new Date(); it }
        Contract contractB = createContract().with { begin = 4.day.ago; end = 1.day.ago; it }
        Contract contractC = createContract().with { begin = 6.day.ago; end = 1.day.from.now; it }

        def filter = new ContractFilter()

        def beginPeriodUnderTest = new Period(5.day.ago, 4.day.ago)
        def endPeriodUnderTest = new Period(1.day.ago, new Date())
        filter.with { beginPeriod = beginPeriodUnderTest; endPeriod = endPeriodUnderTest }

        repository.save(contractA)
        repository.save(contractB)
        repository.save(contractC)

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)

    }

    def 'should not return contracts out of period'() {
        given:
        Contract contractA = createContract()
        Contract contractB = createContract()

        def filter = new ContractFilter()

        def beginPeriodUnderTest = new Period(1.day.from.now, 1.day.from.now)
        def endPeriodUnderTest = new Period(2.day.from.now, 2.day.from.now)
        filter.with { beginPeriod = beginPeriodUnderTest; endPeriod = endPeriodUnderTest }

        repository.save(contractA)
        repository.save(contractB)

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(0)
    }

    def 'should return contracts like name'() {
        given:
        Contract contractA = createContract().with { name = 'amanda'; it }
        Contract contractB = createContract().with { name = 'fernanda'; it}
        Contract contractC = createContract().with { name = 'teste'; it}

        def filter = new ContractFilter()

        filter.with { name = 'anda' }

        repository.save(contractA)
        repository.save(contractB)
        repository.save(contractC)

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)
    }

    def 'should return hirer when find document in more one join'() {
        given:
        Hirer hirerA = setupCreator.createHirer()

        def filter = new HirerFilter()

        filter.with { documentNumber = hirerA.person.document.number }

        when:
        def result = hirerRepository.findAll(filter)

        then:
        that result, hasSize(1)
    }

    def 'should return contracts like exact name'() {
        given:
        Contract contractA = createContract().with { name = 'jose'; it }
        Contract contractB = createContract().with { name = 'fernanda'; it}
        Contract contractC = createContract().with { name = 'teste'; it}

        def filter = new ContractFilter()

        filter.with { name = 'jose' }

        repository.save(contractA)
        repository.save(contractB)
        repository.save(contractC)

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(1)
    }

    private Contract createContract() {
        return Fixture.from(Contract.class).gimme("endedNow").with {
            hirer = hirerUnderTest
            contractor = contractorUnderTest
            product = productUnderTest
            serviceType = productUnderTest.serviceTypes
            it
        }
    }
}
