package br.com.unopay.api.payment.service

import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.payment.model.PaymentRemittance
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class PaymentRemittanceServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRemittanceService service

    @Autowired
    FixtureCreator fixtureCreator

    def 'payment remittance should be created'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        PaymentRemittance paymentRemittance = from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("issuer",issuer)
        }})

        when:
        PaymentRemittance created = service.save(paymentRemittance)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'when create payment remittance should generate a remittance number'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("issuer",issuer)
        }})

        when:
        service.create(issuer.id)
        def result = service.findByIssuer(issuer.id)

        then:
        result.find().number != null
    }

    def 'payment remittance should be created with right issuer'(){
        given:
        Issuer issuer = fixtureCreator.createIssuer()
        from(PaymentRemittance.class).gimme("valid", new Rule(){{
            add("issuer",issuer)
        }})

        when:
        service.create(issuer.id)
        def result = service.findByIssuer(issuer.id)

        then:
        result.find().issuer.id == issuer.id
        result.find().issuerBankCode == issuer.paymentAccount.bankAccount.bacenCode
    }

    def 'should create payment remittance by Issuer'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        from(BatchClosing.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("situation", BatchClosingSituation.FINALIZED)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.create(issuer.id)
        def all = service.findByIssuer(issuer.id)

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(1)
    }

    def 'remittance item value should be a sum of batch closing value by establishment'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        def (Establishment establishmentA, Establishment establishmentB) = from(Establishment.class)
                                                                            .uses(jpaProcessor).gimme(2, "valid")
        def (BatchClosing batchClosingA, BatchClosing batchClosingB) = from(BatchClosing.class)
                                                                .uses(jpaProcessor).gimme(2, "valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("establishment", uniqueRandom(establishmentA, establishmentB))
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.create(issuer.id)
        def all = service.findByIssuer(issuer.id)

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(2)
        all.find().remittanceItems.find { batchClosingA.establishmentId() == it.establishment.id }.value == batchClosingA.value
        all.find().remittanceItems.find { batchClosingB.establishmentId() == it.establishment.id }.value == batchClosingB.value
    }

    def 'remittance item value should be a sum of batch closing value of establishment'(){
        given:
        def issuer = fixtureCreator.createIssuer()
        Establishment establishment = from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def (BatchClosing batchClosingA, BatchClosing batchClosingB) = from(BatchClosing.class)
                                                                    .uses(jpaProcessor).gimme(2, "valid", new Rule() {{
            add("situation", BatchClosingSituation.FINALIZED)
            add("establishment", establishment)
            add("issuer", issuer)
            add("paymentReleaseDateTime", instant("1 day ago"))
        }})

        when:
        service.create(issuer.id)
        def all = service.findByIssuer(issuer.id)

        then:
        that all, hasSize(1)
        that all.find().remittanceItems, hasSize(1)
        all.find().remittanceItems.find().value == batchClosingA.value + batchClosingB.value
    }
}
