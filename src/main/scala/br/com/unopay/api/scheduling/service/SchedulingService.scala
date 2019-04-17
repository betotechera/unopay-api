package br.com.unopay.api.scheduling.service

import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.api.uaa.exception.Errors.SCHEDULING_NOT_FOUND
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.UnovationExceptions.notFound
import org.springframework.stereotype.Service

@Service
class SchedulingService(val schedulingRepository: SchedulingRepository,
                        val contractorService: ContractorService,
                        val contractService: ContractService,
                        val branchService: BranchService,
                        val authorizedMemberService: AuthorizedMemberService,
                        val paymentInstrumentService: PaymentInstrumentService,
                        val userDetailService: UserDetailService) {

    def create(scheduling: Scheduling) : Scheduling = {
        setReferences(scheduling)
        schedulingRepository.save(scheduling)
    }

    def update(id: String, otherScheduling: Scheduling) : Scheduling = {
        val actualScheduling = findById(id)
        actualScheduling.updateMe(otherScheduling)
        setReferences(actualScheduling)
        actualScheduling
    }

    def findById(id: String) : Scheduling = {
        schedulingRepository.findById(id)
                .orElseThrow(throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(id)))
    }

    private def setReferences(scheduling: Scheduling): Unit = {
        val contractor = contractorService.getById(scheduling.contractor.getId)
        scheduling.setContractor(contractor)

        val contract = contractService.findById(scheduling.contract.getId)
        scheduling.setContract(contract)

        val branch = branchService.findById(scheduling.branch.getId)
        scheduling.setBranch(branch)

        val paymentInstrument = paymentInstrumentService.findById(scheduling.paymentInstrument.getId)
        scheduling.setPaymentInstrument(paymentInstrument)

        val user = userDetailService.getById(scheduling.user.getId)
        scheduling.setUser(user)

        if (scheduling.hasAuthorizedMember) {
            val authorizedMember = authorizedMemberService.findById(scheduling.authorizedMember.getId)
            scheduling.setAuthorizedMember(authorizedMember)
        }
    }

}
