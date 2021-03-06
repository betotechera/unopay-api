package br.com.unopay.api

import br.com.unopay.api.uaa.service.UserDetailService
import org.junit.runner.{JUnitCore, RunWith}
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.{ActiveProfiles, TestContextManager}
import org.springframework.test.web.servlet.MockMvc

@RunWith(classOf[JUnitRunner])
@ActiveProfiles(Array("test"))
class ControllerTest extends ScalaFixtureTest { this: Suite =>

    @Autowired
    var mockMvc: MockMvc = _

    @MockBean
    var mockUserDetailService: UserDetailService = _

    override def beforeAll(): Unit = {
        JUnitCore.runClasses(classOf[TestSpringRunner])
        new TestContextManager(this.getClass).prepareTestInstance(this)
    }

    override def beforeEach(): Unit = {
        super.beforeEach()

    }

}

@RunWith(classOf[SpringRunner]) trait TestSpringRunner {}
