package qyy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Configuration
public class PicConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String tempPath =System.getProperty("java.io.tmpdir") + "images/";

        registry.addResourceHandler("/getpic/**")
                .addResourceLocations("file:"+tempPath);

    }

}
