package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.Branch
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.ForbiddenException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class BranchServiceTest extends SpockApplicationTests {

    @Autowired
    BranchService service

    @Autowired
    EstablishmentService establishmentService

    @Autowired
    AccreditedNetworkService accreditedNetworkService

    @Autowired
    FixtureCreator fixtureCreator

    Establishment headOfficeUnderTest

    void setup(){
        headOfficeUnderTest = fixtureCreator.createHeadOffice()
    }

    def 'a valid branch should be created'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }

        when:
        Branch created = service.create(branch)

        then:
        created != null
    }

    def 'a branch person should be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        Branch created = service.create(branch)
        def newField = "teste"
        branch.person.name = newField

        when:
        service.update(created.id, branch)
        Branch result = service.findById(created.id)

        then:
        result.person.name == newField
    }

    def 'a branch head office should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        Branch created = service.create(branch)
        def newField = "teste"
        branch.headOffice.contactMail = newField

        when:
        service.update(created.id, branch)
        Branch result = service.findById(created.id)

        then:
        result.headOffice.contactMail != newField
    }

    def 'a valid branch without person should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.person = null
        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid branch without person id should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.person.id = null

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_ID_REQUIRED'
    }

    def 'a valid branch with unknown person id should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.person.id = ''

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PERSON_NOT_FOUND'
    }

    def 'a valid establishment should not be updated with a unknown logged network'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(branch.id, branch, new AccreditedNetwork())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment should not be updated with a different network'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")
        branch.headOffice.network = fixtureCreator.createNetwork()
        when:
        service.update(branch.id, branch, fixtureCreator.createNetwork())

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid establishment should not be updated the network'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")
        def newNetwork = fixtureCreator.createNetwork()
        branch.headOffice.network = newNetwork
        when:
        service.update(branch.id, branch, newNetwork)

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'given a logged network different than the establishment network should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(branch.id, branch, fixtureCreator.createNetwork())

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid branch without person should not be created'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        branch.person = null
        when:
        service.create(branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PERSON_REQUIRED'
    }

    def 'a valid branch without headOffice should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.headOffice = null

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'HEAD_OFFICE_REQUIRED'
    }

    def 'a valid branch without headOffice id should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.headOffice.id = null

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CANNOT_CHANGE_HEAD_OFFICE'
    }

    def 'a branch with changed headOffice should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def newHeadOffice = fixtureCreator.createHeadOffice()
        def created = service.create(branch)
        branch.headOffice = newHeadOffice

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CANNOT_CHANGE_HEAD_OFFICE'
    }

    def 'a valid branch without headOffice should not be created'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        branch.headOffice = null
        when:
        service.create(branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'HEAD_OFFICE_REQUIRED'
    }

    def 'a known branch should be found'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        Branch created = service.create(branch)

        when:
        Branch result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown branch should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BRANCH_NOT_FOUND'
    }

    def 'a known branch should be deleted'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        Branch created = service.create(branch)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BRANCH_NOT_FOUND'
    }

    def 'a unknown branch should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BRANCH_NOT_FOUND'
    }


}
