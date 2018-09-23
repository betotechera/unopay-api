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
        job = new ContractInstallmentOrderJob(contractInstallmentService,orderService)
        def contractUnderTest = fixtureCreator.createPersistedContract(fixtureCreator.createContractor(),
                fixtureCreator.createProductWithSameIssuerOfHirer())
        contractInstallmentService.create(contractUnderTest)
        installmentUnderTest = contractInstallmentService.findByContractId(contractUnderTest.id).find()
    }

    def "Should create order for contract Installment that will expire in deadline config days"() {
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

    def "Should  not create order for contract Installment that will expire in deadline days but is pid"() {
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
