package br.com.unopay.api.billing.creditcard.gateway.payzen.model

import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.creditcard.model.TransactionStatus
import com.lyra.vads.ws.v5.CommonResponse
import com.lyra.vads.ws.v5.CreatePaymentResponse
import eu.payzen.webservices.sdk.ServiceResult
import spock.lang.Unroll

class PayzenResponseTranslatorTest extends FixtureApplicationTest {

    @Unroll
    'given a code #expedcodeCode should return authorized status'(){
        given:
        def code = expedcodeCode
        def commonResponse = new CommonResponse() {{ setResponseCode(code) }}
        def paymentResult = new CreatePaymentResponse.CreatePaymentResult() {{ setCommonResponse(commonResponse) }}
        def result = new ServiceResult(paymentResult, null)

        when:
        def status = new PayzenResponseTranslator().translate(result)

        then:
        status == TransactionStatus.AUTHORIZED

        where:
        _ | expedcodeCode
        _ | 0
    }

    @Unroll
    'given a code #expedcodeCode should return error status'(){
        given:
        def code = expedcodeCode
        def commonResponse = new CommonResponse() {{ setResponseCode(code) }}
        def paymentResult = new CreatePaymentResponse.CreatePaymentResult() {{ setCommonResponse(commonResponse) }}
        def result = new ServiceResult(paymentResult, null)

        when:
        def status = new PayzenResponseTranslator().translate(result)

        then:
        status == TransactionStatus.ERROR

        where:
        _ | expedcodeCode
        _ | 2
        _ | 3
        _ | 10
        _ | 11
        _ | 12
        _ | 13
        _ | 14
        _ | 15
        _ | 20
        _ | 21
        _ | 22
        _ | 23
        _ | 24
        _ | 25
        _ | 30
        _ | 31
        _ | 32
        _ | 33
        _ | 34
        _ | 35
        _ | 36
        _ | 40
        _ | 41
        _ | 50
        _ | 51
        _ | 52
        _ | 53
        _ | 54
        _ | 55
        _ | 56
        _ | 97
        _ | 98
        _ | 99
    }

    @Unroll
    'given a code #expedcodeCode should return denied status'(){
        given:
        def code = expedcodeCode
        def commonResponse = new CommonResponse() {{ setResponseCode(code) }}
        def paymentResult = new CreatePaymentResponse.CreatePaymentResult() {{ setCommonResponse(commonResponse) }}
        def result = new ServiceResult(paymentResult, null)

        when:
        def status = new PayzenResponseTranslator().translate(result)

        then:
        status == TransactionStatus.DENIED

        where:
        _ | expedcodeCode
        _ | 1
        _ | 42
        _ | 43
        _ | 26
    }
}
