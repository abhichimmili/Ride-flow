
package com.vcube.rideflow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// this is for authorize option in swagger,to test protected end points through bearer token
@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI rideFlowOpenAPI() {
		String schemeName = "Bearer Authentication";

		return new OpenAPI()
				.components(new Components().addSecuritySchemes(schemeName,
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(schemeName));
	}
}
