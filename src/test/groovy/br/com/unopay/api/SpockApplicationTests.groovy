package br.com.unopay.api

import org.flywaydb.core.Flyway
import org.flywaydb.test.annotation.FlywayTest
import org.flywaydb.test.junit.FlywayTestExecutionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
@FlywayTest
@ContextConfiguration(classes = [Mocks, UnopayApiApplication])
@TestExecutionListeners([DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class ])
class SpockApplicationTests extends FixtureApplicationTest{

    @Autowired
    protected WebApplicationContext context

    @Autowired
    protected FilterChainProxy filterChainProxy

    @Autowired
    protected Flyway flyway

    @Autowired
    protected JpaProcessor jpaProcessor

    protected MockMvc mvc

    void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilter(filterChainProxy)
                .build()
        flyway.clean()
        flyway.migrate()
    }

    void clientAuthenticated() {
        OAuth2Authentication authentication = mock(OAuth2Authentication.class)
        when(authentication.isAuthenticated()).thenReturn(true)
        when(authentication.isClientOnly()).thenReturn(true)
        SecurityContextHolder.getContext().setAuthentication(authentication)
    }
}
