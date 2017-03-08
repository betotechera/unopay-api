package br.com.unopay.api;


import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.assertEquals;


public class FlywayApplicationTest extends UnopayApiApplicationTests{

    @Autowired
    private JdbcTemplate template;

    @FlywayTest(invokeCleanDB = true)
    @Test
    public void singleLocation() throws Exception {
        assertEquals(new Integer(1), this.template.queryForObject(
                "SELECT COUNT(*) from AUTHORITY", Integer.class));
    }

    @FlywayTest(locationsForMigrate = {"/test/db/migration"})
    @Test
    public void twoLocations() throws Exception {
        assertEquals(new Integer(4), this.template.queryForObject(
                "SELECT COUNT(*) from oauth_user_details", Integer.class));
    }

}