package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ElasticError implements BaseErrorCode {

    _ES_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "ES503-CONNECTION_FAILED", "Elasticsearch 서버에 연결할 수 없습니다."),
    _ES_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "ES503-TIMEOUT", "Elasticsearch 요청 시간이 초과되었습니다."),
    _ES_CLUSTER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "ES503-CLUSTER_UNAVAILABLE", "Elasticsearch 클러스터를 사용할 수 없습니다."),

    _ES_INDEX_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-INDEX_CREATE_FAILED", "Elasticsearch 인덱스 생성에 실패했습니다."),
    _ES_INDEX_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-INDEX_DELETE_FAILED", "Elasticsearch 인덱스 삭제에 실패했습니다."),
    _ES_INDEX_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-INDEX_NOT_FOUND", "Elasticsearch 인덱스를 찾을 수 없습니다."),
    _ES_INDEX_EXISTS_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-INDEX_EXISTS_CHECK_FAILED", "인덱스 존재 여부 확인에 실패했습니다."),

    _ES_DOCUMENT_INDEX_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-DOCUMENT_INDEX_FAILED", "문서 인덱싱에 실패했습니다."),
    _ES_BULK_INDEX_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-BULK_INDEX_FAILED", "벌크 인덱싱에 실패했습니다."),
    _ES_BULK_PARTIAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-BULK_PARTIAL_FAILED", "일부 문서 인덱싱에 실패했습니다."),
    _ES_DOCUMENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-DOCUMENT_DELETE_FAILED", "문서 삭제에 실패했습니다."),
    _ES_DOCUMENT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-DOCUMENT_UPDATE_FAILED", "문서 업데이트에 실패했습니다."),

    _ES_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-SEARCH_FAILED", "Elasticsearch 검색에 실패했습니다."),
    _ES_QUERY_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-QUERY_EXECUTION_FAILED", "검색 쿼리 실행에 실패했습니다."),

    _ES_ANALYZER_CONFIG_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-ANALYZER_CONFIG_FAILED", "분석기 설정에 실패했습니다. (Nori 플러그인 확인 필요)"),
    _ES_MAPPING_CONFIG_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-MAPPING_CONFIG_FAILED", "매핑 설정에 실패했습니다."),
    _ES_SETTINGS_CONFIG_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ES500-SETTINGS_CONFIG_FAILED", "인덱스 설정에 실패했습니다."),

    _ES_INVALID_QUERY(HttpStatus.BAD_REQUEST, "ES400-INVALID_QUERY", "잘못된 검색 쿼리입니다."),
    _ES_INVALID_DOCUMENT(HttpStatus.BAD_REQUEST, "ES400-INVALID_DOCUMENT", "잘못된 문서 형식입니다."),
    _ES_INVALID_INDEX_NAME(HttpStatus.BAD_REQUEST, "ES400-INVALID_INDEX_NAME", "잘못된 인덱스 이름입니다."),
    _ES_INVALID_MAPPING(HttpStatus.BAD_REQUEST, "ES400-INVALID_MAPPING", "잘못된 매핑 정의입니다."),

    _ES_DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ES404-DOCUMENT_NOT_FOUND", "요청한 문서를 찾을 수 없습니다."),
    _ES_SEARCH_NO_RESULT(HttpStatus.NOT_FOUND, "ES404-SEARCH_NO_RESULT", "검색 결과가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
