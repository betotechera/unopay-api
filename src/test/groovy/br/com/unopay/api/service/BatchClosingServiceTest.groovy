package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.CreditPaymentAccount
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.ServiceAuthorize
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

    def 'should create batch closing'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid")
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).uses(jpaProcessor).gimme("valid")
        Hirer hirer = Fixture.from(Hirer.class).uses(jpaProcessor).gimme("valid")

        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid", new Rule(){{
            add("establishment",establishment)
            add("issuer",issuer)
            add("accreditedNetwork",accreditedNetwork)
            add("hirer",hirer)
        }})

        when:
        def created = service.save(batchClosing)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'should create batch closing by establishment'(){
        given:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        ContractorInstrumentCredit instrumentCredit = createCredit(contract)
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def serviceAuthorize = fixtureCreator.createServiceAuthorize(instrumentCredit, establishment)
        def serviceAuthorizeB = BeanUtils.cloneBean(serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorizeB)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings, hasSize(1)
    }

    def 'should create batch closing value by establishment'(){
        given:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        ContractorInstrumentCredit instrumentCredit = createCredit(contract)
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def serviceAuthorize = fixtureCreator.createServiceAuthorize(instrumentCredit, establishment)
        def serviceAuthorizeB = BeanUtils.cloneBean(serviceAuthorize)
        def serviceAuthorizeC = BeanUtils.cloneBean(serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorizeB)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorizeC)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        bachClosings.find().value == serviceAuthorize.eventValue * 3
    }

    def 'should create batch closing value by hirer'(){
        given:
        List<Contract> contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        ServiceAuthorize serviceAuthorizeHirerA = createAuthorizeByEstablishmentAndContract(contract.find(), establishment)
        ServiceAuthorize serviceAuthorizeHirerB = createAuthorizeByEstablishmentAndContract(contract.last(), establishment)
        createAuthorizes(serviceAuthorizeHirerA)
        createAuthorizes(serviceAuthorizeHirerB)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find { it.hirer.id == serviceAuthorizeHirerB.hirerId() }.batchClosingItems, hasSize(3)
        that bachClosings.find { it.hirer.id == serviceAuthorizeHirerA.hirerId() }.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing value by establishment and hirer'(){
        given:
        List<Contract> contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        ServiceAuthorize serviceAuthorizeHirerA = createAuthorizeByEstablishmentAndContract(contract.find(), establishment.find())
        ServiceAuthorize serviceAuthorizeHirerB = createAuthorizeByEstablishmentAndContract(contract.last(), establishment.last())
        createAuthorizes(serviceAuthorizeHirerA)
        createAuthorizes(serviceAuthorizeHirerB)

        when:
        service.create(establishment.find().id)
        service.create(establishment.last().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishment.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishment.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == serviceAuthorizeHirerA.hirerId() }?.batchClosingItems, hasSize(3)
        that bachClosingsB.find { it.hirer.id == serviceAuthorizeHirerB.hirerId() }?.batchClosingItems, hasSize(3)
    }

    def 'should create batch closing only by invoked establishment'(){
        given:
        List<Contract> contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme(2, "valid")
        List<Establishment> establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme(2, "valid")
        ServiceAuthorize serviceAuthorizeHirerA = createAuthorizeByEstablishmentAndContract(contract.find(), establishment.find())
        ServiceAuthorize serviceAuthorizeHirerB = createAuthorizeByEstablishmentAndContract(contract.last(), establishment.last())
        createAuthorizes(serviceAuthorizeHirerA)
        createAuthorizes(serviceAuthorizeHirerB)

        when:
        service.create(establishment.find().id)
        Set<BatchClosing> bachClosingsA = service.findByEstablishmentId(establishment.find().id)
        Set<BatchClosing> bachClosingsB = service.findByEstablishmentId(establishment.last().id)

        then:
        that bachClosingsA.find { it.hirer.id == serviceAuthorizeHirerA.hirerId() }?.batchClosingItems, hasSize(3)
        that bachClosingsB, hasSize(0)
    }

    def 'should create batch closing item by service authorize'(){
        given:
        Contract contract = Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid")
        ContractorInstrumentCredit instrumentCredit = createCredit(contract)
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def serviceAuthorize = fixtureCreator.createServiceAuthorize(instrumentCredit, establishment)
        def serviceAuthorizeB = BeanUtils.cloneBean(serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorize)
        serviceAuthorizeService.create(serviceAuthorize.user.email, serviceAuthorizeB)

        when:
        service.create(establishment.id)
        Set<BatchClosing> bachClosings = service.findByEstablishmentId(establishment.id)

        then:
        that bachClosings.find().batchClosingItems, hasSize(2)
    }

    private ContractorInstrumentCredit createCredit(Contract contract){
        PaymentInstrument paymentInstrument = Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contractor", contract.contractor)
            add("product", contract.product)
        }})
        CreditPaymentAccount paymentAccount = Fixture.from(CreditPaymentAccount.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("hirerDocument", contract.hirerDocumentNumber)
            add("product", contract.product)
        }})
        ContractorInstrumentCredit instrumentCredit = Fixture.from(ContractorInstrumentCredit.class).uses(jpaProcessor).gimme("allFields", new Rule(){{
            add("contract", contract)
            add("paymentInstrument", paymentInstrument)
            add("creditPaymentAccount", paymentAccount)
        }})
        instrumentCredit
    }

    private void createAuthorizes(ServiceAuthorize serviceAuthorizeHirerA) {
        def serviceAuthorizeB = BeanUtils.cloneBean(serviceAuthorizeHirerA)
        def serviceAuthorizeC = BeanUtils.cloneBean(serviceAuthorizeHirerA)
        serviceAuthorizeService.create(serviceAuthorizeHirerA.user.email, serviceAuthorizeHirerA)
        serviceAuthorizeService.create(serviceAuthorizeHirerA.user.email, serviceAuthorizeB)
        serviceAuthorizeService.create(serviceAuthorizeHirerA.user.email, serviceAuthorizeC)
    }

    private ServiceAuthorize createAuthorizeByEstablishmentAndContract(Contract contract, Establishment establishment) {
        ContractorInstrumentCredit instrumentCredit = createCredit(contract)
        fixtureCreator.createServiceAuthorize(instrumentCredit, establishment)
    }

}
