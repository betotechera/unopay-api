package br.com.unopay.api.model

import br.com.unopay.api.SpockApplicationTests
import static br.com.unopay.api.model.PersonType.LEGAL
import static br.com.unopay.api.model.PersonType.PHYSICAL

class DocumentTypeTest extends SpockApplicationTests {

    def "given a PHYSICAL DocumentType, it should be valid only for one PersonType"() {
        when:
        DocumentType  documentType = DocumentType.CNH
        then:
        assert  documentType.isValidDocumentFor(PHYSICAL)
        assert  !documentType.isValidDocumentFor(LEGAL)
    }

    def "given a LEGAL DocumentType, it should be valid only for one PersonType"() {
        when:
        DocumentType  documentType = DocumentType.CNPJ
        then:
        assert  documentType.isValidDocumentFor(LEGAL)
        assert  !documentType.isValidDocumentFor(PHYSICAL)
    }


    def "given a default DocumentType, it should be valid for both PersonType"() {
        when:
        DocumentType  documentType = DocumentType.RNTRC
        then:
        assert  documentType.isValidDocumentFor(PHYSICAL)
        assert  documentType.isValidDocumentFor(LEGAL)
    }
}
