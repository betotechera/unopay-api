package br.com.unopay.api.network.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.GatheringChannel
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.geo.service.GeoService
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.network.model.BranchServicePeriod
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.network.model.ServicePeriodSituation
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.bootcommons.exception.ForbiddenException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class BranchServiceTest extends SpockApplicationTests {

    @Autowired
    BranchService service

    @Autowired
    BranchServicePeriodService periodService

    @Autowired
    FixtureCreator fixtureCreator

    GeoService geoService = Mock(GeoService)

    Establishment headOfficeUnderTest

    void setup(){
        headOfficeUnderTest = fixtureCreator.createHeadOffice()
        service.setGeoService(geoService)
    }

    def 'a valid branch should be created'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }

        when:
        Branch created = service.create(branch)

        then:
        created != null
    }

    def 'when creating a branch should be geo resolve it address'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }

        when:
        service.create(branch)

        then:
        1 * geoService.defineAddressLatLong(branch)
    }

    def 'when create the periods should be saved'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid", new Rule(){{
            add("headOffice", headOfficeUnderTest)
            add("servicePeriods", Fixture.from(BranchServicePeriod).gimme(1,"valid"))
        }})

        when:
        Branch created = service.create(branch)
        def result = periodService.findForBranch(created.id)

        then:
        !result.isEmpty()
    }

    def 'a valid branch with a head office from the current network should be create'(){
        given:
        def network = fixtureCreator.createNetwork()
        Branch branch = Fixture.from(Branch.class).gimme("valid")
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network", network)
        }})
        branch.headOffice = establishment
        when:
        def created = service.create(branch, network)
        def result = service.findById(created.id)
        then:
        result.headOffice.id == establishment.id
    }

    def 'a valid branch without a head office from the current network should be create'(){
        given:
        def network = fixtureCreator.createNetwork()
        Branch branch = Fixture.from(Branch.class).gimme("valid")
        branch.headOffice = Fixture.from(Establishment.class).gimme("valid")

        when:
        service.create(branch, network)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a valid branch should not be create to a unknown logged network'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid")

        when:
        service.create(branch, new AccreditedNetwork())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a branch address should be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        Branch created = service.create(branch)
        def newField = "teste"
        branch.address.streetName = newField

        when:
        service.update(created.id, branch)
        Branch result = service.findById(created.id)

        then:
        result.address.streetName == newField
    }

    def 'when update a period it should be updated'(){
        given:
        def periods = Fixture.from(BranchServicePeriod).gimme(1,"valid", new Rule(){{
            add("situation", ServicePeriodSituation.SUSPENDED)
        }})
        Branch branch = Fixture.from(Branch.class).gimme("valid", new Rule(){{
            add("headOffice", headOfficeUnderTest)
            add("servicePeriods", periods)
        }})
        def created = service.create(branch)
        created.servicePeriods.find().situation = ServicePeriodSituation.ACTIVE
        when:
        service.update(created.id, created)
        def result = periodService.findForBranch(created.id)

        then:
        result.find().situation == ServicePeriodSituation.ACTIVE
    }

    def 'A branch with a gathering channel should be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid", new Rule(){{
            add("headOffice", headOfficeUnderTest)
            add("gatheringChannels", [GatheringChannel.TEF])
        }})
        Branch created = service.create(branch)
        branch.gatheringChannels = [GatheringChannel.WEB] as Set

        when:
        service.update(created.id, branch)
        Branch result = service.findById(created.id)

        then:
        result.gatheringChannels.find() == GatheringChannel.WEB
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

    def 'a valid branch without an address should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.address = null
        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ADDRESS_REQUIRED'
    }

    def 'a valid branch without an address id should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.address.id = null

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ADDRESS_ID_REQUIRED'
    }

    def 'a valid branch with unknown address id should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def created = service.create(branch)
        branch.address.id = ''

        when:
        service.update(created.id, branch)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ADDRESS_NOT_FOUND'
    }

    def 'a valid branch should not be updated with a unknown logged network'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(branch.id, branch, new AccreditedNetwork())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid branch of an establishment of the same network should be updated'(){
        given:
        def network = fixtureCreator.createNetwork()
        Establishment headOffice = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
           add("network", network)
        }})
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", headOffice)
        }})
        branch.gatheringChannels = [GatheringChannel.MOBILE]
        when:
        service.update(branch.id, branch, network)
        Branch result = service.findById(branch.id)

        then:
        result.gatheringChannels.contains(GatheringChannel.MOBILE)
    }

    def 'a valid branch should not be updated with a different network'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")
        branch.headOffice.network = fixtureCreator.createNetwork()
        when:
        service.update(branch.id, branch, fixtureCreator.createNetwork())

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid branch should not be updated the network'(){
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

    def 'given a logged network different than the branch network should not be updated'(){
        given:
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(branch.id, branch, fixtureCreator.createNetwork())

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid branch without an address should not be created'(){
        given:
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        branch.address = null
        when:
        service.create(branch)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ADDRESS_REQUIRED'
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

    def 'a known branch of an establishment of the same network should be found'(){
        given:
        def network = fixtureCreator.createNetwork()
        Establishment headOffice = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network", network)
        }})
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", headOffice)
        }})
        when:
        Branch result = service.findById(branch.id, network)

        then:
        result != null
        result.headOffice.network.id == network.id
    }

    def 'a known branch of an establishment of another network should not be found'(){
        given:
        def network = fixtureCreator.createNetwork()
        Establishment headOffice = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        Branch branch = Fixture.from(Branch.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("headOffice", headOffice)
        }})
        when:
        service.findById(branch.id, network)

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
