package org.atdev.artrip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ArtripAPI() {
        Info info = new Info()
                .title("Artrip Server API")
                .description("API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSchemas("CommonResponse", createCommonResponseSchema());

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Schema<?> createCommonResponseSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("공통 응답 형식");

        Schema<?> isSuccessSchema = new Schema<>();
        isSuccessSchema.setType("boolean");
        isSuccessSchema.setDescription("요청 성공 여부");
        isSuccessSchema.setExample(false);

        Schema<?> codeSchema = new Schema<>();
        codeSchema.setType("string");
        codeSchema.setDescription("응답 코드");
        codeSchema.setExample("COMMON400");

        Schema<?> messageSchema = new Schema<>();
        messageSchema.setType("string");
        messageSchema.setDescription("응답 메시지");
        messageSchema.setExample("잘못된 요청입니다.");

        Schema<?> resultSchema = new Schema<>();
        resultSchema.setType("object");
        resultSchema.setNullable(true);
        resultSchema.setDescription("응답 데이터");

        schema.addProperty("isSuccess", isSuccessSchema);
        schema.addProperty("code", codeSchema);
        schema.addProperty("message", messageSchema);
        schema.addProperty("result", resultSchema);

        return schema;
    }
}