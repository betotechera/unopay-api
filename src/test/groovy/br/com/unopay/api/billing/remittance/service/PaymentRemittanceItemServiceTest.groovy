package br.com.unopay.api.billing.remittance.service

import br.com.six2six.fixturefactory.Fixture
import static br.com.six2six.fixturefactory.Fixture.from
import static br.com.six2six.fixturefactory.Fixture.from
import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.BankAccount
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.remittance.model.PaymentRemittance
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem
import br.com.unopay.api.billing.remittance.model.RemittancePayee
import br.com.unopay.api.billing.remittance.model.RemittanceSituation
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.model.BatchClosingSituation
import br.com.unopay.api.network.model.Establishment
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import static org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that
import static spock.util.matcher.HamcrestSupport.that

class PaymentRemittanceItemServiceTest extends SpockApplicationTests {

    @Autowired
    PaymentRemittanceItemService service

    @Autowired
    PaymentRemittanceService remittanceService

    @Autowired
    FixtureCreator fixtureCreator

    def 'given more than one payee with the same document then only one remittance item should be created with the consolidated value for it'(){
        given:
        Establishment establishment= from(Establishment.class).uses(jpaProcessor).gimme("valid")

        List<RemittancePayee> payees = from(RemittancePayee.class).gimme(2,"valid", new Rule() {{
            add("documentNumber", establishment.documentNumber())
            add("agency", "1234")
            add("accountNumber", "5556677")
        }})

        when:
        def items = service.processItems(payees)

        then:
        that items, hasSize(1)
        items.find().value == payees.sum { it.receivable }
    }

    def 'given payees with a different document should create a remittance item for each one'(){
        given:
        List<RemittancePayee> payees = from(RemittancePayee.class).gimme(2,"valid")

        when:
        def items = service.processItems(payees)

        then:

        that items, hasSize(2)
        items.find { it.payeeDocumentIs(payees.find().documentNumber) } .value == payees.find().receivable
        items.find { it.payeeDocumentIs(payees.last().documentNumber) } .value == payees.last().receivable
    }

    def 'payment remittance item should be created'(){
        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).gimme("valid")
        remittanceService.save(paymentRemittance)

        PaymentRemittanceItem paymentRemittanceItem = Fixture.from(PaymentRemittanceItem.class)
                                                                                            .gimme("valid", new Rule(){{
            add("paymentRemittance", paymentRemittance)
        }})

        when:
        PaymentRemittanceItem created = service.save(paymentRemittanceItem)
        def result = service.findById(created.id)

        then:
        result.id != null
    }

    def 'given a payment remittance item with processing situation should be found'(){

        given:
        PaymentRemittance paymentRemittance = Fixture.from(PaymentRemittance.class).gimme("valid")
        remittanceService.save(paymentRemittance)
        PaymentRemittanceItem item = Fixture.from(PaymentRemittanceItem.class).gimme("valid", new Rule() {
            {
                add("paymentRemittance", paymentRemittance)
                add("situation", RemittanceSituation.PROCESSING)
            }
        })
        service.save(item)

        when:
        Optional<PaymentRemittanceItem> result = service.findProcessingByEstablishment(item.payee.documentNumber)

        then:
        result.get().id == item.id
        result.get().situation == RemittanceSituation.PROCESSING
    }

}
