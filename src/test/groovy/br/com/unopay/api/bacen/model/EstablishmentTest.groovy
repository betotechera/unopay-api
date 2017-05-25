package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class EstablishmentTest extends FixtureApplicationTest {


    def 'should update all fields'(){
        given:
        Establishment establishmentA = Fixture.from(Establishment.class).gimme("valid")
        Establishment establishmentB = Fixture.from(Establishment.class).gimme("valid")

        when:
        establishmentA.updateMe(establishmentB)

        then:
        establishmentA.administrativeContact == establishmentB.administrativeContact
        establishmentA.alternativeMail == establishmentB.alternativeMail
        establishmentA.bachShipmentMail == establishmentB.bachShipmentMail
        establishmentA.bankAccount == establishmentB.bankAccount
        establishmentA.brandFlag == establishmentB.brandFlag
        establishmentA.checkout == establishmentB.checkout
        establishmentA.contactMail == establishmentB.contactMail
        establishmentA.contractUri == establishmentB.contractUri
        establishmentA.establishmentPhotoUri == establishmentB.establishmentPhotoUri
        establishmentA.cancellationTolerance == establishmentB.cancellationTolerance
        establishmentA.financierContact == establishmentB.financierContact
        establishmentA.invoiceMail == establishmentB.invoiceMail
        establishmentA.logoUri == establishmentB.logoUri
        establishmentA.person.name == establishmentB.person.name
        establishmentA.tax == establishmentB.tax
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
