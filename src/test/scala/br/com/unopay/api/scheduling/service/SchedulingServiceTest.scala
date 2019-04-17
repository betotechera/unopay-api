package br.com.unopay.api.scheduling.service

import java.util.{Optional, UUID}

import br.com.six2six.fixturefactory.{Fixture, Rule}
import br.com.unopay.api.ScalaFixtureTest
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.junit.runner.RunWith
import org.mockito.Matchers.{any, isA}
import org.mockito.Mockito.{verify, when}
import org.mockito.{ArgumentCaptor, Matchers, Mockito}
import org.scalatest.junit.JUnitRunner
import org.springframework.data.domain.PageRequest

@RunWith(classOf[JUnitRunner])
class SchedulingServiceTest extends ScalaFixtureTest {

    private var schedulingService: SchedulingService = _

    var mockSchedulingRepository: SchedulingRepository = _
    var mockContractorService: ContractorService = _
    var mockContractService: ContractService = _
    var mockBranchService: BranchService = _
    var mockAuthorizedMemberService: AuthorizedMemberService = _
    var mockPaymentInstrumentService: PaymentInstrumentService = _
    var mockUserDetailService: UserDetailService = _

    override def beforeEach() {
        super.beforeEach()

        mockSchedulingRepository = Mockito.mock(classOf[SchedulingRepository])
        mockContractorService = Mockito.mock(classOf[ContractorService])
        mockContractService = Mockito.mock(classOf[ContractService])
        mockBranchService = Mockito.mock(classOf[BranchService])
        mockAuthorizedMemberService = Mockito.mock(classOf[AuthorizedMemberService])
        mockPaymentInstrumentService = Mockito.mock(classOf[PaymentInstrumentService])
        mockUserDetailService = Mockito.mock(classOf[UserDetailService])

        schedulingService = new SchedulingService(mockSchedulingRepository, mockContractorService, mockContractService,
            mockBranchService, mockAuthorizedMemberService, mockPaymentInstrumentService, mockUserDetailService)
    }

    it should "find scheduling by id" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        when(mockSchedulingRepository.findById(any())).thenReturn(Optional.of(scheduling))

        assert(schedulingService.findById("1244AABBSS") != null)
    }

    it should "throw exception when not found scheduling" in {
        when(mockSchedulingRepository.findById(any())).thenReturn(Optional.empty[Scheduling]())
        assertThrows[NotFoundException] {
            schedulingService.findById("1244AABBSS")
        }
    }

    it should "create a Scheduling" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        mockReturnReferences(scheduling)

        schedulingService.create(scheduling)

        verify(mockSchedulingRepository).save(scheduling)
        expectCallReferences(scheduling)
    }

    it should "update a Scheduling" in {
        val id = UUID.randomUUID().toString

        val actualScheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})
        val otherScheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        when(mockSchedulingRepository.findById(id)).thenReturn(Optional.of(actualScheduling))

        mockReturnReferences(actualScheduling)

        schedulingService.update(id, otherScheduling)

        val schedulingUpdated = ArgumentCaptor.forClass(classOf[Scheduling])

        Mockito.verify(mockSchedulingRepository).save(schedulingUpdated.capture())
        expectCallReferences(actualScheduling)
        assert(actualScheduling.id eq schedulingUpdated.getValue.id)
        assert(actualScheduling.token eq otherScheduling.token)
    }

    it should "delete a Scheduling" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})

        when(mockSchedulingRepository.findById(id)).thenReturn(Optional.of(scheduling))

        schedulingService.deleteById(id)

        Mockito.verify(mockSchedulingRepository).delete(scheduling)
    }

    it should "filter schedules" in {
        val schedulingFilter = new SchedulingFilter
        val pageRequest = new UnovationPageRequest

        schedulingService.findAll(schedulingFilter, pageRequest)

        Mockito.verify(mockSchedulingRepository).findAll(Matchers.eq(schedulingFilter), isA(classOf[PageRequest]))
    }

    private def expectCallReferences(scheduling: Scheduling) = {
        verify(mockContractorService).getById(scheduling.getContractor.getId)
        verify(mockContractService).findById(scheduling.contract.getId)
        verify(mockBranchService).findById(scheduling.branch.getId)
        verify(mockAuthorizedMemberService).findById(scheduling.authorizedMember.getId)
        verify(mockPaymentInstrumentService).findById(scheduling.paymentInstrument.getId)
        verify(mockUserDetailService).getById(scheduling.user.getId)
    }

    private def mockReturnReferences(scheduling: Scheduling) = {
        when(mockContractorService.getById(scheduling.getContractor.getId)).thenReturn(scheduling.contractor)
        when(mockContractService.findById(scheduling.contract.getId)).thenReturn(scheduling.contract)
        when(mockBranchService.findById(scheduling.branch.getId)).thenReturn(scheduling.branch)
        when(mockAuthorizedMemberService.findById(scheduling.authorizedMember.getId))
                .thenReturn(scheduling.authorizedMember)
        when(mockPaymentInstrumentService.findById(scheduling.paymentInstrument.getId))
                .thenReturn(scheduling.paymentInstrument)
        when(mockUserDetailService.getById(scheduling.user.getId)).thenReturn(scheduling.user)
    }
}
