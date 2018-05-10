package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.RecurrencePeriod
import br.com.unopay.api.job.RemittanceJob
import br.com.unopay.api.job.UnopayScheduler
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class IssuerServiceTest  extends SpockApplicationTests {

    @Autowired
    IssuerService service

    @Autowired
    PaymentRuleGroupService paymentRuleGroupService

    UnopayScheduler schedulerMock = Mock(UnopayScheduler)

    def setup(){
        service.scheduler = schedulerMock
    }

    def 'when create issuer should be schedule remittance job'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid", new Rule(){{
            add("paymentAccount.depositPeriod", RecurrencePeriod.BIWEEKLY)
        }})

        when:
        service.create(issuer)

        then:
        1 * schedulerMock.schedule(_,RecurrencePeriod.BIWEEKLY.pattern, RemittanceJob.class)
    }

    def 'a valid issuer should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

    def 'when creating issuer without servicePasswordRequired should define it required'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result.servicePasswordRequired
    }

    def 'should create issuer with unrequired service password'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.servicePasswordRequired = false

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        !result.servicePasswordRequired
    }

    def 'a valid issuer with the same document number should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        service.create(created.with { id= null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'PERSON_ISSUER_ALREADY_EXISTS'
    }

    def 'should not create issuer with existing document'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        service.create(issuer)
        service.create(issuer.with { id= null;person.id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'PERSON_DOCUMENT_ALREADY_EXISTS'
    }

    def 'a valid issuer without person should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.setPerson(null)

        when:
        service.create(issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid issuer with unknown payment account should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def idNotFound = '567498879'
        issuer.getPaymentAccount().setId(idNotFound)

        when:
        def created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

    def 'a valid issuer without payment account should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.setPaymentAccount(null)

        when:
        service.create(issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PAYMENT_ACCOUNT_REQUIRED'
    }

    def 'a valid issuer with unknown movement account should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def idNotFound = '567498879'
        issuer.getMovementAccount().setId(idNotFound)

        when:
        service.create(issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a valid issuer without movement account should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.setMovementAccount(null)

        when:
        service.create(issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'MOVEMENT_ACCOUNT_REQUIRED'
    }

    def 'a valid issuer with unknown person should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def idNotFound = '55588899'
        issuer.getPerson().setId(idNotFound)

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.id)
        then:
        result != null
    }

    def 'a valid issuer without payment rule groups should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.setPaymentRuleGroups(null)

        when:
        service.create(issuer)
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

    def 'a valid issuer with unknown payment rule groups should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        List<PaymentRuleGroup> paymentRuleGroups = Fixture.from(PaymentRuleGroup.class).gimme(1, "valid")
        def idNotFound = '555888999'
        paymentRuleGroups.find().id = idNotFound
        issuer.paymentRuleGroups = paymentRuleGroups

        when:
        service.create(issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
        ex.errors.find().arguments.find() == "[$idNotFound]"
    }

    def 'when update issuer should be schedule remittance job'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentAccount.depositPeriod", RecurrencePeriod.BIWEEKLY)
        }})

        when:
        service.update(issuer.id, issuer)

        then:
        1 * schedulerMock.schedule(_,RecurrencePeriod.BIWEEKLY.pattern, RemittanceJob.class)
    }

    def 'given a existing issuer should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)

        issuer.with {
            fee = 0.3
            servicePasswordRequired = Boolean.FALSE
        }
        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        result.fee == 0.3d
        !result.servicePasswordRequired
    }

    def 'a person issuer should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        def name = 'newName'
        def phone = '115566778899'
        issuer.person.name = name
        issuer.person.telephone = phone

        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        result.person.name == name
        result.person.telephone == phone
    }

    def 'a movement account should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        Issuer created = service.create(issuer)
        def newAccountNumber = '5464656'
        issuer.movementAccount.accountNumber = newAccountNumber

        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        issuer.movementAccount.accountNumber == newAccountNumber
    }

    def 'a payment account should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        Issuer created = service.create(issuer)
        def newAccountNumber = '5464656'
        def postPaid = 50
        def prePaid = 6
        issuer.paymentAccount.bankAccount.accountNumber = newAccountNumber
        issuer.paymentAccount.postPaidPaymentDays = postPaid
        issuer.paymentAccount.prePaidPaymentDays = prePaid

        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        issuer.paymentAccount.bankAccount.accountNumber  == newAccountNumber
        issuer.paymentAccount.postPaidPaymentDays == postPaid
        issuer.paymentAccount.prePaidPaymentDays == prePaid
    }

    def 'a payment rule group reference should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        PaymentRuleGroup paymentRuleGroup = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        Issuer created = service.create(issuer)
        PaymentRuleGroup paymentRuleGroupCreated = paymentRuleGroupService.create(paymentRuleGroup)
        issuer.paymentRuleGroups = [paymentRuleGroupCreated]

        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        that result.paymentRuleGroups, hasSize(1)
        result.paymentRuleGroups.find()?.id == paymentRuleGroupCreated.id

    }

    def 'given a unknown issuer when updated should not be found'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        issuer.with { fee = 0.3 }
        when:
        service.update('', issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }

    def 'given a issuer without person id when updated should not be processable'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.getPerson().setId(null)

        issuer.with { fee = 0.3 }
        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_ID_REQUIRED'
    }

    def 'given a issuer without person when updated should not be processable'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        issuer.setPerson(null)

        issuer.with { fee = 0.3 }
        when:
        service.update('', issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid issuer without person should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.setPerson(null)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid issuer with unknown payment account should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        def idNotFound = '567498879'
        issuer.getPaymentAccount().setId(idNotFound)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PAYMENT_ACCOUNT_NOT_FOUND'
    }

    def 'a valid issuer without payment account id should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.getPaymentAccount().setId(null)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PAYMENT_ACCOUNT_ID_REQUIRED'
    }

    def 'a valid issuer without payment account should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.setPaymentAccount(null)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PAYMENT_ACCOUNT_REQUIRED'
    }

    def 'a valid issuer with unknown movement account should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        def idNotFound = '567498879'
        issuer.getMovementAccount().setId(idNotFound)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a valid issuer without movement account should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.setMovementAccount(null)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'MOVEMENT_ACCOUNT_REQUIRED'
    }

    def 'a valid issuer with unknown person should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        def idNotFound = '55588899'
        issuer.getPerson().setId(idNotFound)

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PERSON_NOT_FOUND'
        ex.errors.find().arguments.find() == idNotFound
    }

    def 'a valid issuer without payment rule groups should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        issuer.setPaymentRuleGroups(null)

        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
    }

    def 'a valid issuer with unknown payment rule groups should not be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)
        List<PaymentRuleGroup> paymentRuleGroups = Fixture.from(PaymentRuleGroup.class).gimme(1, "valid")
        def idNotFound = '555888999'
        paymentRuleGroups.find().id = idNotFound
        issuer.paymentRuleGroups = paymentRuleGroups

        when:
        service.update(created.id, issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PAYMENT_RULE_GROUP_NOT_FOUND'
        ex.errors.find().arguments.find() == "[$idNotFound]"
    }


    def 'given a unknown issuer when find should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }

    def 'a known issuer should be deleted'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)

        when:
        def found = service.findById(created.id)
        service.delete(created.id)
        service.findById(created.id)

        then:
        found != null
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }


    def 'given a unknown issuer when delete should not be found'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ISSUER_NOT_FOUND'
    }


}
