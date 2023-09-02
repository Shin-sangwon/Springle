package com.springle.account.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springle.account.dto.request.RegistrationRequest;
import com.springle.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AccountControllerTest extends ControllerTest {

    private final String baseUrl = "/api/v1/account";

    @DisplayName("회원 가입한다")
    @Test
    void memberJoinTest() throws Exception {

        RegistrationRequest request = new RegistrationRequest("loginId", "loginPassword", "Name",
            "email@email.com");

        mockMvc.perform(
                   post(baseUrl + "/join")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsBytes(request))
               )
               .andExpect(status().isNoContent())
               .andDo(print())
               .andDo(document(
                   "account/join",
                   preprocessRequest(prettyPrint()),
                   requestFields(
                       fieldWithPath("loginId").description("로그인 아이디"),
                       fieldWithPath("loginPassword").description("로그인 패스워드"),
                       fieldWithPath("name").description("이름"),
                       fieldWithPath("email").description("이메일")
                   )
               ));
    }

}