package com.mbt.estore.core;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreAxonConfig {
    @Bean
    public XStream XStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[] {
                "com.mbt.estore.**"
        });
        return xStream;
    }
}
