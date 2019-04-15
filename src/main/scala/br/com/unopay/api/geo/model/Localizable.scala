package br.com.unopay.api.geo.model

trait Localizable {

  def defineAddressLat(lat: Double)

  def defineAddressLong(lng: Double)

  def formattedAddress: String

}
