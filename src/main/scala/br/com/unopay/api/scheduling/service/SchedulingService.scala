package br.com.unopay.api.scheduling.service

import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.api.uaa.exception.Errors.SCHEDULING_NOT_FOUND
import br.com.unopay.api.uaa.service.UserDetailService
import br.com.unopay.bootcommons.exception.UnovationExceptions.notFound
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Service

@Service
class SchedulingService(val schedulingRepository: SchedulingRepository,
                        val contractorService: ContractorService,
                        val contractService: ContractService,
                        val branchService: BranchService,
                        val authorizedMemberService: AuthorizedMemberService,
                        val paymentInstrumentService: PaymentInstrumentService,
                        val userDetailService: UserDetailService) {

    def create(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        checkNetworkContextReferences(scheduling, accreditedNetwork)
        create(scheduling)
    }

    def create(scheduling: Scheduling) : Scheduling = {
        setReferences(scheduling)
        schedulingRepository.save(scheduling)
    }

    def update(id: String, otherScheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        checkNetworkContextReferences(otherScheduling, accreditedNetwork)
        findById(id, accreditedNetwork)
        update(id, otherScheduling)
    }

    def update(id: String, otherScheduling: Scheduling) : Scheduling = {
        val actualScheduling = findById(id)
        actualScheduling.updateAllExcept(otherScheduling, "user")
        setReferences(actualScheduling)
        schedulingRepository.save(actualScheduling)
    }

    def findById(id: String, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        schedulingRepository.findByIdAndBranchHeadOfficeNetworkId(id, accreditedNetwork.getId)
          .orElseThrow(() => throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(id)))
    }

    def findById(id: String, contractor: Contractor) : Scheduling = {
        schedulingRepository.findByIdAndContractorId(id, contractor.getId)
          .orElseThrow(() => throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(id)))
    }

    def findById(id: String) : Scheduling = {
        schedulingRepository.findById(id)
                .orElseThrow(() => throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(id)))
    }

    def cancelById(id: String, accreditedNetwork: AccreditedNetwork): Unit = {
        val current = findById(id, accreditedNetwork)
        cancelById(id, current)
    }

    private def cancelById(id: String, current: Scheduling) = {
        current.cancelMe()
        update(id, current)
    }

    def cancelById(id: String, contractor: Contractor): Unit = {
        val current = findById(id, contractor)
        cancelById(id, current)
    }

    def deleteById(id: String): Unit = {
        val scheduling = findById(id)
        schedulingRepository.delete(scheduling)
    }

    def findAll(schedulingFilter: SchedulingFilter, network: AccreditedNetwork, pageable: UnovationPageRequest): Page[Scheduling] = {
        schedulingFilter.network = network.getId
        findAll(schedulingFilter, pageable)
    }

    def findAll(schedulingFilter: SchedulingFilter, contractor: Contractor, pageable: UnovationPageRequest): Page[Scheduling] = {
        schedulingFilter.contractor = contractor.getId
        findAll(schedulingFilter, pageable)
    }

    def findAll(schedulingFilter: SchedulingFilter, pageable: UnovationPageRequest): Page[Scheduling] = {
        val pageRequest = new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize())
        schedulingRepository.findAll(schedulingFilter, pageRequest)
    }

    private def checkNetworkContextReferences(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) = {
        branchService.findById(scheduling.branchId(), accreditedNetwork)
        contractorService.getByIdForNetwork(scheduling.contractorId(), accreditedNetwork)
        contractorService.getByIdForConctract(scheduling.contractorId(), scheduling.getContract)
        paymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId())
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
