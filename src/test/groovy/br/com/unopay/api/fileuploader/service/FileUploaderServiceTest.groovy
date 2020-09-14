package br.com.unopay.api.fileuploader.service

import br.com.unopay.api.SpockApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Ignore

class FileUploaderServiceTest extends SpockApplicationTests {


    @Autowired
    FileUploaderService service

    @Autowired
    ResourceLoader resourceLoader

    @Value('${amazon.s3.cdn.uri}')
    String cdn

    @Ignore
    def'given a existing file should be uploaded'(){
        given:
        Resource createPassword  = resourceLoader.getResource("classpath:/create-password.html")
        MultipartFile file = new MockMultipartFile('file', createPassword.getInputStream())

        when:
        def path = service.upload(file, 'avatar')
        service.deleteFile(path.replaceAll(cdn + '/', ''))

        then:
        notThrown(Exception)
    }

}
