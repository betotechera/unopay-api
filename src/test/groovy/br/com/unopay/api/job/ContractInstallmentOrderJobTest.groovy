package br.com.unopay.api.job

import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.config.Queues
import br.com.unopay.api.infra.Notifier
import br.com.unopay.api.model.ContractInstallment
import br.com.unopay.api.order.service.OrderService
import br.com.unopay.api.service.ContractInstallmentService
import br.com.unopay.bootcommons.exception.BaseException
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired

class ContractInstallmentOrderJobTest extends SpockApplicationTests {

    @Autowired
    OrderService orderService

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    ContractInstallmentService contractInstallmentService

    private Notifier notifierMock = Mock(Notifier)

    ContractInstallmentOrderJob job

    ContractInstallment installmentUnderTest

    @Override
    void setup() {
        orderService.notifier = notifierMock
        job = new ContractInstallmentOrderJob(contractInstallmentService, orderService)

        def hirer = fixtureCreator.createHirer()
        def product = fixtureCreator.createProductWithSameIssuerOfHirer(BigDecimal.TEN, hirer)
        def contractUnderTest = fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                product,hirer)
        contractInstallmentService.create(contractUnderTest)
        installmentUnderTest = contractInstallmentService.findByContractId(contractUnderTest.id).first()
    }

    def "Should create order for contract Installment that will expire in deadline config days when is a PF product"() {
        given:
        contractInstallmentService.update(installmentUnderTest.id, installmentUnderTest.with {
            it.expiration = LocalDate.now().plusDays(contractInstallmentService.boletoDeadlineInDays)
                    .toDate()
            it
        })


        when:
        job.execute()
        then:
        notThrown(BaseException)
        1 * notifierMock.notify(Queues.ORDER_CREATED, _)
    }


    def "Should create order for contract Installment that will expire in deadline config days when is a PF product get all day interval"() {
        given:
        contractInstallmentService.update(installmentUnderTest.id, installmentUnderTest.with {
            it.expiration = LocalDateTime.now().plusDays(contractInstallmentService.boletoDeadlineInDays)
            .withTime(13,54,10,2)
                    .toDate()
            it
        })


        when:
        job.execute()
        then:
        notThrown(BaseException)
        1 * notifierMock.notify(Queues.ORDER_CREATED, _)
    }


    def "Should not create order for contract Installment that will expire in deadline config days but is a PJ product"() {
        given:
        contractInstallmentService.update(installmentUnderTest.id, installmentUnderTest.with {
            it.expiration = LocalDate.now().plusDays(contractInstallmentService.boletoDeadlineInDays)
                    .toDate()
            it.contract = fixtureCreator.createPersistedContract()
            it
        })


        when:
        job.execute()
        then:
        notThrown(BaseException)
        0 * notifierMock.notify(_, _)
    }


    def "Should  not create order for contract Installment that will expire in deadline days but is paid"() {
        given:
        contractInstallmentService.update(installmentUnderTest.id, installmentUnderTest.with {
            it.expiration = LocalDate.now().plusDays(contractInstallmentService.boletoDeadlineInDays).toDate()
            it.paymentDateTime = new Date()
            it
        })

        when:
        job.execute()
        then:
        notThrown(BaseException)
        0 * notifierMock.notify(_ , _)
    }

    def "Should  not create order for contract Installment that will expire in deadline days plus one day"() {
        given:
        contractInstallmentService.update(installmentUnderTest.id, installmentUnderTest.with {
            it.expiration = LocalDate.now().plusDays(contractInstallmentService.boletoDeadlineInDays + 1).toDate()
            it
        })

        when:
        job.execute()
        then:
        notThrown(BaseException)
        0 * notifierMock.notify(_ , _)
    }

}
