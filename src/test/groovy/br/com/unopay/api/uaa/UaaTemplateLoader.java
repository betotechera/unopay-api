package br.com.unopay.api.uaa;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserType;

import java.util.Arrays;

public class UaaTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(UserDetail.class).addTemplate("without-group", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("name", firstName());
            add("type", one(UserType.class, "valid"));
            add("password", regex("\\d{5,8}"));
        }});

        Fixture.of(UserDetail.class).addTemplate("without-institution", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("name", firstName());
            add("type", one(UserType.class, "institution"));
            add("password", regex("\\d{5,8}"));
        }});


        Fixture.of(UserDetail.class).addTemplate("with-institution", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("name", firstName());
            add("type", one(UserType.class, "institution"));
            add("institution", one(Institution.class, "user"));
            add("password", regex("\\d{5,8}"));
        }});



        Fixture.of(UserDetail.class).addTemplate("with-group", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("password", regex("\\d{5,8}"));
            add("name", firstName());
            add("type", one(UserType.class, "valid"));
            add("groups", has(1).of(Group.class, "valid"));
        }});

        Fixture.of(UserDetail.class).addTemplate("group-with-unknown-role", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("email", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("name", firstName());
            add("password", regex("\\d{5,8}"));
            add("type", one(UserType.class, "valid"));
            add("groups", has(1).of(Group.class, "with-unknown-role"));
        }});


        Fixture.of(Group.class).addTemplate("valid", new Rule(){{
            add("name", uniqueRandom("adm", "atndente"));
            add("description", random("grupo 1", "grupo 2"));
            add("userType", one(UserType.class, "valid"));
            add("authorities", has(1).of(Authority.class, "valid-admin"));
        }});

        Fixture.of(Group.class).addTemplate("with-id", new Rule(){{
            add("id", uniqueRandom("1e765bed-5459-49fb-b6fa-e841960f4bd2", "89e349d8-0ee3-42ce-afaf-6ea3b9ceffc4", "254859ed-4690-4752-bfe5-608d48ee8b14", "e3ddf1e7-b6d1-48bd-a5e8-c7d8aa88e329"));
            add("name", uniqueRandom("adm", "atndente"));
            add("description", random("grupo 1", "grupo 2"));
            add("userType", one(UserType.class, "valid"));
            add("authorities", Arrays.asList("ROLE_ADMIN", "ROLE_UNKNOWN"));
        }});


        Fixture.of(Group.class).addTemplate("with-unknown-role", new Rule(){{
            add("name", uniqueRandom("adm", "atndente"));
            add("description", random("grupo 1", "grupo 2"));
            add("userType", one(UserType.class, "valid"));
            add("authorities", has(1).of(Authority.class, "unknown"));
        }});

        Fixture.of(Group.class).addTemplate("without-name", new Rule(){{
            add("description", random("grupo 1", "grupo 2"));
            add("userType", one(UserType.class, "valid"));
            add("authorities", Arrays.asList("ROLE_ADMIN", "ROLE_UNKNOWN"));
        }});

        Fixture.of(Authority.class).addTemplate("valid", new Rule(){{
            add("name", uniqueRandom("ROLE_ADMIN", "ROLE_USER"));
            add("description", uniqueRandom("Role 1", "Role 2"));
        }});

        Fixture.of(Authority.class).addTemplate("valid-admin", new Rule(){{
            add("name", uniqueRandom("ROLE_ADMIN"));
            add("description", uniqueRandom("Role 1", "Role 2"));
        }});

        Fixture.of(Authority.class).addTemplate("unknown", new Rule(){{
            add("name", uniqueRandom("ROLE_UNKNOWN_1", "ROLE_UNKNOWN_2"));
            add("description", uniqueRandom("Role 1", "Role 2"));
        }});

        Fixture.of(UserType.class).addTemplate("valid", new Rule(){{
            add("id", "3");
            add("name", uniqueRandom("MASTER", "SUPER"));
            add("description", uniqueRandom("User type 1", "User type 2"));
        }});


        Fixture.of(UserType.class).addTemplate("institution", new Rule(){{
            add("id", "2");
            add("name", "INSTITUIDOR");
            add("description", "Instituidor de arranjo de Pagamento");
        }});


    }
}
