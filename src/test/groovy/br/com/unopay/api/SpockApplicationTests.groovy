package br.com.unopay.api

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader
import org.flywaydb.core.Flyway
import org.flywaydb.test.annotation.FlywayTest
import org.flywaydb.test.junit.FlywayTestExecutionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
@FlywayTest
@ContextConfiguration
@TestExecutionListeners([DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class ])
class SpockApplicationTests extends Specification{

    @Autowired
    protected WebApplicationContext context

    @Autowired
    protected FilterChainProxy filterChainProxy

    @Autowired
    protected Flyway flyway

    protected MockMvc mvc

    void setup() {

        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilter(filterChainProxy)
                .build()

        FixtureFactoryLoader.loadTemplates("br.com.unopay.api")
        flyway.clean()
        flyway.migrate()
    }
}
