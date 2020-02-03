package br.com.unopay.api.scheduling.service

import br.com.unopay.api.`implicit`.DateImplicit.DateImplicit
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.market.service.AuthorizedMemberService
import br.com.unopay.api.network.model.{AccreditedNetwork, Establishment}
import br.com.unopay.api.network.service.{BranchService, EstablishmentEventService, EventService}
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.{ContractService, PaymentInstrumentService}
import br.com.unopay.api.uaa.exception.Errors.SCHEDULING_NOT_FOUND
import br.com.unopay.api.util.TokenFactory
import br.com.unopay.bootcommons.exception.UnovationExceptions.notFound
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class SchedulingService(val schedulingRepository: SchedulingRepository,
                        val contractorService: ContractorService,
                        val contractService: ContractService,
                        val branchService: BranchService,
                        val authorizedMemberService: AuthorizedMemberService,
                        val paymentInstrumentService: PaymentInstrumentService,
                        val establishmentEventService: EstablishmentEventService) {

    private val MAX_EXPIRATION_IN_DAYS = 5

    def create(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        setSchedulingToken(scheduling)
        setReferences(scheduling, accreditedNetwork)
        setExpiration(scheduling)
        schedulingRepository.save(scheduling)
    }

    def create(scheduling: Scheduling, contractor: Contractor) : Scheduling = {
        setSchedulingToken(scheduling)
        setReferences(scheduling, contractor)
        setExpiration(scheduling)
        schedulingRepository.save(scheduling)
    }

    def create(scheduling: Scheduling) : Scheduling = {
        setSchedulingToken(scheduling)
        setReferences(scheduling)
        setExpiration(scheduling)
        schedulingRepository.save(scheduling)
    }

    def update(id: String, otherScheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Scheduling = {
        val actualScheduling = findById(id, accreditedNetwork)
        actualScheduling.updateMe(otherScheduling)
        setReferences(actualScheduling, accreditedNetwork)
        schedulingRepository.save(actualScheduling)
    }

    def update(id: String, otherScheduling: Scheduling) : Scheduling = {
        val actualScheduling = findById(id)
        actualScheduling.updateAllExcept(otherScheduling, "user")
        setReferences(actualScheduling)
        schedulingRepository.save(actualScheduling)
    }

    def findByToken(token: String, establishment: Establishment) = {
        schedulingRepository.findByTokenAndBranchHeadOfficeNetworkId(token, establishment.getNetwork.getId)
          .orElseThrow(() => throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(token)))
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

    def findByToken(token: String) : Scheduling = {
        schedulingRepository.findByToken(token)
          .orElseThrow(() => throw notFound.withErrors(SCHEDULING_NOT_FOUND.withOnlyArguments(token)))
    }

    def cancelById(id: String): Unit = {
        val current = findById(id)
        cancel(current)
    }

    def cancelById(id: String, accreditedNetwork: AccreditedNetwork): Unit = {
        val current = findById(id, accreditedNetwork)
        cancel(current)
    }

    private def cancel(current: Scheduling) = {
        current.cancelMe()
        schedulingRepository.save(current)
    }

    def cancelById(id: String, contractor: Contractor): Unit = {
        val current = findById(id, contractor)
        cancel(current)
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

    private def setReferences(scheduling: Scheduling, contractor: Contractor) : Unit = {
        val branch = branchService.findById(scheduling.branchId())
        scheduling.setBranch(branch)

        contractorService.getById(scheduling.contractorId())
        val contractor = contractorService.getByIdForConctract(scheduling.contractorId(), scheduling.getContract)
        scheduling.setContractor(contractor)

        val paymentInstrument = paymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId())
        scheduling.setPaymentInstrument(paymentInstrument)

        this.setCommonReferences(scheduling)
    }

    private def setReferences(scheduling: Scheduling, accreditedNetwork: AccreditedNetwork) : Unit = {
        val branch = branchService.findById(scheduling.branchId(), accreditedNetwork)
        scheduling.setBranch(branch)

        contractorService.getByIdForNetwork(scheduling.contractorId(), accreditedNetwork)
        val contractor = contractorService.getByIdForConctract(scheduling.contractorId(), scheduling.getContract)
        scheduling.setContractor(contractor)

        val paymentInstrument = paymentInstrumentService.findByIdAndContractorId(scheduling.instrumentId(), scheduling.contractorId())
        scheduling.setPaymentInstrument(paymentInstrument)

        this.setCommonReferences(scheduling)
    }

    private def setReferences(scheduling: Scheduling): Unit = {
        val contractor = contractorService.getById(scheduling.contractor.getId)
        scheduling.setContractor(contractor)


        val branch = branchService.findById(scheduling.branch.getId)
        scheduling.setBranch(branch)

        val paymentInstrument = paymentInstrumentService.findById(scheduling.paymentInstrument.getId)
        scheduling.setPaymentInstrument(paymentInstrument)

        this.setCommonReferences(scheduling)
    }

    private def setCommonReferences(scheduling: Scheduling): Unit = {
        val contract = contractService.findById(scheduling.contract.getId)
        scheduling.setContract(contract)

        if (scheduling.hasAuthorizedMember) {
            val authorizedMember = authorizedMemberService.findById(scheduling.authorizedMember.getId)
            scheduling.setAuthorizedMember(authorizedMember)
        }

        if (scheduling.hasEvents()){
            scheduling.events = scheduling.events.asScala.map(event =>
                establishmentEventService.findByEventIdAndEstablishmentId(event.getId, scheduling.getBranch.headOfficeId())).map(_.getEvent).asJava
        }
    }

    private def setExpiration(scheduling: Scheduling) : Unit = {
        scheduling.setExpirationDate(scheduling.date.plusDays(MAX_EXPIRATION_IN_DAYS))
    }

    def setSchedulingToken(scheduling: Scheduling) : Unit = {
        scheduling.setToken(TokenFactory.generateToken())
    }
}
