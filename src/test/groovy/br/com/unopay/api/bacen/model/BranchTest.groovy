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
        branchA.contactMail == branchB.contactMail
        branchA.branchPhotoUri == branchB.branchPhotoUri
        branchA.gatheringChannels == branchB.gatheringChannels
        branchA.address == branchB.address
        branchA.services == branchB.services
        branchA.technicalContact == branchB.technicalContact
        branchA.name == branchB.name
        branchA.shortName == branchB.shortName
        branchA.fantasyName == branchB.fantasyName
    }

    def 'should be equals'(){
        given:
        Branch a = Fixture.from(Branch.class).gimme("valid")

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        Branch a = Fixture.from(Branch.class).gimme("valid")
        Branch b = Fixture.from(Branch.class).gimme("valid")

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals

    }
}
