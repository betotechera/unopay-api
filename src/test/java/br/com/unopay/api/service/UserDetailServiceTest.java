package br.com.unopay.api.service;


import br.com.unopay.api.AuthServerApplicationTests;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

public class UserDetailServiceTest extends AuthServerApplicationTests {

    @Autowired
    UserDetailService service;

    @Test
    public void unknownAuthoritiesShouldNotBeSaved() {

        UserDetail user = new UserDetail(randomNumeric(5),
                "test@integrationtest.com",
                "123",
                newHashSet("ROLE_UNKNOWN", "ROLE_ADMIN"));

        service.create(user);

        UserDetail created = service.getById(user.getId());
        Assert.assertThat(created.getAuthorities(), contains("ROLE_ADMIN"));
        Assert.assertThat(created.getAuthorities(), not(contains("ROLE_UNKNOWN")));


        user.getAuthorities().add("ROLE_UNKNOWN");
        service.update(created);

        created = service.getById(user.getId());
        Assert.assertThat(created.getAuthorities(), contains("ROLE_ADMIN"));
        Assert.assertThat(created.getAuthorities(), not(contains("ROLE_UNKNOWN")));

    }

}
