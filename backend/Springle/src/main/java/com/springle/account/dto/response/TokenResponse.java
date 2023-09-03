package com.springle.account.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {

}
