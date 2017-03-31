package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Institution
import br.com.unopay.api.bacen.model.InstitutionFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class AccreditedNetworkServiceTest extends SpockApplicationTests {

    @Autowired
    AccreditedNetworkService service

    void 'should create AccreditedNetwork'(){
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")

        when:
        accreditedNetwork =  service.create(accreditedNetwork)
        AccreditedNetwork result  = service.getById(accreditedNetwork.getId())

        then:
        result != null
    }

}
