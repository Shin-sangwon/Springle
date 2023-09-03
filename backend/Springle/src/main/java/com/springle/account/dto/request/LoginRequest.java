package com.springle.account.dto.request;

public record LoginRequest (
    String loginId,
    String loginPassword
) {

}
