package nextstep.member.acceptance;

import static nextstep.member.acceptance.MemberSteps.내정보_조회_요청;
import static nextstep.member.acceptance.MemberSteps.회원_로그인_요청_후_token_추출;
import static nextstep.member.acceptance.MemberSteps.회원_삭제_요청;
import static nextstep.member.acceptance.MemberSteps.회원_생성_요청;
import static nextstep.member.acceptance.MemberSteps.회원_정보_수정_요청;
import static nextstep.member.acceptance.MemberSteps.회원_정보_조회_요청;
import static nextstep.member.acceptance.MemberSteps.회원_정보_조회됨;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.utils.AcceptanceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final int AGE = 20;

    @DisplayName("회원가입을 한다.")
    @Test
    void createMember() {
        // when
        var response = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_정보_조회_요청(createResponse);

        // then
        회원_정보_조회됨(response, EMAIL, AGE);

    }

    @DisplayName("회원 정보를 수정한다.")
    @Test
    void updateMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_정보_수정_요청(createResponse, "new" + EMAIL, "new" + PASSWORD, AGE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("회원 정보를 삭제한다.")
    @Test
    void deleteMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_삭제_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() {

        //given
        var createResponse = 회원_정보_조회_요청(회원_생성_요청(EMAIL, PASSWORD, AGE));

        String accessToken = 회원_로그인_요청_후_token_추출(EMAIL, PASSWORD);

        //when
         var response = 내정보_조회_요청(accessToken);

         //then
        Assertions.assertAll(
            () -> assertThat(response.jsonPath().getString("id"))
                .isEqualTo(createResponse.jsonPath().getString("id")),
            () -> assertThat(response.jsonPath().getString("email"))
                .isEqualTo(createResponse.jsonPath().getString("email")),
            () -> assertThat(response.jsonPath().getInt("age"))
                .isEqualTo(createResponse.jsonPath().getInt("age"))
        );
    }

    @DisplayName("내 정보를 조회요청시 토큰값을 검증한다.")
    @Test
    void validTokenAtGettingMyInfo() {

        //given
        var createResponse = 회원_정보_조회_요청(회원_생성_요청(EMAIL, PASSWORD, AGE));

        //when
        var response = 내정보_조회_요청("아무토큰~~~");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}