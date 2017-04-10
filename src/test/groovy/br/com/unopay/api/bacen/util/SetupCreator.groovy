package br.com.unopay.api.bacen.util

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.service.AccreditedNetworkService
import br.com.unopay.api.bacen.service.EstablishmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SetupCreator {

    @Autowired
    AccreditedNetworkService accreditedNetworkService

    @Autowired
    EstablishmentService establishmentService

    Establishment createHeadOffice() {
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        accreditedNetworkService.create(establishment.network)
        establishmentService.create(establishment)
    }

    AccreditedNetwork createNetwork() {
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        accreditedNetworkService.create(accreditedNetwork)
    }
}
