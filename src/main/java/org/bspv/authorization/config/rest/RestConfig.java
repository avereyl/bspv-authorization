package org.bspv.authorization.config.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class RestConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
      final List<MediaType> defaultMediaTypes = Collections.unmodifiableList(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.ALL));
      // First try the default, if the method produces (@RequestMapping#produces) something else use any.
      configurer.defaultContentTypeStrategy(request -> defaultMediaTypes).favorParameter(true);
    }
    
}
