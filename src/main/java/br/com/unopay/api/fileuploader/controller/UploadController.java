package br.com.unopay.api.fileuploader.controller;

import br.com.unopay.api.fileuploader.service.FileUploaderService;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@RestController
@Validated
@Timed(prefix = "file-uploader")
public class UploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    private static final Pattern IMAGES_PATTERN =
            Pattern.compile("^.+\\.(png|jpe?g|gif|bmp|svg)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOC_PATTERN = Pattern.compile("^.+\\.(pdf|doc?)$", Pattern.CASE_INSENSITIVE);

    private FileUploaderService fileUploaderService;

    @Autowired
    public UploadController(FileUploaderService fileUploaderService) {
        this.fileUploaderService = fileUploaderService;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/images/{service}", method = RequestMethod.POST,
            consumes = "multipart/form-data", produces = "text/plain")
    public String uploadPictureFile(
            @RequestParam MultipartFile file, @PathVariable String service){
        String fileName = file.getOriginalFilename();
        LOGGER.info("uploading file {}", fileName);
        if (!IMAGES_PATTERN.matcher(fileName).matches()) {
            throw new BadRequestException("File extension not supported");
        }
        return fileUploaderService.upload(file,service);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/docs/{service}", method = RequestMethod.POST,
            consumes = "multipart/form-data", produces = "text/plain")
    public String uploadDocFile(
            @RequestParam MultipartFile file, @PathVariable String service) {
        String fileName = file.getOriginalFilename();
        LOGGER.info("uploading file {}", fileName);
        if (!DOC_PATTERN.matcher(fileName).matches()) {
            throw new BadRequestException("File extension not supported");
        }
        return fileUploaderService.upload(file,service);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/images", method = RequestMethod.POST,
            consumes = "multipart/form-data", produces = "text/plain")
    public String uploadImage(@RequestParam MultipartFile file, @RequestParam String imagePath) {
        String fileName = file.getOriginalFilename();
        if (!IMAGES_PATTERN.matcher(fileName).matches()) {
            throw new BadRequestException("File extension not supported");
        }
        return fileUploaderService.uploadFile(file, imagePath);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/images", method = RequestMethod.DELETE)
    public void delete(@RequestBody String filePath){
        fileUploaderService.deleteFile(filePath);
    }
}