package br.com.unopay.api.scheduling.controller

import java.util.UUID

import br.com.six2six.fixturefactory.{Fixture, Rule}
import br.com.six2six.fixturefactory.loader.TemplateLoader
import br.com.unopay.api.scheduling.model.Scheduling


class SchedulingTemplateLoader extends TemplateLoader {

    override def load(): Unit = {
        Fixture.of(classOf[Scheduling]).addTemplate("valid", new Rule() {
            add("token", UUID.randomUUID().toString)
        })
    }
}
