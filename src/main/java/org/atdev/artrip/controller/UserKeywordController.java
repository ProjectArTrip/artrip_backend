package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.spec.KeywordSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.KeywordService;
import org.atdev.artrip.controller.dto.request.KeywordRequest;
import org.atdev.artrip.controller.dto.response.KeywordResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.KeywordErrorCode;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.command.KeywordCommand;
import org.atdev.artrip.service.dto.result.KeywordResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserKeywordController implements KeywordSpecification {

    private final KeywordService keywordService;

    @Override
    @PostMapping("/keywords")
    public ResponseEntity<Void> saveUserKeywords(
            @LoginUser Long userId,
            @RequestBody KeywordRequest request) {


        KeywordCommand command= request.toCommand(userId);

        keywordService.saveUserKeywords(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/allKeywords")
    public ResponseEntity<List<KeywordResponse>> getAllKeywords() {

        List<KeywordResult> keywords = keywordService.getAllKeywords();
        List<KeywordResponse> responses = KeywordResponse.from(keywords);

        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordResponse>> getUserKeywords(@LoginUser Long userId) {

        List<KeywordResult> keywords = keywordService.getUserKeywords(userId);
        List<KeywordResponse> responses = KeywordResponse.from(keywords);

        return ResponseEntity.ok(responses);
    }


}

