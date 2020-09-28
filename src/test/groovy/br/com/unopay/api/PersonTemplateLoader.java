package br.com.unopay.api;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.function.impl.CpfFunction;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.LegalPersonDetail;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.PhysicalPersonDetail;
import br.com.unopay.api.model.RegistryEntity;

import static br.com.unopay.api.model.State.SP;


public class PersonTemplateLoader implements TemplateLoader {

    @Override
    public void load() {


        Fixture.of(Person.class).addTemplate("base", new Rule(){{
            add("name", firstName());
            add("shortName", firstName());
            add("address", one(Address.class, "address"));
            add("telephone", "11999999999");
            add("cellPhone", "11999999999");

        }});

        Fixture.of(Person.class).addTemplate("physical").inherits("base", new Rule(){{
            add("type", PersonType.PHYSICAL);
            add("document", one(Document.class, "valid-cpf"));
            add("physicalPersonDetail", one(PhysicalPersonDetail.class, "physical-person"));
        }});

        Fixture.of(Person.class).addTemplate("legal").inherits("base", new Rule(){{
            add("type", PersonType.LEGAL);
            add("document", one(Document.class, "valid-cnpj"));
            add("legalPersonDetail", one(LegalPersonDetail.class, "legal-person"));
        }});


        Fixture.of(PhysicalPersonDetail.class).addTemplate("physical-person", new Rule(){{
            add("email", uniqueRandom("user@company.com", "user2@uol.com.br", "usuario@domino.com"));
            add("birthDate", instant("18 years ago"));
            add("gender", random(Gender.class));
        }});


        Fixture.of(LegalPersonDetail.class).addTemplate("legal-person", new Rule(){{
            add("fantasyName", "Teste");
            add("responsibleName", "Diretor");
            add("responsibleEmail", "diretor@company.com");
            add("responsibleDocument", one(Document.class, "valid-cpf"));
        }});

        Fixture.of(Document.class).addTemplate("valid-cnpj", new Rule(){{
            add("number", cnpj());
            add("type", DocumentType.CNPJ);
            add("registryEntity", RegistryEntity.DIC);
        }});

        Fixture.of(Document.class).addTemplate("valid-cpf", new Rule(){{
            add("number", new CpfFunction());
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
