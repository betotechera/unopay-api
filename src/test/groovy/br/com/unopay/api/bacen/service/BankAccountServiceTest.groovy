package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.BankAccount
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class BankAccountServiceTest extends SpockApplicationTests {

    @Autowired
    BankAccountService service

    def 'a valid account should be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")

        when:
        BankAccount created = service.create(account)

        then:
        created != null
    }

    def 'given a account without agency should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setAgency(null)
        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'AGENCY_REQUIRED'
    }

    def 'given a account without agency dv should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setDvAgency(null)
        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'AGENCY_DV_REQUIRED'
    }

    def 'given a account without number dv should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setDvAccountNumber(null)
        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCOUNT_NUMBER_DV_REQUIRED'
    }

    def 'given a account without number should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setDvAccountNumber(null)
        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCOUNT_NUMBER_DV_REQUIRED'
    }

    def 'given a account without type should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setType(null)
        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'TYPE_REQUIRED'
    }

    def 'given a account without agency should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setAgency(null)
        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'AGENCY_REQUIRED'
    }

    def 'given a account without agency dv should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setDvAgency(null)
        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'AGENCY_DV_REQUIRED'
    }

    def 'given a account without number dv should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setDvAccountNumber(null)
        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCOUNT_NUMBER_DV_REQUIRED'
    }

    def 'given a account without number should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setDvAccountNumber(null)
        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCOUNT_NUMBER_DV_REQUIRED'
    }

    def 'given a account without type should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setType(null)
        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'TYPE_REQUIRED'
    }

    def 'given a account with unknown bank should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.getBank().setBacenCode(888888)

        when:
        service.create(account)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_NOT_FOUND'
    }

    def 'given a account without bank code should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.getBank().setBacenCode(null)

        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_CODE_REQUIRED'
    }

    def 'given a account without bank should not be created'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        account.setBank(null)

        when:
        service.create(account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_REQUIRED'
    }

    def 'a valid account should be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        def agency = '555555'
        account.setAgency(agency)

        when:
        service.update(created.id, account)
        BankAccount result = service.findById(created.id)

        then:
        result != null
        result.agency == agency

    }


    def 'given a account without bank code should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.getBank().setBacenCode(null)

        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_CODE_REQUIRED'
    }

    def 'given a account without bank should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.setBank(null)

        when:
        service.update(created.id, account)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_REQUIRED'
    }

    def 'given a account with unknown bank should not be updated'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)
        account.getBank().setBacenCode(888888)

        when:
        service.update(created.id, account)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_NOT_FOUND'
    }

    def 'a known account should be deleted'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)

        when:
        BankAccount result = service.findById(created.id)
        service.delete(created.id)
        service.findById(created.id)

        then:
        result != null
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a unknown account should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a known account should be found'(){
        given:
        BankAccount account = Fixture.from(BankAccount.class).gimme("valid")
        BankAccount created = service.create(account)

        when:
        BankAccount result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown account should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }
}
