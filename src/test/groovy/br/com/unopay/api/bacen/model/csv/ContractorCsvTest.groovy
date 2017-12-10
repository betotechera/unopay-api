package br.com.unopay.api.bacen.model.csv

import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.model.DocumentType
import br.com.unopay.api.model.PersonType

class ContractorCsvTest extends FixtureApplicationTest {

    def 'given contractor csv with should create person'(){
        given:
        def contractorCsv = new ContractorCsv() {
            {
                document = "5645465"
                email = "1@gmail.com"
                shortName = "teste"
                fullName = "teste teste"
                birthDate = new Date()
                gender = "MALE"
                cellPhone = "116546587496"
                telephone = "5466987498"
                zipCode = "03645645"
                streetName = "Rua da rua"
                number = "102"
                complement = "casa"
                district = "vila"
                city = "Sao Paulo"
                state = "SP"
            }
        }
        when:
        def person = contractorCsv.toPerson()

        then:
        person.documentNumber() == contractorCsv.document
        person.document.type == DocumentType.CPF
        person.type == PersonType.PHYSICAL
        person.physicalPersonDetail.email == contractorCsv.email
        person.shortName == contractorCsv.shortName
        person.name == contractorCsv.fullName
        person.physicalPersonDetail.birthDate == contractorCsv.birthDate
        person.physicalPersonDetail.gender.name() == contractorCsv.gender
        person.cellPhone == contractorCsv.cellPhone
        person.telephone == contractorCsv.telephone
        person.address.zipCode == contractorCsv.zipCode
        person.address.streetName == contractorCsv.streetName
        person.address.number == contractorCsv.number
        person.address.complement == contractorCsv.complement
        person.address.district == contractorCsv.district
        person.address.city == contractorCsv.city
        person.address.state.name() == contractorCsv.state
    }

    def 'should be equals'(){
        given:
        ContractorCsv a = new ContractorCsv() {{ setShortName("Name")}}

        when:
        def shouldBeEquals = a == a

        then:
        shouldBeEquals

    }

    def 'should not be equals'(){
        ContractorCsv a = new ContractorCsv() {{ setShortName("Name")}}
        ContractorCsv b = new ContractorCsv() {{ setShortName("Name 2")}}

        when:
        def shouldBeEquals = a == b

        then:
        !shouldBeEquals
    }
}
