package atdd.user.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.user.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

import atdd.AbstractAcceptanceTest;
import atdd.auth.application.dto.AuthInfoView;
import atdd.user.application.dto.UserResponseView;

public class UserManageAcceptanceTest extends AbstractAcceptanceTest {
  private UserManageHttpTest userManageHttpTest;

  @BeforeEach
  void setUp() {
    this.userManageHttpTest = new UserManageHttpTest(webTestClient);
  }

  @Test
  public void createUser() {
    //When
    Long userID = userManageHttpTest.createNewUser(
        USER_1_EMAIL, 
        USER_1_NAME,
        USER_1_PASSWORD
        );

    EntityExchangeResult<UserResponseView> response = userManageHttpTest.retrieveUser(userID);

    //then
    assertThat(response.getResponseBody().getName()).isEqualTo(USER_1_NAME);
    assertThat(response.getResponseBody().getEmail()).isEqualTo(USER_1_EMAIL);
  }

  @Test
  public void retrieveUser() {
    //Given
    Long userID = userManageHttpTest.createNewUser(
        USER_1_EMAIL, 
        USER_1_NAME,
        USER_1_PASSWORD
        );

    //When
    EntityExchangeResult<UserResponseView> response = userManageHttpTest.retrieveUser(userID);

    //then
    assertThat(response.getResponseBody().getName()).isEqualTo(USER_1_NAME);
    assertThat(response.getResponseBody().getEmail()).isEqualTo(USER_1_EMAIL);
  }

  @Test
  public void deleteUser() {
    //Given
    Long userID = userManageHttpTest.createNewUser(
        USER_1_EMAIL, 
        USER_1_NAME,
        USER_1_PASSWORD
        );

    userManageHttpTest.retrieveUser(userID);

    //When
    webTestClient.delete().uri(USER_URL + "/" + userID.toString())
      .exchange()
      .expectStatus().isNoContent();

    //Then
    webTestClient.get().uri(USER_URL + "/" + userID.toString())
      .exchange()
      .expectStatus().isNotFound();
  }

  @Test
  public void loginUser() {
    //Given
    Long userID = userManageHttpTest.createNewUser(
        USER_1_EMAIL, 
        USER_1_NAME,
        USER_1_PASSWORD
        );

    //When
    EntityExchangeResult<AuthInfoView> result = userManageHttpTest.loginUser(
        USER_1_EMAIL, USER_1_PASSWORD);

    //then
    AuthInfoView authInfoView = result.getResponseBody();
    assertThat(authInfoView.getAccessToken()).isNotNull();
    assertThat(authInfoView.getTokenType()).isNotNull();
  }

  @Test
  public void getMyInfo() {
    //Given
    Long userID = userManageHttpTest.createNewUser(
        USER_1_EMAIL, 
        USER_1_NAME,
        USER_1_PASSWORD
        );
    AuthInfoView authInfoView = userManageHttpTest.loginUser(
        USER_1_EMAIL, USER_1_PASSWORD).getResponseBody();

    //When
    EntityExchangeResult<UserResponseView> result = userManageHttpTest.getMyResult(authInfoView);

    //Then
    UserResponseView userResponseView = result.getResponseBody();
    assertThat(userResponseView.getId()).isEqualTo(userID);
    assertThat(userResponseView.getEmail()).isEqualTo(USER_1_EMAIL);
  }
}