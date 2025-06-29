package app.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bus application API",
                version = "1.0"
        ),
        security = {@SecurityRequirement(name = "jwtToken")}
)
@SecuritySchemes({
        @SecurityScheme(name = "jwtToken", type = SecuritySchemeType.HTTP, scheme = "bearer",
                bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
})
public class SwaggerConfig {}