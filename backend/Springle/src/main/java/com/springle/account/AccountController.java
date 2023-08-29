package com.springle.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/account")
@RestController
public class AccountController {

    @GetMapping("/login")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("security");
    }
}
