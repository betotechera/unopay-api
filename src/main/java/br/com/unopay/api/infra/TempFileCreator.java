package br.com.unopay.api.infra;

import java.io.File;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class TempFileCreator {

    @SneakyThrows
    public File createTempFile(Resource resource){
        InputStream inputStream = resource.getInputStream();
        File somethingFile = File.createTempFile(resource.getFilename(), "-unopay-api");
        try {
            FileUtils.copyInputStreamToFile(inputStream, somethingFile);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return somethingFile;
    }
}
