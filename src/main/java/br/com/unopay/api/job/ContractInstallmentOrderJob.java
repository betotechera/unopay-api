package br.com.unopay.api.job;

import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.service.ContractInstallmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Component
public class ContractInstallmentOrderJob {

    private ContractInstallmentService contractInstallmentService;
    private OrderService orderService;

    @Autowired
    public ContractInstallmentOrderJob(ContractInstallmentService contractInstallmentService, OrderService orderService) {
        this.contractInstallmentService = contractInstallmentService;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 10 3 ? * *", zone = "GMT-3")
    void execute() {
        log.info("running contract installment order job");
        Stream<ContractInstallment> installments = contractInstallmentService.findInstallmentAboutToExpire();
        installments.map(ContractInstallment::toOrder)
                .forEach(order -> orderService.create(order));
        log.info("finished contract installment order job.");
    }
}
