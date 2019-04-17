package br.com.unopay.api.scheduling.service

import java.util.Optional

import br.com.unopay.api.ScalaFixtureTest
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.{NotFoundException, UnovationExceptions}
import org.junit.runner.RunWith
import org.mockito.{Matchers, Mockito}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SchedulingServiceTest extends ScalaFixtureTest {

    private var schedulingService: SchedulingService = _

    var schedulingRepository: SchedulingRepository = _
    var contractorService: ContractorService = _
    var contractService: ContractService = _
    var branchService: BranchService = _
    var authorizedMemberService: AuthorizedMemberService = _
    var paymentInstrumentService: PaymentInstrumentService = _
    var userDetailService: UserDetailService = _

    override def beforeEach() {
        super.beforeEach()

        schedulingRepository = Mockito.mock(classOf[SchedulingRepository])
        contractorService = Mockito.mock(classOf[ContractorService])
        contractService = Mockito.mock(classOf[ContractService])
        branchService = Mockito.mock(classOf[BranchService])
        authorizedMemberService = Mockito.mock(classOf[AuthorizedMemberService])
        paymentInstrumentService = Mockito.mock(classOf[PaymentInstrumentService])
        userDetailService = Mockito.mock(classOf[UserDetailService])

        schedulingService = new SchedulingService(schedulingRepository, contractorService, contractService,
            branchService, authorizedMemberService, paymentInstrumentService, userDetailService)
    }

    it should "throw exception when not found scheduling" in {
        Mockito.when(schedulingRepository.findById(Matchers.any())).thenReturn(Optional.empty[Scheduling]())
        assertThrows[NotFoundException] {
            schedulingService.findById("1244AABBSS")
        }
    }

}
