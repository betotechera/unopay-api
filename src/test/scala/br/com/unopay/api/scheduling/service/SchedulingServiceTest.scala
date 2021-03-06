package br.com.unopay.api.scheduling.service

import java.util.{Optional, UUID}

import br.com.six2six.fixturefactory.{Fixture, Rule}
import br.com.unopay.api.ScalaFixtureTest
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.service.{BranchService, EstablishmentEventService}
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.junit.runner.RunWith
import org.mockito.Matchers.{any, isA}
import org.mockito.Mockito._
import org.mockito.{ArgumentCaptor, Matchers}
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
    var mockEstablishmentEventService: EstablishmentEventService = _

    override def beforeEach() {
        super.beforeEach()

        mockSchedulingRepository = mock(classOf[SchedulingRepository])
        mockContractorService = mock(classOf[ContractorService])
        mockContractService = mock(classOf[ContractService])
        mockBranchService = mock(classOf[BranchService])
        mockAuthorizedMemberService = mock(classOf[AuthorizedMemberService])
        mockPaymentInstrumentService = mock(classOf[PaymentInstrumentService])
        mockEstablishmentEventService = mock(classOf[EstablishmentEventService])

        schedulingService = new SchedulingService(mockSchedulingRepository, mockContractorService, mockContractService,
            mockBranchService, mockAuthorizedMemberService, mockPaymentInstrumentService, mockEstablishmentEventService)
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

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(captor.capture())

        val schedulingSaved = captor.getValue

        assert(schedulingSaved.token != null, "Token should not be null")
        assert(schedulingSaved.expirationDate != null, "ExpirationDate should not be null")
        expectCallReferences(scheduling)
    }

    it should "create a Scheduling and verify token" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        mockReturnReferences(scheduling)

        schedulingService.create(scheduling)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(captor.capture())

        val schedulingSaved = captor.getValue

        assert(schedulingSaved.token != null, "Token should not be null")
        expectCallReferences(scheduling)
    }

    it should "create a Scheduling and ignore passed token" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        val initialToken = "GDF87BVKN"
        scheduling.token = initialToken
        mockReturnReferences(scheduling)

        schedulingService.create(scheduling)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(captor.capture())

        val schedulingSaved = captor.getValue

        assert(schedulingSaved.token != initialToken, "Token should not be equal")
        expectCallReferences(scheduling)
    }

    it should "create a Scheduling by accreditedNetwork" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        val network: AccreditedNetwork = Fixture.from(classOf[AccreditedNetwork]).gimme("valid")
        mockReturnReferences(scheduling, network)

        schedulingService.create(scheduling, network)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(captor.capture())

        val schedulingSaved = captor.getValue

        assert(schedulingSaved.token != null, "Token should not be null")
        assert(schedulingSaved.expirationDate != null, "ExpirationDate should not be null")

        expectCallReferences(scheduling, network)
    }

    it should "update a Scheduling  by accredit network" in {
        val id = UUID.randomUUID().toString

        val actualScheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})
        val otherScheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")


        val network: AccreditedNetwork = Fixture.from(classOf[AccreditedNetwork]).gimme("valid")
        when(mockSchedulingRepository.findByIdAndBranchHeadOfficeNetworkId(id, network.getId))
                .thenReturn(Optional.of(actualScheduling))
        mockReturnReferences(actualScheduling, network)

        schedulingService.update(id, otherScheduling, network)

        val schedulingUpdated = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(schedulingUpdated.capture())
        expectCallReferences(actualScheduling, network)
        assert(id eq schedulingUpdated.getValue.id, "Current id can not change")
        assert(actualScheduling.date eq otherScheduling.date, "Date must be updated")
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

        verify(mockSchedulingRepository).save(schedulingUpdated.capture())
        expectCallReferences(actualScheduling)
        assert(id eq schedulingUpdated.getValue.id, "Current id can not change")
        assert(actualScheduling.date eq otherScheduling.date, "Date must be updated")
    }

    it should "cancel a Scheduling by accredit network" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})

        val network: AccreditedNetwork = Fixture.from(classOf[AccreditedNetwork]).gimme("valid")

        when(mockSchedulingRepository.findByIdAndBranchHeadOfficeNetworkId(id, network.getId))
                .thenReturn(Optional.of(scheduling))

        schedulingService.cancelById(id, network)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])
        verify(mockSchedulingRepository).save(captor.capture())
        assert(captor.getValue.cancellationDate != null, "Cancellation date should not be null")
    }

    it should "cancel a Scheduling by Contractor" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})

        val contractor: Contractor = Fixture.from(classOf[Contractor]).gimme("valid")

        when(mockSchedulingRepository.findByIdAndContractorId(id, contractor.getId))
                .thenReturn(Optional.of(scheduling))

        schedulingService.cancelById(id, contractor)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])
        verify(mockSchedulingRepository).save(captor.capture())
        assert(captor.getValue.cancellationDate != null, "Cancellation date should not be null")
    }

    it should "cancel a Scheduling" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})

        when(mockSchedulingRepository.findById(id))
          .thenReturn(Optional.of(scheduling))

        schedulingService.cancelById(id)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])
        verify(mockSchedulingRepository).save(captor.capture())
        assert(captor.getValue.cancellationDate != null, "Cancellation date should not be null")
    }

    it should "delete a Scheduling" in {
        val id = UUID.randomUUID().toString
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid", new Rule {{
            add("id", id)
        }})

        when(mockSchedulingRepository.findById(id)).thenReturn(Optional.of(scheduling))

        schedulingService.deleteById(id)

        verify(mockSchedulingRepository).delete(scheduling)
    }

    it should "filter schedules" in {
        val schedulingFilter = new SchedulingFilter
        val pageRequest = new UnovationPageRequest

        schedulingService.findAll(schedulingFilter, pageRequest)

        verify(mockSchedulingRepository).findAll(Matchers.eq(schedulingFilter), isA(classOf[PageRequest]))
    }

    it should "filter schedules for contractor" in {
        val loggedContractor = new Contractor
        val schedulingFilter = new SchedulingFilter
        val pageRequest = new UnovationPageRequest

        schedulingService.findAll(schedulingFilter, loggedContractor, pageRequest)

        verify(mockSchedulingRepository).findAll(Matchers.eq(schedulingFilter), isA(classOf[PageRequest]))
    }

    it should "find scheduling by id for logged contractor" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")

        when(mockSchedulingRepository.findByIdAndContractorId(any(), any())).thenReturn(Optional.of(scheduling))

        assert(schedulingService.findById("1244AABBSS", scheduling.contractor) != null)
    }

    it should "throw exception when not found scheduling for logged contractor" in {
        when(mockSchedulingRepository.findByIdAndContractorId(any(), any())).thenReturn(Optional.empty[Scheduling]())
        assertThrows[NotFoundException] {
            schedulingService.findById("1244AABBSS", new Contractor)
        }
    }

    it should "create a Scheduling by contractor" in {
        val scheduling: Scheduling = Fixture.from(classOf[Scheduling]).gimme("valid")
        val contractor = scheduling.contractor

        mockReturnReferences(scheduling, contractor)

        schedulingService.create(scheduling, contractor)

        val captor = ArgumentCaptor.forClass(classOf[Scheduling])

        verify(mockSchedulingRepository).save(captor.capture())

        val schedulingSaved = captor.getValue

        assert(schedulingSaved.token != null, "Token should not be null")
        assert(schedulingSaved.expirationDate != null, "ExpirationDate should not be null")

        expectCallReferences(scheduling, contractor)
    }

    private def expectCallReferences(scheduling: Scheduling) = {
        verify(mockBranchService, never()).findById(Matchers.eq(scheduling.branchId()), Matchers.any(classOf[AccreditedNetwork]))
        verify(mockContractorService).getById(scheduling.getContractor.getId)
        verify(mockContractService).findById(scheduling.contract.getId)
        verify(mockBranchService).findById(scheduling.branch.getId)
        verify(mockAuthorizedMemberService).findById(scheduling.authorizedMember.getId)
        verify(mockPaymentInstrumentService).findById(scheduling.paymentInstrument.getId)
    }

    private def expectCallReferences(scheduling: Scheduling, network: AccreditedNetwork) = {
        verify(mockBranchService).findById(scheduling.branchId(), network)
        verify(mockContractorService).getByIdForNetwork(scheduling.contractorId(), network)
        verify(mockContractorService).getByIdForConctract(scheduling.contractorId(), scheduling.getContract)
        verify(mockPaymentInstrumentService).findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId())

        verify(mockContractService).findById(scheduling.contract.getId)
        verify(mockAuthorizedMemberService).findById(scheduling.authorizedMember.getId)

        verify(mockBranchService, never()).findById(scheduling.branch.getId)
        verify(mockPaymentInstrumentService, never()).findById(scheduling.paymentInstrument.getId)
    }

    private def expectCallReferences(scheduling: Scheduling, contractor: Contractor) = {
        verify(mockBranchService).findById(scheduling.branchId())
        verify(mockPaymentInstrumentService).findByIdAndContractorId(scheduling.instrumentId(), contractor.getId)
        verify(mockContractService).findById(scheduling.contract.getId)
        verify(mockAuthorizedMemberService).findById(scheduling.authorizedMember.getId)
    }

    private def mockReturnReferences(scheduling: Scheduling) = {
        when(mockContractorService.getById(scheduling.getContractor.getId)).thenReturn(scheduling.contractor)
        when(mockBranchService.findById(scheduling.branch.getId)).thenReturn(scheduling.branch)
        when(mockPaymentInstrumentService.findById(scheduling.paymentInstrument.getId))
                .thenReturn(scheduling.paymentInstrument)

        when(mockAuthorizedMemberService.findById(scheduling.authorizedMember.getId))
                .thenReturn(scheduling.authorizedMember)
        when(mockContractService.findById(scheduling.contract.getId)).thenReturn(scheduling.contract)
    }

    def mockReturnReferences(scheduling: Scheduling, network: AccreditedNetwork) : Unit = {
        when(mockBranchService.findById(scheduling.branchId(), network)).thenReturn(scheduling.branch)
        when(mockContractorService.getByIdForConctract(scheduling.contractorId(), scheduling.getContract))
                .thenReturn(scheduling.contractor)
        when(mockPaymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId()))
                .thenReturn(scheduling.paymentInstrument)

        when(mockAuthorizedMemberService.findById(scheduling.authorizedMember.getId))
                .thenReturn(scheduling.authorizedMember)
        when(mockContractService.findById(scheduling.contract.getId)).thenReturn(scheduling.contract)
    }

    def mockReturnReferences(scheduling: Scheduling, contractor: Contractor) : Unit = {
        when(mockBranchService.findById(scheduling.branchId())).thenReturn(scheduling.branch)
        when(mockPaymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), contractor.getId))
          .thenReturn(scheduling.paymentInstrument)
        when(mockAuthorizedMemberService.findById(scheduling.authorizedMember.getId))
          .thenReturn(scheduling.authorizedMember)
        when(mockContractService.findById(scheduling.contract.getId)).thenReturn(scheduling.contract)
    }
}
