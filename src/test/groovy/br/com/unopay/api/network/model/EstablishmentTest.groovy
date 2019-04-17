package br.com.unopay.api.network.model

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.network.model.Establishment

class EstablishmentTest extends FixtureApplicationTest {


    def 'should update all fields'(){
        given:
        Establishment establishmentA = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("person.id", '1')
        }})
        Establishment establishmentB = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("person.id", '1')
        }})

        when:
        establishmentA.updateMe(establishmentB)

        then:
        establishmentA.administrativeContact == establishmentB.administrativeContact
        establishmentA.alternativeMail == establishmentB.alternativeMail
        establishmentA.bachShipmentMail == establishmentB.bachShipmentMail
        establishmentA.bankAccount == establishmentB.bankAccount
        establishmentA.checkout == establishmentB.checkout
        establishmentA.contactMail == establishmentB.contactMail
        establishmentA.contractUri == establishmentB.contractUri
        establishmentA.facadePhotoUri == establishmentB.facadePhotoUri
        establishmentA.cancellationTolerance == establishmentB.cancellationTolerance
        establishmentA.financierContact == establishmentB.financierContact
        establishmentA.invoiceMail == establishmentB.invoiceMail
        establishmentA.logoUri == establishmentB.logoUri
        establishmentA.person.cellPhone == establishmentB.person.cellPhone
        establishmentA.person.name == establishmentB.person.name
        establishmentA.fee == establishmentB.fee
        establishmentA.network.id == establishmentB.network.id
        establishmentA.type == establishmentB.type
        establishmentA.operationalContact == establishmentB.operationalContact
        establishmentA.technicalContact == establishmentB.technicalContact
    }

    def 'should be equals'(){
        given:
        Establishment establishmentA = Fixture.from(Establishment.class).gimme("valid")

        when:
        def shouldBeEquals = establishmentA == establishmentA

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Establishment establishmentA = Fixture.from(Establishment.class).gimme("valid")
        Establishment establishmentB = Fixture.from(Establishment.class).gimme("valid")

        when:
        def shouldBeEquals = establishmentA == establishmentB

        then:
        !shouldBeEquals

    }
}