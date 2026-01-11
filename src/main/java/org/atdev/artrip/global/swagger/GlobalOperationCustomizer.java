package org.atdev.artrip.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.security.PermitAll;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GlobalOperationCustomizer implements OperationCustomizer {

    private boolean isSecuredApi(HandlerMethod handlerMethod) {
        return !(
                handlerMethod.hasMethodAnnotation(PermitAll.class)
                        || handlerMethod.getBeanType().isAnnotationPresent(PermitAll.class)
        );
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses responses = operation.getResponses();

        if (isSecuredApi(handlerMethod)) {
            addOrMergeErrorResponseWithExamples(
                    responses,
                    "401",
                    List.of(
                            UserErrorCode._JWT_INVALID_TOKEN,
                            UserErrorCode._JWT_EXPIRED_ACCESS_TOKEN,
                            UserErrorCode._SOCIAL_TOKEN_INVALID_SIGNATURE,
                            UserErrorCode._JWT_UNSUPPORTED_TOKEN
                    )
            );
        }

        ApiErrorResponses annotation = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
        if (annotation != null) {
            processAllErrorAttributes(responses, annotation);
        }

        return operation;
    }


    private void processAllErrorAttributes(ApiResponses responses, ApiErrorResponses annotation) {

        List<BaseErrorCode> allErrors = new ArrayList<>();

        for (Method method : ApiErrorResponses.class.getDeclaredMethods()) {
            try {
                Object value = method.invoke(annotation);
                if (value instanceof BaseErrorCode[]) {
                    allErrors.addAll(Arrays.asList((BaseErrorCode[]) value));
                }
            } catch (Exception e) {
            }
        }

        Map<String, List<BaseErrorCode>> errorsByStatusCode = allErrors.stream()
                .collect(Collectors.groupingBy(
                        error -> String.valueOf(error.getHttpStatus().value()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        errorsByStatusCode.forEach((statusCode, errors) -> {
            addOrMergeErrorResponseWithExamples(responses, statusCode, errors);
        });
    }

    private void addOrMergeErrorResponseWithExamples(
            ApiResponses responses,
            String statusCode,
            List<BaseErrorCode> errorsToAdd
    ) {
        ApiResponse apiResponse = responses.get(statusCode);
        if (apiResponse == null) {
            apiResponse = new ApiResponse();
            apiResponse.setDescription(errorsToAdd.get(0).getHttpStatus().getReasonPhrase());
            apiResponse.setContent(new Content());
            responses.addApiResponse(statusCode, apiResponse);
        }

        MediaType mediaType = apiResponse.getContent().get("application/json");
        if (mediaType == null) {
            mediaType = new MediaType();
            mediaType.setSchema(new Schema<>().$ref("#/components/schemas/CommonResponse"));
            apiResponse.getContent().addMediaType("application/json", mediaType);
        }

        Map<String, Example> exampleMap = mediaType.getExamples();
        if (exampleMap == null) {
            exampleMap = new LinkedHashMap<>();
        }

        for (BaseErrorCode error : errorsToAdd) {
            exampleMap.putIfAbsent(error.getCode(), new Example()
                    .value(createErrorExample(error))
                    .description(error.getMessage()));
        }

        mediaType.setExamples(exampleMap);
    }


    private Map<String, Object> createErrorExample(BaseErrorCode error) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("isSuccess", false);
        example.put("code", error.getCode());
        example.put("message", error.getMessage());
        example.put("result", null);
        return example;
    }


}
