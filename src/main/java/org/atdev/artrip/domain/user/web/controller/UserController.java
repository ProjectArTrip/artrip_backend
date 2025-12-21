package org.atdev.artrip.domain.user.web.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;


}
