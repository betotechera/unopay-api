package br.com.unopay.api.scheduling.service

import br.com.unopay.api.bacen.service.ContractorService
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.scheduling.model.Scheduling
import br.com.unopay.api.scheduling.repository.SchedulingRepository
import br.com.unopay.api.service.ContractService
import org.springframework.stereotype.Service

@Service
class SchedulingService(val schedulingRepository: SchedulingRepository,
                        val contractorService: ContractorService,
                        val contractService: ContractService,
                        val branchService: BranchService) {


    def create(scheduling: Scheduling) : Scheduling = {
        val contractor = contractorService.getById(scheduling.contractor.getId)
        scheduling.setContractor(contractor)

        val contract = contractService.findById(scheduling.contract.getId)
        scheduling.setContract(contract)

        val branch = branchService.findById(scheduling.branch.getId)
        scheduling.setBranch(branch)

        schedulingRepository.save(scheduling)
    }

}
