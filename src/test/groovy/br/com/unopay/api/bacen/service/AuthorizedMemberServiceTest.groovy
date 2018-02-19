package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AuthorizedMember
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.Document
import br.com.unopay.api.model.DocumentType
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.service.ProductService
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.Page
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile

import static br.com.six2six.fixturefactory.Fixture.from


class AuthorizedMemberServiceTest extends SpockApplicationTests {
    @Autowired
    AuthorizedMemberService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    ResourceLoader resourceLoader

    @Autowired
    ProductService productService

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

    void 'given AuthorizedMember with birthDate before minimum date should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.birthDate = new DateTime().minusYears(151).toDate()
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_AUTHORIZED_MEMBER_BIRTH_DATE'
    }

    void 'given AuthorizedMember with birthDate after today should return error'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.birthDate = new DateTime().plusDays(1).toDate()
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'INVALID_AUTHORIZED_MEMBER_BIRTH_DATE'
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

    void 'should find known AuthorizedMember by id and contractor id'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        when:
        def found = service.findByIdForContractor(authorizedMember.id, authorizedMember.contract.contractor)
        then:
        found
    }

    void 'given unknown contractor id should return error when find by id and contractor id'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        Contractor contractor = from(Contractor.class).gimme("valid")
        contractor.id = "123"
        when:
        service.findByIdForContractor(authorizedMember.id, contractor)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should find known AuthorizedMember by id and hirer id'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        when:
        def found = service.findByIdForHirer(authorizedMember.id, authorizedMember.contract.hirer)
        then:
        found
    }

    void 'given unknown hirer should return error when find by id and hirer id'(){
        given:
        AuthorizedMember authorizedMember =  fixtureCreator.createPersistedAuthorizedMember()
        Hirer hirer = from(Hirer.class).gimme("valid")
        hirer.id = "123"
        when:
        service.findByIdForHirer(authorizedMember.id, hirer)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
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


    void 'given knoen AuthorizedMember should update for hirer'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        def hirer = authorizedMember.contract.hirer
        when:
        service.updateForHirer(authorizedMember.id, hirer, authorizedMember)

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

    void 'should delete AuthorizedMember by Hirer'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        when:
        service.deleteForHirer(authorizedMember.id, authorizedMember.contract.hirer)
        service.findById(authorizedMember.id)
        then:
        thrown(NotFoundException)
    }

    void 'should create AuthorizedMembers from csv'() {
        given:
        def contractor = createContractor("123456789")
        createPersistedContract(contractor, 123456L, createProduct("123"))
        createPersistedContract(contractor, 123457L, createProduct("1234"))
        createPersistedContract(contractor, 123458L, createProduct("1235"))

        def code = productService.findByCode("123")
        def code1 = productService.findByCode("1234")
        def code2 = productService.findByCode("1235")

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
        def id = authorizedMember.contract.hirer.id
        when:
        def filter = new AuthorizedMemberFilter() {{
            hirerId = id
        }}

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> result = service.findByFilter(filter, page)
        then:
        result.content.size() > 0
    }

    void 'should find known AuthorizedMember by contractorDocumentNumber filter'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def id = authorizedMember.contract.contractor.id
        when:
        def filter = new AuthorizedMemberFilter() {{
            contractorId = id
        }}

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> result = service.findByFilter(filter, page)
        then:
        result.content.size() > 0
    }

    void 'should find known AuthorizedMember by paymentInstrumentNumber filter'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        def instrumentNumber = authorizedMember.paymentInstrument.number
        when:
        def filter = new AuthorizedMemberFilter() {{
            paymentInstrumentNumber = instrumentNumber
        }}

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> result = service.findByFilter(filter, page)
        then:
        result.content.size() > 0
    }

    private Contractor createContractor(String documentNumber) {
        Document document = new Document() {{
            type = DocumentType.CNPJ
            number = documentNumber
        }}
        def person = from(Person.class).uses(jpaProcessor).gimme("legal", new Rule() {{
            add("document", document)
        }})
        from(Contractor.class).uses(jpaProcessor).gimme("valid", new Rule() {{
            add("person", person)
        }})
    }

    private Contract createPersistedContract(contractor, contractCode, product = createProduct()) {
        def hirer = fixtureCreator.createHirer()
        def situation = ContractSituation.ACTIVE
        from(Contract.class).uses(jpaProcessor).gimme("valid", new Rule() {
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

    private Product createProduct(code) {
        PaymentRuleGroup paymentRuleGroupUnderTest = fixtureCreator.createPaymentRuleGroup()
        BigDecimal membershipFee = (Math.random() * 100)
        return from(Product.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("paymentRuleGroup", paymentRuleGroupUnderTest)
            add("membershipFee", membershipFee)
            add("code", code)
        }})
    }

    private PaymentInstrument createInstrument(contractor, number, type = PaymentInstrumentType.VIRTUAL_CARD) {
        def product = fixtureCreator.createProduct()

        String generatePassword = new RegexFunction("\\d{3}\\w{5}").generateValue()
        PaymentInstrument inst = from(PaymentInstrument.class).uses(jpaProcessor).gimme("valid", new Rule() {
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
