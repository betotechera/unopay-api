package br.com.unopay.api.repository

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Service
import static br.com.unopay.api.bacen.model.ServiceType.DIAGNOSIS_AND_THERAPY
import static br.com.unopay.api.bacen.model.ServiceType.DOCTORS_APPOINTMENTS
import static br.com.unopay.api.bacen.model.ServiceType.MEDICINES
import br.com.unopay.api.bacen.model.filter.HirerFilter
import br.com.unopay.api.bacen.model.filter.ServiceFilter
import br.com.unopay.api.bacen.repository.HirerRepository
import br.com.unopay.api.bacen.repository.ServiceRepository
import br.com.unopay.api.bacen.util.FixtureCreator
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.filter.ContractFilter
import br.com.unopay.bootcommons.model.Period
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class FilterTest extends SpockApplicationTests {

    @Autowired
    ContractRepository repository

    @Autowired
    ServiceRepository serviceRepository

    @Autowired
    HirerRepository hirerRepository

    @Autowired
    FixtureCreator fixtureCreator

    Hirer hirerUnderTest
    Contractor contractorUnderTest
    Product productUnderTest

    void setup(){
        hirerUnderTest = fixtureCreator.createHirer()
        contractorUnderTest = fixtureCreator.createContractor()
        productUnderTest = fixtureCreator.createProduct()
        Fixture.of(Contract.class).addTemplate("withReferences").inherits("valid", new Rule() {{
            add("hirer",hirerUnderTest)
            add("contractor",contractorUnderTest)
            add("product",productUnderTest)
            add("serviceTypes",productUnderTest.serviceTypes)
        }})
    }

    def 'should return contracts in period'(){
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("withReferences", new Rule(){{
            add("begin", instant("5 days ago"))
            add("end", instant("now"))
        }})
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("withReferences", new Rule(){{
            add("begin", instant("4 days ago"))
            add("end", instant("1 day ago"))
        }})
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("withReferences", new Rule(){{
            add("begin", instant("6 days ago"))
            add("end", instant("1 day from now"))
        }})

        def filter = new ContractFilter()
        def beginPeriodUnderTest = new Period(instant("5 days ago"), instant("4 days ago"))
        def endPeriodUnderTest = new Period(instant("1 day ago"), instant("now"))
        filter.with { beginPeriod = beginPeriodUnderTest; endPeriod = endPeriodUnderTest }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)
    }

    def 'should return contracts in inclusive period'(){
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("withReferences", new Rule(){{
            add("createdDateTime", instant("now"))
        }})

        def filter = new ContractFilter()
        def endPeriodUnderTest = new Period(instant("1 day ago"), instant("today"))
        filter.with { createdDateTime =  endPeriodUnderTest }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(1)

    }

    def 'should not return contracts out of period'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "withReferences", new Rule(){{
            add("begin", instant("5 days ago"))
            add("end", instant("now"))
        }})

        def filter = new ContractFilter()

        def beginPeriodUnderTest = new Period(instant("1 day from now"), instant("1 day from now"))
        def endPeriodUnderTest = new Period(instant("2 days from now"), instant("2 days from now"))
        filter.with { beginPeriod = beginPeriodUnderTest; endPeriod = endPeriodUnderTest }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(0)
    }

    def 'should return contracts like name'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("name", uniqueRandom("amanda", "fernanda", "joao"))
        }})

        def filter = new ContractFilter()

        filter.with { name = 'anda' }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)
    }

    def 'should return contracts filter with collection attribute by list'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("serviceTypes",
                    uniqueRandom([DOCTORS_APPOINTMENTS, ], [DIAGNOSIS_AND_THERAPY, MEDICINES], [DOCTORS_APPOINTMENTS, DIAGNOSIS_AND_THERAPY]))
        }})
        def filter = new ContractFilter()

        filter.with { serviceTypes = [DOCTORS_APPOINTMENTS] as Set}

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)
    }

    def 'should return contracts filter single attribute by list'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("code",
                    uniqueRandom(1L, 2L, 3L))
        }})
        def filter = new ContractFilter()

        filter.with { inCodes = [1L,2L] }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(2)
    }


    def 'should return hirer when find document in more one join'() {
        given:
        Hirer hirerA = fixtureCreator.createHirer()

        def filter = new HirerFilter()

        filter.with { documentNumber = hirerA.person.document.number }

        when:
        def result = hirerRepository.findAll(filter)

        then:
        that result, hasSize(1)
    }

    def 'should return service equals Integer code'() {
        given:
        Fixture.from(Service.class).uses(jpaProcessor).gimme(3,"valid", new Rule(){{
            add("code", uniqueRandom(1, 2, 3))
        }})

        def filter = new ServiceFilter()

        filter.with { code = 2 }

        when:
        def result = serviceRepository.findAll(filter)

        then:
        that result, hasSize(1)
    }


    def 'should return contracts like exact name'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("name", uniqueRandom("jose", "fernanda", "joao"))
        }})

        def filter = new ContractFilter()

        filter.with { name = 'jose' }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(1)
    }

    def 'should return contracts like name with ignore case'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("name", uniqueRandom("JoSe", "fernanda", "joao"))
        }})

        def filter = new ContractFilter()

        filter.with { name = findName }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(1)

        where:
        findName | _
        'JOse'   | _
        'jose'   | _
    }

    def 'should return contracts with long filter parameter'() {
        given:
        Fixture.from(Contract.class).uses(jpaProcessor).gimme(3,"withReferences", new Rule(){{
            add("name", uniqueRandom("JoSe", "fernanda", "joao"))
            add("code", 555333222l)
        }})

        def filter = new ContractFilter()

        filter.with { code = 555333222l }

        when:
        def result = repository.findAll(filter)

        then:
        that result, hasSize(1)
    }


}
