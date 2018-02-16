package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.Page
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile


class AuthorizedMemberServiceTest extends SpockApplicationTests {
    @Autowired
    AuthorizedMemberService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    ResourceLoader resourceLoader

    void 'given valid AuthorizedMember should create'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        when:
        def result = service.create(authorizedMember)
        then:
        result
    }

    void 'given AuthorizedMember without birthDate should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.birthDate = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_BIRTH_DATE_REQUIRED'
    }

    void "given AuthorizedMember with paymentInstrument that doesn't belong to it's contractor should return error"(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.paymentInstrument = fixtureCreator.createInstrumentToProduct()
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INSTRUMENT_NOT_BELONGS_TO_CONTRACTOR'
    }

    void 'given AuthorizedMember without contract should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.contract = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'CONTRACT_REQUIRED'
    }

    void 'given AuthorizedMember without gender should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.gender = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_GENDER_REQUIRED'
    }

    void 'given AuthorizedMember without relatedness should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.relatedness = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_RELATEDNESS_REQUIRED'
    }

    void 'given AuthorizedMember without paymentInstrument should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.paymentInstrument = null
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'PAYMENT_INSTRUMENT_REQUIRED'
    }

    void 'should find known AuthorizedMember'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        when:
        def found = service.findById(authorizedMember.id)
        then:
        found
    }

    void 'when trying to find unknown AuthorizedMember should return error'(){
        given:
        def id = "123"
        when:
        service.findById(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should update known AuthorizedMember'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        when:
        service.update(authorizedMember.id, authorizedMember)

        then:
        def found = service.findById(authorizedMember.id)
        found.name == authorizedMember.name
    }

    void 'when trying to delete unknown AuthorizedMember should return error'(){
        given:
        def id = "123"
        when:
        service.delete(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should delete known AuthorizedMember'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def id = authorizedMember.id;
        when:
        service.delete(id)
        service.findById(id)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should create AuthorizedMembers from csv'() {
        given:
        def contractor = fixtureCreator.createContractor("valid")
        createPersistedContract(contractor, 123456L)
        createPersistedContract(contractor, 123457L)
        createPersistedContract(contractor, 123458L)

        createInstrument(contractor, "123456")
        createInstrument(contractor, "123457")
        createInstrument(contractor, "123458", PaymentInstrumentType.DIGITAL_WALLET)

        Resource csv  = resourceLoader.getResource("classpath:/AuthorizedMember.csv")
        MultipartFile file = new MockMultipartFile('file', csv.getInputStream())

        when:
        service.createFromCsv(file)

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> authorizedMembers = service.findByFilter(new AuthorizedMemberFilter(), page)

        then:
        authorizedMembers.content.size() == 3
    }

    void 'should find known AuthorizedMember by hirerDocumentNumber filter'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def documentNumber = authorizedMember.contract.hirer.documentNumber
        when:
        def filter = new AuthorizedMemberFilter() {{
            hirerDocumentNumber = documentNumber
        }}

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> result = service.findByFilter(filter, page)
        then:
        result.content.size() > 0
    }

    void 'should find known AuthorizedMember by contractorDocumentNumber filter'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def documentNumber = authorizedMember.contract.contractor.documentNumber
        when:
        def filter = new AuthorizedMemberFilter() {{
            contractorDocumentNumber = documentNumber
        }}

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> result = service.findByFilter(filter, page)
        then:
        result.content.size() > 0
    }

    Contract createPersistedContract(contractor, contractCode) {
        def product = fixtureCreator.createProduct()
        def hirer = fixtureCreator.createHirer()
        def situation = ContractSituation.ACTIVE
        Fixture.from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("hirer", hirer)
                add("contractor", contractor)
                add("product", product)
                add("serviceTypes", product.serviceTypes)
                add("situation", situation)
                add("code", contractCode)
            }
        })
    }

    PaymentInstrument createInstrument(contractor, number, type = PaymentInstrumentType.VIRTUAL_CARD) {
        def product = fixtureCreator.createProduct()

        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        PaymentInstrument inst = Fixture.from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {
            {
                add("product", product)
                add("contractor", contractor)
                add("password", passwordEncoder.encode(generatePassword))
                add("number", number)
                add("type", type)
            }
        })
        inst.with { password = generatePassword; it }
    }
}
