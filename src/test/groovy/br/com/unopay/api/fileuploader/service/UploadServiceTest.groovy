package br.com.unopay.api.fileuploader.service

import spock.lang.Specification
import spock.lang.Unroll

class UploadServiceTest extends Specification {

    @Unroll
    void 'given a #filename expect #slugfiedName'() {
        given:
        AbstractUploadService uploadService = new AbstractUploadService() {
            @Override
            String getRelativePath(String service) {
                return null
            }
        }
        when:
        String fileNameResult = uploadService.slugfyIgnoringExtension(filename)
        then:
        slugfiedName == fileNameResult
        where:
        filename                              || slugfiedName
        'Filé namẽ withí (extensions) .jpg'   || 'file-name-withi-extensions.jpg'
        'file-name-with-no-extension'         || 'file-name-with-no-extension'
        '&xemplóó de - nome - com espaço.SVG' || 'exemploo-de-nome-com-espaco.svg'
        'téste de exemplo sem extension'      || 'teste-de-exemplo-sem-extension'
        '20150728173214389_ciot.jpg'        || '20150728173214389-ciot.jpg'
        'test@aí.jpg'                         || 'testai.jpg'
        'test@aí...PNG'                       || 'testai.png'
    }
}