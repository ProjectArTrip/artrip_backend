package org.atdev.artrip.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GlobalOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses responses = operation.getResponses();

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
            addErrorResponseWithMultipleExamples(responses, statusCode, errors);
        });
    }

    private void addErrorResponseWithMultipleExamples(ApiResponses responses, String statusCode, List<BaseErrorCode> errors) {
        ApiResponse apiResponse = new ApiResponse();

        String description = errors.get(0).getHttpStatus().getReasonPhrase();
        apiResponse.setDescription(description);

        Content content = new Content();
        MediaType mediaType = new MediaType();

        Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/CommonResponse");
        mediaType.setSchema(schema);

        Map<String, Example> exampleMap = new LinkedHashMap<>();
        for (BaseErrorCode error :errors) {
            Example example = new Example();
            example.setValue(createErrorExample(error));
            example.setDescription(error.getMessage());

            exampleMap.put(error.getCode(), example);
        }

        mediaType.setExamples(exampleMap);
        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);

        responses.addApiResponse(statusCode, apiResponse);

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
