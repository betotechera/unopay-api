package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.Purpose;
import br.com.unopay.api.bacen.model.Scope;
import br.com.unopay.api.bacen.model.UserRelationship;
import br.com.unopay.api.model.*;
import br.com.unopay.api.uaa.model.UserType;

import static br.com.unopay.api.model.State.SP;


public class PersonTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(Person.class).addTemplate("physical", new Rule(){{
            add("name", "Teste");
            add("type", PersonType.PHYSICAL);
            add("document", one(Document.class, "valid-cpf"));
            add("address", one(Address.class, "address"));
            add("telephone", "11999999999");
        }});


        Fixture.of(Person.class).addTemplate("legal", new Rule(){{
            add("name", "Teste");
            add("type", PersonType.LEGAL);
            add("document", one(Document.class, "valid-cnpj"));
            add("legalPersonDetail", one(LegalPersonDetail.class, "legal-person"));
            add("address", one(Address.class, "address"));
            add("telephone", "11999999999");
        }});

        Fixture.of(LegalPersonDetail.class).addTemplate("legal-person", new Rule(){{
            add("fantasyName", "Teste");
            add("responsibleName", "Diretor");
            add("responsibleEmail", "diretor@company.com");
            add("responsibleDocument", one(Document.class, "valid-cpf"));
        }});

        Fixture.of(Document.class).addTemplate("valid-cnpj", new Rule(){{
            add("number", uniqueRandom("28004825000101","45241782000199","22190238000160","83084877000135"));
            add("type", DocumentType.CNPJ);
            add("registryEntity", RegistryEntity.DIC);
        }});

        Fixture.of(Document.class).addTemplate("valid-cpf", new Rule(){{
            add("number", "92505722803");
            add("type", DocumentType.CPF);
            add("registryEntity", RegistryEntity.SSP);
        }});

        Fixture.of(Document.class).addTemplate("valid-cpf", new Rule(){{
            add("number", "92505722803");
            add("type", DocumentType.CPF);
            add("registryEntity", RegistryEntity.SSP);
        }});

        Fixture.of(Address.class).addTemplate("address", new Rule(){{
            add("zipCode", "05302030");
            add("streetName", "Rua aaaa");
            add("number", "12344");
            add("district", "ADDCA");
            add("city", "dadad");
            add("state", SP);
        }});

    }
}
