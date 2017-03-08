package br.com.unopay.api;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spock.lang.Specification;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("test")
@FlywayTest(locationsForMigrate = {"/test/db/migration"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class })
public abstract class UnopayApiApplicationTests extends Specification{

	@Autowired
	protected WebApplicationContext context;

	@Autowired
	protected FilterChainProxy filterChainProxy;

	protected MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.addFilter(filterChainProxy)
				.build();
	}

}
