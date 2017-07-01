package br.com.unopay.api.infra;

import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnopayEncryptor {

    private BytesEncryptor encryptor;

    public UnopayEncryptor(){}

    @Autowired
    public UnopayEncryptor(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public String encrypt(byte[] encrypt) {
        byte[] encryptedPassword = encryptor.encrypt(encrypt);
        return DatatypeConverter.printBase64Binary(encryptedPassword);
    }

    public String decrypt(String decrypt){
        return new String(encryptor.decrypt(DatatypeConverter.parseBase64Binary(decrypt)));
    }
}
