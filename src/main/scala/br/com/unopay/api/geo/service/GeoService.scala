package br.com.unopay.api.geo.service

import java.net.URLEncoder
import java.util.Optional

import br.com.unopay.api.geo.model.{GeoResponse, Localizable, Location}
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.uaa.exception.Errors
import br.com.unopay.bootcommons.exception.UnovationExceptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import scala.collection.JavaConverters._

@Profile(Array("!test"))
@Service
class GeoService() {

  val restTemplate: RestTemplate = new RestTemplate()
  @Value("${google.url:}")
  private var googleUrl: String = _

  @Value("${google.key:}")
  private var googleKey: String = _

  private val encode: String = "UTF-8"

  def defineAddressLatLong(localizable: Localizable): Unit = {
    val locationOptional = findLocation(localizable.formattedAddress)
    val location = locationOptional.orElseThrow(() => UnovationExceptions.notFound.withErrors(Errors.GOOGLE_ADDRESS_NOT_FOUND))
    localizable.defineAddressLat(location.lat)
    localizable.defineAddressLong(location.lng)
  }

  private def findLocation(address: String) = {
    val query = URLEncoder.encode(address, encode)
    val result = restTemplate.getForEntity(s"$googleUrl?key=$googleKey&address=$query", classOf[GeoResponse]).getBody
    if (result.results.isEmpty) {
      Optional.empty[Location]()
    } else {
      Optional.of[Location](result.getResults.asScala.head.geometry.location)
    }
  }

}
