package br.com.unopay.api.scheduling

import java.util.Date

import br.com.six2six.fixturefactory.loader.TemplateLoader
import br.com.six2six.fixturefactory.{Fixture, Rule}
import br.com.unopay.api.`implicit`.DateImplicit._
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.{Contract, PaymentInstrument}
import br.com.unopay.api.network.model.{Branch, ServiceType}
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.util.TokenFactory

class SchedulingTemplateLoader extends TemplateLoader {

    override def load(): Unit = {
        val contract: Contract = createContract()

        Fixture.of(classOf[Scheduling]).addTemplate("valid", new Rule() {
            add("token", TokenFactory.generateToken())
            add("date", new Date().plusDays(10))
            add("branch", createBranch)
            add("contract", contract)
            add("contractor", contract.getContractor)
            add("paymentInstrument", createPaymentInstrument)
            add("serviceDescription", name())
            add("serviceType", random(classOf[ServiceType]))
            add("user", createUser)
            add("authorizedMember", createAuthorizedMember)
        })

        Fixture.of(classOf[Scheduling]).addTemplate("invalid", new Rule() {
            add("token", TokenFactory.generateToken())
        })
    }

    private def createAuthorizedMember = {
        val authorizedMember = new AuthorizedMember
        authorizedMember.setId("6")
        authorizedMember
    }

    private def createUser = {
        val user = new UserDetail()
        user.setId("5")
        user
    }

    private def createPaymentInstrument = {
        val paymentInstrument = new PaymentInstrument()
        paymentInstrument.setId("4")
        paymentInstrument
    }

    private def createContract() = {
        val contract = new Contract()
        contract.setId("3")
        contract.setContractor(createContractor)
        contract
    }

    private def createContractor = {
        val contractor = new Contractor()
        contractor.setId("2")
        contractor
    }

    private def createBranch = {
        val branch = new Branch()
        branch.setId("1")
        branch
    }
}
