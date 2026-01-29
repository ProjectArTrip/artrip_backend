package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.KeywordListResponse;
import org.atdev.artrip.controller.spec.KeywordSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.KeywordService;
import org.atdev.artrip.controller.dto.request.KeywordRequest;
import org.atdev.artrip.service.dto.command.KeywordCommand;
import org.atdev.artrip.service.dto.result.KeywordResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keyword")
public class UserKeywordController implements KeywordSpecification {

    private final KeywordService keywordService;

    @Override
    @PostMapping
    public ResponseEntity<Void> saveKeywords(
            @LoginUser Long userId,
            @RequestBody KeywordRequest request) {

        keywordService.saveKeywords(userId,request.keywords());
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<KeywordListResponse> getAllKeywords() {

        List<KeywordResult> keywords = keywordService.getAllKeywords();
        KeywordListResponse response = KeywordListResponse.from(keywords);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<KeywordListResponse> getKeyword(@LoginUser Long userId) {

        List<KeywordResult> keywords = keywordService.getKeyword(userId);
        KeywordListResponse response = KeywordListResponse.from(keywords);

        return ResponseEntity.ok(response);
    }


}

