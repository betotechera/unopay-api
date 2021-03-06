package br.com.unopay.api.market.service

import static br.com.six2six.fixturefactory.Fixture.from
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.function.impl.RegexFunction
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Hirer
import br.com.unopay.api.bacen.model.PaymentRuleGroup
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.market.model.AuthorizedMember
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractSituation
import br.com.unopay.api.model.Document
import br.com.unopay.api.model.DocumentType
import br.com.unopay.api.model.Gender
import br.com.unopay.api.model.PaymentInstrument
import br.com.unopay.api.model.PaymentInstrumentType
import br.com.unopay.api.model.Person
import br.com.unopay.api.model.Product
import br.com.unopay.api.model.Relatedness
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

class AuthorizedMemberServiceTest extends SpockApplicationTests {

    @Autowired
    private AuthorizedMemberService service
    @Autowired
    private FixtureCreator fixtureCreator
    @Autowired
    private PasswordEncoder passwordEncoder
    @Autowired
    private ResourceLoader resourceLoader

    void 'given valid AuthorizedMember should create'(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        when:
        def result = service.create(authorizedMember)
        then:
        result
    }

    void 'given a hirer with a own contract when creating a member should not return an error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember =  fixtureCreator.createAuthorizedMemberToPersist(contract)
        authorizedMember.contract = contract
        when:
        def created = service.create(authorizedMember, contract.hirer)
        def result = service.findById(created.id)
        then:
        result.contract.id == contract.id
    }

    void 'given a hirer when creating a member with a contract from another hirer should return an error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember =  fixtureCreator.createAuthorizedMemberToPersist(contract)
        authorizedMember.contract = contract
        Hirer hirer = from(Hirer.class).uses(jpaProcessor).gimme("valid")
        when:
        service.create(authorizedMember, hirer)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
    }

    void 'given a contractor with a own contract when creating a member should not return an error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember =  fixtureCreator.createAuthorizedMemberToPersist(contract)
        authorizedMember.contract = contract
        when:
        def created = service.create(authorizedMember, contract.contractor)
        def result = service.findById(created.id)
        then:
        result.contract.id == contract.id
    }

    void 'given a contractor when creating a member with a contract from another hirer should return an error'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember =  fixtureCreator.createAuthorizedMemberToPersist(contract)
        authorizedMember.contract = contract
        when:
        service.create(authorizedMember, fixtureCreator.createContractor("valid"))
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'CONTRACTOR_CONTRACT_NOT_FOUND'
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

    void "given AuthorizedMember with unknown paymentInstrument should return error"(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.paymentInstrument.id = "123"
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'PAYMENT_INSTRUMENT_NOT_FOUND'
    }

    void "given AuthorizedMember with unknown contract should return error"(){
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createAuthorizedMemberToPersist()
        authorizedMember.contract.id = "123"
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'CONTRACT_NOT_FOUND'
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

    void 'given AuthorizedMember without paymentInstrument and contractor without digital wallet should return error'(){
        given:
        AuthorizedMember authorizedMember = from(AuthorizedMember.class).gimme("valid", new Rule() {{
            add("paymentInstrument", null)
            add("contract", fixtureCreator.createContract())
        }})
        when:
        service.create(authorizedMember)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.first().logref == 'PREVIOUS_DIGITAL_WALLET_OR_PAYMENT_INSTRUMENT_REQUIRED'
    }

    void """given AuthorizedMember without paymentInstrument and contractor with digital wallet
            should define paymentInstrument as digital wallet"""(){
        given:
        def expectedInstrumentNumber = "54646546"
        AuthorizedMember authorizedMember = from(AuthorizedMember.class).gimme("valid", new Rule() {{
            add("paymentInstrument", null)
            add("contract", fixtureCreator.createPersistedContract())
        }})
        createInstrument(authorizedMember.contract.contractor,
                expectedInstrumentNumber, PaymentInstrumentType.DIGITAL_WALLET)

        when:
        def created = service.create(authorizedMember)
        then:
        created.paymentInstrument.type == PaymentInstrumentType.DIGITAL_WALLET
        created.paymentInstrument.number == expectedInstrumentNumber
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


    void 'given known AuthorizedMember should update for hirer'() {
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

    void 'given known AuthorizedMember from another hirer should not be updated for hirer'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        def hirer = fixtureCreator.createHirer()
        when:
        service.updateForHirer(authorizedMember.id, hirer, authorizedMember)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'given known AuthorizedMember should update for contractor'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        def contractor = authorizedMember.contract.contractor
        when:
        service.updateForContractor(authorizedMember.id, contractor, authorizedMember)

        then:
        def found = service.findById(authorizedMember.id)
        found.name == authorizedMember.name
    }

    void 'given known AuthorizedMember from another contractor should not be updated for contractor'() {
        given:
        AuthorizedMember authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        authorizedMember.name = "new name"
        def contractor = fixtureCreator.createContractor("valid")
        when:
        service.updateForContractor(authorizedMember.id, contractor, authorizedMember)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
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

    void 'should not delete AuthorizedMember from another Hirer'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        when:
        service.deleteForHirer(authorizedMember.id, fixtureCreator.createHirer())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should delete AuthorizedMember by Contractor'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        when:
        service.deleteForContractor(authorizedMember.id, authorizedMember.contract.contractor)
        service.findById(authorizedMember.id)

        then:
        thrown(NotFoundException)
    }

    void 'should not delete AuthorizedMember from another Contractor'(){
        given:
        def authorizedMember = fixtureCreator.createPersistedAuthorizedMember()
        when:
        service.deleteForContractor(authorizedMember.id, fixtureCreator.createContractor("valid"))

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'AUTHORIZED_MEMBER_NOT_FOUND'
    }

    void 'should create AuthorizedMembers from csv'() {
        given:
        def contractor = createContractor("123456789")
        def hirer = fixtureCreator.createHirerWithDocument("12345678")
        createPersistedContract(hirer, contractor, 123456L, createProduct("123"))
        createPersistedContract(hirer, contractor, 123457L, createProduct("1234"))
        createPersistedContract(hirer, contractor, 123458L, createProduct("1235"))

        createInstrument(contractor, "123456")
        createInstrument(contractor, "123457")
        createInstrument(contractor, "123458", PaymentInstrumentType.DIGITAL_WALLET)

        Resource csv  = resourceLoader.getResource("classpath:/AuthorizedMember.csv")
        MultipartFile file = new MockMultipartFile('file', csv.getInputStream())

        when:
        service.createFromCsv(file)

        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> member1 = service.findByFilter(new AuthorizedMemberFilter(name: "test"), page)
        Page<AuthorizedMember> member2 = service.findByFilter(new AuthorizedMemberFilter(name: "tset"), page)
        Page<AuthorizedMember> member3 = service.findByFilter(new AuthorizedMemberFilter(name: "sete"), page)

        then:
        member1.first().relatedness == Relatedness.SIBLING
        member2.first().relatedness == Relatedness.FATHER
        member3.first().relatedness == Relatedness.MOTHER
        member1.first().email == "test@test.com"
        member2.first().email == "test@test.bra"
        member3.first().email == "test@sett.com"
        member1.first().document.number == "123456"
        member2.first().document.number == "123457"
        member3.first().document.number == "123458"
        member1.first().paymentInstrument.number == "123456"
        member2.first().paymentInstrument.number == "123457"
        member3.first().paymentInstrument.number == "123458"
        member1.first().gender == Gender.FEMALE
        member2.first().gender == Gender.FEMALE
        member3.first().gender == Gender.MALE
    }

    void 'should create AuthorizedMembers from csv for hirer'() {
        given:
        def contractor = createContractor("123456789")
        def hirer = fixtureCreator.createHirerWithDocument("12345678")
        createPersistedContract(hirer, contractor, 123456L, createProduct("123"))
        createPersistedContract(hirer, contractor, 123457L, createProduct("1234"))
        createPersistedContract(hirer, contractor, 123458L, createProduct("1235"))

        createInstrument(contractor, "123456")
        createInstrument(contractor, "123457")
        createInstrument(contractor, "123458", PaymentInstrumentType.DIGITAL_WALLET)

        Resource csv  = resourceLoader.getResource("classpath:/AuthorizedMember.csv")
        MultipartFile file = new MockMultipartFile('file', csv.getInputStream())

        when:
        service.createFromCsvForHirer(hirer.documentNumber,file)

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

    void 'given a known contract id as filter should return an authorized member for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember = from(AuthorizedMember.class)
                .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
        }})

        AuthorizedMemberFilter filter = new AuthorizedMemberFilter()
        filter.contractId = contract.getId()
        filter.networkId = contract.productNetworkId()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> authorizedMembers = service.findByFilter(filter, page)

        then:
        assert authorizedMembers.content.size() > 0
    }

    void 'given a unknown contract id as filter should not return an authorized member for a logged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()

        AuthorizedMemberFilter filter = new AuthorizedMemberFilter()
        filter.contractId = contract.getId()
        filter.networkId = contract.productNetworkId()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> authorizedMembers = service.findByFilter(filter, page)

        then:
        assert authorizedMembers.content.size() == 0
    }

    void 'given a known contract id as filter should not return an authorized member for a unlogged network'(){
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        AuthorizedMember authorizedMember = from(AuthorizedMember.class)
                .uses(jpaProcessor).gimme("valid", new Rule(){{
            add("contract", contract)
        }})

        AuthorizedMemberFilter filter = new AuthorizedMemberFilter()
        filter.contractId = contract.getId()
        filter.networkId = fixtureCreator.createNetwork()

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AuthorizedMember> authorizedMembers = service.findByFilter(filter, page)

        then:
        assert authorizedMembers.content.size() == 0
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

    private Contract createPersistedContract(hirer, contractor, contractCode, product = createProduct()) {
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
