package br.com.unopay.api.network.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.network.model.filter.BranchFilter
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import static org.hamcrest.Matchers.hasSize
import org.springframework.beans.factory.annotation.Autowired
import static spock.util.matcher.HamcrestSupport.that

class EstablishmentBranchServiceTest extends SpockApplicationTests{

    private AccreditedNetwork networkUnderTest

    @Autowired
    private FixtureCreator fixtureCreator

    @Autowired
    private EstablishmentBranchService service
    @Autowired
    private BranchService branchService

    void setup(){
        networkUnderTest = fixtureCreator.createNetwork()
    }

    def 'when creating a establishment with the createBranch option should create it'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", true)
        }})
        when:
        Establishment created = service.create(establishment)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(1)
    }

    def 'given a existing establishment with the createBranch option should create it'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", true)
        }})
        when:
        Establishment created = service.create(establishment)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(1)
    }

    def 'when creating a establishment without the createBranch option should not create it'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", false)
        }})
        when:
        Establishment created = service.create(establishment)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(0)
    }

    def 'when creating a establishment with the createBranch option should create it for newtwork'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", true)
        }})
        when:
        Establishment created = service.create(establishment, networkUnderTest)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(1)
    }

    def 'given a existing establishment with the createBranch option should create it for network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", true)
        }})
        when:
        Establishment created = service.create(establishment, networkUnderTest)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(1)
    }

    def 'when creating a establishment without the createBranch option should not create it for network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("createBranch", false)
        }})
        when:
        Establishment created = service.create(establishment, networkUnderTest)
        def branchResult = branchService.findByFilter(new BranchFilter(){{ setHeadOffice( created.getId())}}, new UnovationPageRequest())

        then:
        that branchResult.getContent(), hasSize(0)
    }
}
