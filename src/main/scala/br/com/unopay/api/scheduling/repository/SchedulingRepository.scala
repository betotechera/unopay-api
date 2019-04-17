package br.com.unopay.api.scheduling.repository

import br.com.unopay.api.scheduling.model.Scheduling
import org.springframework.data.jpa.repository.JpaRepository

trait SchedulingRepository extends JpaRepository[Scheduling, String]{

}
