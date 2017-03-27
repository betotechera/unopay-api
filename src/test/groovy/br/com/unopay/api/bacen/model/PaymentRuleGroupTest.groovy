package br.com.unopay.api.bacen.model;

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

 class PaymentRuleGroupTest extends SpockApplicationTests {

    void 'when validating paymentRuleGroup without userRelationship should return error'() {
        given:
        PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
        group.userRelationship = null
        when:
        group.validate()

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find()?.logref == 'USER_RELATIONSHIP_REQUIRED'
    }

     void 'when validating paymentRuleGroup without name should return error'() {
         given:
         PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
         group.name = null

         when:
         group.validate()

         then:
         def ex = thrown(UnprocessableEntityException)
         ex.errors.find()?.logref == 'PAYMENT_RULE_GROUP_NAME_REQUIRED'
     }


     void 'should not validate paymentRuleGroup with large name'(){
         given:
         PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
         group.name = """In sem justo, commodo ut, suscipit at, pharetra vitae, 
                        orci. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. 
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 
                        Aliquam id dolor. Class aptent taciti sociosqu ad litora"""

         when:
         group.validate()

         then:
         def ex = thrown(UnprocessableEntityException)
         ex.errors.first().logref == 'LARGE_PAYMENT_RULE_GROUP_NAME'
     }

     void 'should not validate paymentRuleGroup with short name'(){
         given:
         PaymentRuleGroup group = Fixture.from(PaymentRuleGroup.class).gimme("valid")
         group.name = "aa"

         when:
         group.validate()

         then:
         def ex = thrown(UnprocessableEntityException)
         ex.errors.first().logref == 'SHORT_PAYMENT_RULE_GROUP_NAME'
     }

 }
