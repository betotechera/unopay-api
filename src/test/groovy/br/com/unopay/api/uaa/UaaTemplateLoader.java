package br.com.unopay.api.uaa;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;

import java.util.Arrays;

public class UaaTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(UserDetail.class).addTemplate("without-group", new Rule(){{
            add("id", random(String.class, "1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", random("nerd@gmail.com", "news@gmail.com"));
            add("password", regex("\\d{3,5}"));
            add("authorities", Arrays.asList("ROLE_ADMIN", "ROLE_UNKNOWN"));
        }});

        Fixture.of(UserDetail.class).addTemplate("with-group", new Rule(){{
            add("email", random("nerd@gmail.com", "news@gmail.com"));
            add("password", regex("\\d{3,5}"));
            add("groups", has(1).of(Group.class, "valid"));
            add("authorities", Arrays.asList("ROLE_ADMIN", "ROLE_UNKNOWN"));
        }});


        Fixture.of(Group.class).addTemplate("valid", new Rule(){{
            add("name", random(String.class, "administrador", "atendente"));
            add("description", random(String.class,"grupo 1", "grupo 2"));
        }});

        Fixture.of(Group.class).addTemplate("without-name", new Rule(){{
            add("description", random(String.class,"grupo 1", "grupo 2"));
        }});

        Fixture.of(Authority.class).addTemplate("valid", new Rule(){{
            add("name", "ROLE_ADMIN");
            add("description", random(String.class,"Role 1", "Role 2"));
        }});

        Fixture.of(Authority.class).addTemplate("invalid", new Rule(){{
            add("name", "ROLE_UNKNOWN");
            add("description", random(String.class,"Role 1", "Role 2"));
        }});


    }
}
