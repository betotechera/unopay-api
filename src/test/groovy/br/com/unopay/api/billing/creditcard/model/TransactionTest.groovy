package br.com.unopay.api.billing.creditcard.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.billing.creditcard.model.CardBrand.fromCardNumber
import static br.com.unopay.api.function.FixtureFunctions.instant

class TransactionTest extends FixtureApplicationTest {

    def 'when create the create date should be current date'(){
        when:
        def transaction = new Transaction()

        then:
        transaction.createDateTime >= instant("1 second ago")
        transaction.createDateTime < instant("1 second from now")
    }

    def 'created transaction should be pending'(){
        when:
        def transaction = new Transaction()

        then:
        transaction.status == TransactionStatus.PENDING
    }

    def 'should return long value'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        Long amount = transaction.getLongAmountValue()

        then:
        amount == (transaction.getAmount().getValue() * (new BigDecimal(100))).longValue()
    }

    def 'should return right brand'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        CardBrand brand = transaction.getCreditCard().getCardBrand()

        then:
        brand == fromCardNumber(transaction.getCreditCard().getNumber())
    }

    def 'should return amount iso code'(){
        given:
        Transaction transaction = Fixture.from(Transaction.class).gimme("valid")

        when:
        Integer isoCode = transaction.getAmountCurrencyIsoCode()

        then:
        isoCode ==  transaction.getAmount().getCurrency().getIso()
    }

    def 'should be equals'() {
        given:
        Transaction a = Fixture.from(Transaction.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals
    }

    def 'should not be equals'() {
        List list = Fixture.from(Transaction.class).gimme(2, "valid")

        when:
        def shouldBeEquals = list.head() == list.tail()

        then:
        !shouldBeEquals

    }
}
