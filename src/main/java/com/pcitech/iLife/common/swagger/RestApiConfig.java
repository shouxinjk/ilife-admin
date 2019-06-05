package com.pcitech.iLife.common.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
//import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * Title:RestApiConfig 
 * Description:
 * Company:
 * @author jcx
 * @date 2017年8月22日 下午4:16:09
 */

@EnableWebMvc
@EnableSwagger2
@Configuration
@ComponentScan(basePackages ="com.pcitech.iLife.modules")
public class RestApiConfig extends WebMvcConfigurationSupport {
String AUTH_SERVER = "http://www.shouxinjk.net/ilife";
String CLIENT_ID = "ilife";
String CLIENT_SECRET="ilife";

    @Bean
    public Docket createRestApi() {
    		ParameterBuilder tokenPar = new ParameterBuilder();  
        List<Parameter> pars = new ArrayList<Parameter>();  
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();  
        pars.add(tokenPar.build());  
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pcitech.iLife.modules"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
                //.securitySchemes(Arrays.asList(securityScheme()))
                //.securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API文档")
                .termsOfServiceUrl("")
                .contact(new Contact("","",""))
                .version("1.0.0")
                .build();
    }
    
    /**
    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .scopeSeparator(" ")
            .useBasicAuthenticationWithAccessCodeGrant(true)
            .build();
    }
    
    private SecurityScheme securityScheme() {
        GrantType grantType = new AuthorizationCodeGrantBuilder()
            .tokenEndpoint(new TokenEndpoint(AUTH_SERVER + "/token", "oauthtoken"))
            .tokenRequestEndpoint(
              new TokenRequestEndpoint(AUTH_SERVER + "/authorize", CLIENT_ID, CLIENT_ID))
            .build();
     
        SecurityScheme oauth = new OAuthBuilder().name("spring_oauth")
            .grantTypes(Arrays.asList(grantType))
            .scopes(Arrays.asList(scopes()))
            .build();
        return oauth;
    }

    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = { 
          new AuthorizationScope("read", "for read operations"), 
          new AuthorizationScope("write", "for write operations"), 
          new AuthorizationScope("foo", "Access foo API") };
        return scopes;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
          .securityReferences(
            Arrays.asList(new SecurityReference("spring_oauth", scopes())))
          .forPaths(PathSelectors.regex("/foos.*"))
          .build();
    }
    
    //**/
    
}
