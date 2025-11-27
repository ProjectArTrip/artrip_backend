package org.atdev.artrip.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalOperationCustomizer implements OperationCustomizer {

    // 하기의 주석은 각 메서드에 숙지가 완료되면 삭제 예정입니다.

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses responses = operation.getResponses();


        ApiErrorResponses annotation = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
        if (annotation != null) {
            processAllErrorAttributes(responses, annotation);
        }
        return operation;
    }

    /*
     * ArtripError를 기반으로 에러 응답 추가 (중복체크)
     * 이미 같은 HTTP 상태 코드가 있으면 추가 하지 않습니다.
     */
    private void addErrorResponseFromStatusIfNotExists(ApiResponses responses, BaseErrorCode error) {
        String statusCode = String.valueOf(error.getHttpStatus().value());
        if (!responses.containsKey(statusCode)) {
            addErrorResponseFromStatus(responses, error);
        }
    }

    /*
    * ArtripError에 에러 응답을 생성
    *
    * 생성 내용:
    * - HTTP 상태코드
    * - 설명 (메시지 + 코드)
    * - 응답 스키마 (CommonResponse)
    **/
    private void addErrorResponseFromStatus(ApiResponses responses, BaseErrorCode error) {
        String statusCode = String.valueOf(error.getHttpStatus().value());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription(
                String.format("%s (%s)", error.getMessage(), error.getCode())
        );

        Content content = new Content();
        MediaType mediaType = new MediaType();

        Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/CommonResponse");
        mediaType.setSchema(schema);

        Example example = new Example();
        example.setValue(createErrorExample(error));
        example.setDescription(error.getMessage());

        Map<String, Example> exampleMap = new LinkedHashMap<>();
        exampleMap.put(error.getCode(), example);
        mediaType.setExamples(exampleMap);

        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);

        responses.addApiResponse(statusCode, apiResponse);

    }

    /*
    * ArtripError 예시 JSON
    *
    * 생성 형식 :
    * {
    *   "isSuccess": false,
    *   "code": "EXHIBIT404-NOT_FOUND",
    *   "message": "Error message",
    *   "result": null
    * }*/
    private Map<String, Object> createErrorExample(BaseErrorCode error) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("isSuccess", false);
        example.put("code", error.getCode());
        example.put("message", error.getMessage());
        example.put("result", null);
        return example;
    }

    /*
     * @ApiErrorResponses 어노테이션에서 정의된 모든 에러를 처리
     * 
     * 동작 방식:
     * 1. 어노테이션의 모든 속성(common, user, exhibit, review, file)을 순회
     * 2. 각 속성에서 정의된 BaseErrorCode[] 배열을 추출
     * 3. 중복 체크 후 에러 응답 추가 (이미 있는 상태 코드는 건너뜀)
     */
    private void processAllErrorAttributes(ApiResponses responses, ApiErrorResponses annotation) {
        for (Method method : ApiErrorResponses.class.getDeclaredMethods()) {
            try {
                Object value = method.invoke(annotation);

                if (value instanceof BaseErrorCode[]) {
                    BaseErrorCode[] errorCodes = (BaseErrorCode[]) value;
                    for (BaseErrorCode error : errorCodes) {
                        addErrorResponseFromStatusIfNotExists(responses, error);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

}
