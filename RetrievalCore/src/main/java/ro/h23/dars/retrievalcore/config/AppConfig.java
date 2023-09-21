package ro.h23.dars.retrievalcore.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.h23.dars.retrievalcore.service.HashService;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class AppConfig {

    @Bean
    public HashService hashService() {
        return new HashService() {

            @Override
            public String computeHash(String data) {
                return DigestUtils.sha3_256Hex(data);
            }

            @Override
            public String computeHash(byte[] data) {
                return DigestUtils.sha3_256Hex(data);
            }

            @Override
            public String computeHash(InputStream data) throws IOException {
                return DigestUtils.sha3_256Hex(data);
            }

        };
    }
}
