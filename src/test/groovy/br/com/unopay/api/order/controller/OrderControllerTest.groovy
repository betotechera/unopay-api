package br.com.unopay.api.order.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.order.model.PaymentStatus
import br.com.unopay.api.order.model.OrderType
import br.com.unopay.api.order.service.OrderService
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.apache.commons.beanutils.BeanUtils
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrderControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    OrderService orderService

    @Autowired
    ContractInstallmentService installmentService

    void 'valid order should be created'() {
        given:
        String accessToken = getUserAccessToken()
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("type", OrderType.ADHESION)
        }})

        when:
        def result = this.mvc.perform(post('/orders?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(order)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'valid adhesion order should be created'() {
        given:
        String accessToken = getClientAccessToken()
        def product = fixtureCreator.crateProductWithSameIssuerOfHirer()
        Order order = Fixture.from(Order.class).gimme("valid", new Rule(){{
            add("product", product)
            add("type", OrderType.ADHESION)
        }})

        when:
        def result = this.mvc.perform(post('/orders?access_token={access_token}&type=ADHESION', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(order)))
        then:
        result.andExpect(status().isCreated())
    }

    def 'given a known credit order with status waiting payment when status is changed should be updated'(){
        given:
        def expectedStatus = PaymentStatus.PAID
        String accessToken = getUserAccessToken()
        Order order = createPersistedOrder(OrderType.CREDIT, PaymentStatus.WAITING_PAYMENT)
        def id = order.id
        Order orderForUpdate = BeanUtils.cloneBean(order)
        orderForUpdate.status = expectedStatus

        when:
        def result = this.mvc.perform(put('/orders/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(orderForUpdate)))

        def current = orderService.findById(order.id)

        then:
        result.andExpect(status().isNoContent())
        current.status == expectedStatus

    }

    void 'known orders should be found'() {
        given:
        String accessToken = getUserAccessToken()
        Order order = Fixture.from(Order.class).uses(jpaProcessor).gimme("valid")
        def id = order.id

        when:
        def result = this.mvc.perform(get('/orders/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.number', is(notNullValue())))
    }
    private Order createPersistedOrder(OrderType type = OrderType.CREDIT, PaymentStatus status = PaymentStatus.PAID){
        def contractor = fixtureCreator.createContractor()
        def product = fixtureCreator.createProduct()
        def contract = fixtureCreator.createPersistedContract(contractor, product)
        installmentService.create(contract)
        def instrument = fixtureCreator.createInstrumentToProduct(product, contractor)
        return Fixture.from(Order.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("person", contractor.person)
            add("product", product)
            add("contract", contract)
            add("type", type)
            add("paymentInstrument", instrument)
            add("value", BigDecimal.ONE)
            add("status", status)
        }})
    }

}

