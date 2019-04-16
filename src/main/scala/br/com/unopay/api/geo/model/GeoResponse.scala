package br.com.unopay.api.geo.model

import java.util

import scala.beans.BeanProperty

class GeoResponse {
  @BeanProperty
  var results: util.Collection[GeoAddress] = _
}

class GeoAddress {
  @BeanProperty
  var geometry: Geometry = _
}

class Geometry {
  @BeanProperty
  var location: Location = _
}

class Location {
  @BeanProperty
  var lat: Double = _

  @BeanProperty
  var lng: Double = _
}
