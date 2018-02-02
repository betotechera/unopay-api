package br.com.unopay.api.credit.receiver

import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.billing.creditcard.service.TransactionService
import static br.com.unopay.api.credit.model.CreditInsertionType.DIRECT_DEBIT
import br.com.unopay.api.credit.model.CreditProcessed
import static br.com.unopay.api.credit.model.CreditTarget.CLIENT
import static br.com.unopay.api.credit.model.CreditTarget.HIRER
import br.com.unopay.api.credit.service.CreditService
import br.com.unopay.api.util.GenericObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import static java.math.BigDecimal.ONE
import spock.lang.Specification

class CreditReceiverTest extends  Specification{

    ObjectMapper objectMapper = new ObjectMapper()
    GenericObjectMapper genericObjectMapper = new GenericObjectMapper(objectMapper)
    CreditService creditServiceMock = Mock(CreditService)
    TicketService boletoServiceMock = Mock(TicketService)
    TransactionService transactionServiceMock = Mock(TransactionService)

    def 'when receive credit for hirer should call credit service'(){
        given:
        def receiver = new CreditReceiver(creditServiceMock,genericObjectMapper, boletoServiceMock, transactionServiceMock)
        def valueAsString = objectMapper.writeValueAsString(new CreditProcessed("123", ONE, DIRECT_DEBIT, HIRER))
        when:
        receiver.creditReceiptNotify(valueAsString)

        then:
        1 * creditServiceMock.unblockCredit(_)
    }

    def 'when receive credit for client should call credit service'(){
        given:
        def receiver = new CreditReceiver(creditServiceMock,genericObjectMapper, boletoServiceMock, transactionServiceMock)
        def valueAsString = objectMapper.writeValueAsString(new CreditProcessed("123", ONE, DIRECT_DEBIT, CLIENT))
        when:
        receiver.creditReceiptNotify(valueAsString)

        then:
        0 * creditServiceMock.unblockCredit(_)
    }
}
