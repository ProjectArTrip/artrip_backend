package org.atdev.artrip;

import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StatusController {

    private static final Map<Long, String> users = new HashMap<>();
    static {
        users.put(1L, "aa");
        users.put(2L, "bb");
    }
    @GetMapping("/")
    public StatusResponse getStatus() {
        return new StatusResponse("200", "greeting");
    }

    @GetMapping("/a")
    public ResponseEntity<ApiResponse<List<String>>> getAllUsers() {
        List<String> allUsers = new ArrayList<>(users.values());
        return ResponseEntity.ok(ApiResponse.onSuccess(allUsers));
    }
}
