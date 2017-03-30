package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.hamcrest.core.Is
import org.springframework.beans.factory.annotation.Autowired

class IssuerServiceTest  extends SpockApplicationTests {

    @Autowired
    IssuerService service

    def 'a valid issuer should be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        when:
        Issuer created = service.create(issuer)
        Issuer result = service.findById(created.getId())

        then:
        result != null
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

    def 'a valid issuer with unknown payment account should not be created'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def idNotFound = '567498879'
        issuer.getPaymentAccount().setId(idNotFound)

        when:
        service.create(issuer)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
        ex.errors.find().arguments.find() == "[$idNotFound]"
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
        ex.errors.find().arguments.find() == "[$idNotFound]"
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

    def 'given a existing issuer should be updated'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        Issuer created = service.create(issuer)

        issuer.with { tax = 0.3 }
        when:
        service.update(created.id, issuer)
        Issuer result = service.findById(created.id)

        then:
        result != null
        result.tax == 0.3d
    }

    def 'given a unknown issuer when updated should not be found'(){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")

        issuer.with { tax = 0.3 }
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

        issuer.with { tax = 0.3 }
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

        issuer.with { tax = 0.3 }
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
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
        ex.errors.find().arguments.find() == "[$idNotFound]"
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
        ex.errors.find().arguments.find() == "[$idNotFound]"
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
