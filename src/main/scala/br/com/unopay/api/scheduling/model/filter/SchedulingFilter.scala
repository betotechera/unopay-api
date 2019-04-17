package br.com.unopay.api.scheduling.model.filter

import lombok.Data

import scala.beans.BeanProperty

@Data
class SchedulingFilter {

    @BeanProperty
    var token: String = _

}
