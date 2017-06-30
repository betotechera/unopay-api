package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.notification.service.NotificationService
import org.apache.commons.beanutils.BeanUtils
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class BatchClosingServiceTest extends SpockApplicationTests {

    @Autowired
    BatchClosingService service

    @Autowired
    ServiceAuthorizeService serviceAuthorizeService

    @Autowired
    FixtureCreator fixtureCreator

    NotificationService notificationServiceMock = Mock(NotificationService)

    void setup(){
        service.notificationService = notificationServiceMock
    }

    def 'should create batch closing'(){
        given:
        BatchClosing batchClosing = fixtureCreator.createBatchToPersist()

        when:
        def created = service.save(batchClosing)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'when create batch closing should send email notification'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment)

        when:
        service.create(establishment.id)

        then:
        1 * notificationServiceMock.sendBatchClosingMail(_, _)
    }

    def 'should create batch closing by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'given authorizations of one or more days ago should be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "1 day ago")

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'given authorizations only today should not be processed'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizationsAt(contracts, establishment, "1 hour ago")

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(0)
    }

    def 'should not create batch closing to processed authorizations by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        that batchClosings.find().batchClosingItems, hasSize(2)

    }

    def 'given a contract with issue invoice should create batch closing document received situation'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", true)
        }})
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        batchClosings.find().situation == BatchClosingSituation.DOCUMENT_RECEIVED
    }

    def 'given a contract without issue invoice should create batch closing finalized situation'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid", new Rule(){{
            add("issueInvoice", false)
        }})
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> batchClosings = service.findByEstablishmentId(establishment.id)

        then:
        that batchClosings, hasSize(1)
        batchClosings.find().situation == BatchClosingSituation.FINALIZED
    }

    def 'should create batch closing value by establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishment, 3)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        bachClosings.find()?.value == totalByHirer.entrySet().find().value
    }

    def 'should create batch closing value by hirer'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishment, 3)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosings.find { it.hirer.id == totalByHirer.entrySet().last().key }?.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing value by establishment and hirer'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishments = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishments, 3)

        when:
        service.create(establishments.find().id)
        service.create(establishments.last().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishments.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishments.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosingsB.find { it.hirer.id == totalByHirer.entrySet().last().key }?.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing only by invoked establishment'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishments = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        Map totalByHirer = createServiceAuthorizations(contracts, establishments, 3)

        when:
        service.create(establishments.find().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishments.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishments.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == totalByHirer.entrySet().find().key }?.batchClosingItems, hasSize(3)
        that bachClosingsB, hasSize(0)
    }

    def 'should create batch closing item by service authorize'(){
        given:
        List<Contract> contracts = Fixture.from(Contract.class).uses(jpaProcessor).gimme(1, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        createServiceAuthorizations(contracts, establishment, 2)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find()?.batchClosingItems, hasSize(2)
    }



    Map createServiceAuthorizations(List<Contract> contract, Establishment establishment, Integer authorizations = 1) {
        return createServiceAuthorizations(contract, Arrays.asList(establishment), authorizations)
    }

    Map createServiceAuthorizationsAt(List<Contract> contract, Establishment establishment, String dateAsText) {
        return createServiceAuthorizations(contract, Arrays.asList(establishment), 2, dateAsText)
    }

    Map createServiceAuthorizations(List<Contract> contracts, List<Establishment> establishments,
                                    numberOfAuthorizations = 1, String dateAsText = "1 day ago") {
        def sumValueByHirer = [:]
        (1..contracts.size()).each { Integer index ->
            def establishment = contracts.size() != establishments.size() ? establishments.find() : establishments.get(index-1)
            def instrumentCredit = fixtureCreator.createInstrumentToContract(contracts.get(index-1))
            def serviceAuthorize = fixtureCreator.createServiceAuthorize(instrumentCredit, establishment, dateAsText)
            sumValueByHirer.put(serviceAuthorize.hirerId(), serviceAuthorize.eventValue * numberOfAuthorizations)
            (1..numberOfAuthorizations).each {
                ServiceAuthorize cloned = BeanUtils.cloneBean(serviceAuthorize)
                serviceAuthorizeService.save(cloned)
            }
        }
        return sumValueByHirer
    }
}
