package br.com.unopay.api.util

import br.com.unopay.api.geo.model.Localizable
import br.com.unopay.api.geo.service.GeoService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile(Array("test"))
@Component
class GeoMock extends GeoService {


  override def defineAddressLatLong(localizable: Localizable): Unit = {}

}
