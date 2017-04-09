package br.com.unopay.api.bacen.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest

class BranchTest extends FixtureApplicationTest{

    def 'should update all fields'(){
        given:
        Branch branchA = Fixture.from(Branch.class).gimme("valid")
        Branch branchB = Fixture.from(Branch.class).gimme("valid")

        when:
        branchA.updateMe(branchB)

        then:
        branchA.headOffice == branchB.headOffice
        branchA.alternativeMail == branchB.alternativeMail
        branchA.bankAccount == branchB.bankAccount
        branchA.checkout == branchB.checkout
        branchA.contactMail == branchB.contactMail
        branchA.contractUri == branchB.contractUri
        branchA.branchPhotoUri == branchB.branchPhotoUri
        branchA.cancellationTolerance == branchB.cancellationTolerance
        branchA.gatheringChannel == branchB.gatheringChannel
        branchA.invoiceMail == branchB.invoiceMail
        branchA.person == branchB.person
        branchA.tax == branchB.tax
        branchA.technicalContact == branchB.technicalContact
    }
}
