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
import br.com.unopay.api.`implicit`.DateImplicit.DateImplicit

@Service
class SchedulingService(val schedulingRepository: SchedulingRepository,
                        val contractorService: ContractorService,
                        val contractService: ContractService,
                        val branchService: BranchService,
                        val authorizedMemberService: AuthorizedMemberService,
                        val paymentInstrumentService: PaymentInstrumentService) {

    private val MAX_EXPIRATION_IN_DAYS = 5

    def create(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        setReferences(scheduling, accreditedNetwork)
        scheduling.setExpirationDate(scheduling.date.plusDays(MAX_EXPIRATION_IN_DAYS))
        schedulingRepository.save(scheduling)
    }

    def create(scheduling: Scheduling) : Scheduling = {
        setReferences(scheduling)
        scheduling.setExpirationDate(scheduling.date.plusDays(MAX_EXPIRATION_IN_DAYS))
        schedulingRepository.save(scheduling)
    }

    def update(id: String, otherScheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        val actualScheduling = findById(id, accreditedNetwork)
        actualScheduling.updateMe(otherScheduling)
        setReferences(otherScheduling, accreditedNetwork)
        schedulingRepository.save(actualScheduling)
    }

    def update(id: String, otherScheduling: Scheduling) : Scheduling = {
        val actualScheduling = findById(id)
        actualScheduling.updateMe(otherScheduling)
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
        schedulingRepository.save(current)
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

    private def setReferences(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Unit = {
        val branch = branchService.findById(scheduling.branchId(), accreditedNetwork)
        scheduling.setBranch(branch)

        contractorService.getByIdForNetwork(scheduling.contractorId(), accreditedNetwork)
        val contractor = contractorService.getByIdForConctract(scheduling.contractorId(), scheduling.getContract)
        scheduling.setContractor(contractor)

        val paymentInstrument = paymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId())
        scheduling.setPaymentInstrument(paymentInstrument)

        val contract = contractService.findById(scheduling.contract.getId)
        scheduling.setContract(contract)

        if (scheduling.hasAuthorizedMember) {
            val authorizedMember = authorizedMemberService.findById(scheduling.authorizedMember.getId)
            scheduling.setAuthorizedMember(authorizedMember)
        }
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

        if (scheduling.hasAuthorizedMember) {
            val authorizedMember = authorizedMemberService.findById(scheduling.authorizedMember.getId)
            scheduling.setAuthorizedMember(authorizedMember)
        }
    }



}
