package it.infn.mw.iam.test.notification;

import static it.infn.mw.iam.core.IamRegistrationRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamRegistrationRequestStatus.CONFIRMED;
import static it.infn.mw.iam.core.IamRegistrationRequestStatus.REJECTED;
import static it.infn.mw.iam.test.TestUtils.waitIfPortIsUsed;
import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.account.password_reset.PasswordResetController;
import it.infn.mw.iam.api.account.password_reset.PasswordResetService;
import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.core.IamRegistrationRequestStatus;
import it.infn.mw.iam.notification.MockTimeProvider;
import it.infn.mw.iam.notification.NotificationProperties;
import it.infn.mw.iam.notification.NotificationService;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithMockOAuthUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class NotificationTests {

  @Autowired
  @Qualifier("defaultNotificationService")
  private NotificationService notificationService;

  @Autowired
  private NotificationProperties properties;

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  @Value("${iam.organisation.name}")
  private String organisationName;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  @Autowired
  private PasswordResetService passwordResetService;

  @Autowired
  private MockTimeProvider timeProvider;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private PersistentUUIDTokenGenerator generator;

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mvc;

  private Wiser wiser;

  @Before
  public synchronized void setUp() throws InterruptedException {

    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();

    waitIfPortIsUsed(mailHost, mailPort, 30);

    wiser = new Wiser();
    wiser.setHostname(mailHost);
    wiser.setPort(mailPort);
    wiser.start();
  }

  @After
  public synchronized void tearDown() throws InterruptedException {

    wiser.stop();

    notificationRepository.deleteAll();

    if (wiser.getServer().isRunning()) {
      Assert.fail("Fake mail server is still running after stop!!");
    }
  }

  @Test
  public void testSendEmails() throws Exception {

    String username = "test_user";

    createRegistrationRequest(username);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(1));
    WiserMessage message = wiser.getMessages().get(0);

    assertThat(message.getEnvelopeSender(), equalTo(properties.getMailFrom()));
    assertThat(message.getEnvelopeReceiver(), startsWith(username));
    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("confirmation")));

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      assertThat(elem.getDeliveryStatus(), equalTo(IamDeliveryStatus.DELIVERED));
    }
  }

  @Test
  public void testDisableNotificationOption() throws Exception {

    String username = "test_user";

    createRegistrationRequest(username);

    properties.setDisable(true);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(0));

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      assertThat(elem.getDeliveryStatus(), equalTo(IamDeliveryStatus.DELIVERED));
    }

    properties.setDisable(false);
  }

  @Test
  public void testSendMultipleNotifications() throws Exception {

    int count = 3;
    List<RegistrationRequestDto> requestList = new ArrayList<>();

    for (int idx = 1; idx <= count; idx++) {
      RegistrationRequestDto reg = createRegistrationRequest("test_user_" + idx);
      requestList.add(reg);
    }

    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(count));

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      assertThat(elem.getDeliveryStatus(), equalTo(IamDeliveryStatus.DELIVERED));
    }
  }

  @Test
  public void testSendWithEmptyQueue() {

    notificationService.sendPendingNotifications();
    assertThat(wiser.getMessages(), hasSize(0));
  }

  @Test
  public void testDeliveryFailure() throws Exception {

    String username = "test_user";
    createRegistrationRequest(username);

    wiser.stop();

    notificationService.sendPendingNotifications();

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      assertThat(elem.getDeliveryStatus(), equalTo(IamDeliveryStatus.DELIVERY_ERROR));
    }
  }

  @Test
  @WithMockOAuthUser(clientId = "registration-client",
      scopes = {"registration:read", "registration:write"})
  public void testApproveFlowNotifications() throws Exception {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(1));

    WiserMessage message = wiser.getMessages().get(0);

    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("confirmation")));

    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(2));

    message = wiser.getMessages().get(1);

    assertEquals(properties.getSubject().get("adminHandleRequest"),
        message.getMimeMessage().getSubject());

    assertThat(message.getEnvelopeReceiver(), startsWith(properties.getAdminAddress()));

    approveRequest(reg.getUuid());
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(3));

    message = wiser.getMessages().get(2);

    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("activated")));
  }

  @Test
  @WithMockOAuthUser(clientId = "registration-client",
      scopes = {"registration:read", "registration:write"})
  public void testRejectFlowNotifications() throws Exception {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(1));

    WiserMessage message = wiser.getMessages().get(0);
    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("confirmation")));

    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(2));
    message = wiser.getMessages().get(1);
    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("adminHandleRequest")));
    assertThat(message.getEnvelopeReceiver(), startsWith(properties.getAdminAddress()));

    rejectRequest(reg.getUuid());
    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(3));

    message = wiser.getMessages().get(2);
    assertThat(message.getMimeMessage().getSubject(),
        equalTo(properties.getSubject().get("rejected")));
  }

  @Test
  public void testCleanOldMessages() throws Exception {

    String username = "test_user";

    createRegistrationRequest(username);
    notificationService.sendPendingNotifications();
    assertThat(wiser.getMessages(), hasSize(1));

    Date fakeDate = DateUtils.addDays(new Date(), (properties.getCleanupAge() + 1));
    timeProvider.setTime(fakeDate.getTime());

    notificationService.clearExpiredNotifications();

    int count = notificationRepository.countAllMessages();
    assertEquals(0, count);
  }

  @Test
  public void testEveryMailShouldContainSignature() throws Exception {

    String signature = String.format("The %s registration service", organisationName);

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);
    approveRequest(reg.getUuid());

    notificationService.sendPendingNotifications();

    for (WiserMessage message : wiser.getMessages()) {
      assertTrue("text/plain", message.getMimeMessage().isMimeType("text/plain"));
      String content = message.getMimeMessage().getContent().toString();
      assertThat(content, containsString(signature));
    }
  }

  @Test
  public void testConfirmMailShouldContainsConfirmationLink() throws Exception {

    String username = "test_user";

    createRegistrationRequest(username);
    String confirmationKey = generator.getLastToken();

    String confirmURL = format("%s/registration/verify/%s", baseUrl, confirmationKey);

    notificationService.sendPendingNotifications();

    WiserMessage message = wiser.getMessages().get(0);

    assertThat(message.getMimeMessage().isMimeType("text/plain"), is(true));
    String content = message.getMimeMessage().getContent().toString();
    assertThat(content, containsString(confirmURL));
  }

  @Test
  public void testActivationMailShouldContainsResetPasswordLink() throws Exception {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);
    approveRequest(reg.getUuid());
    String resetKey = generator.getLastToken();

    String resetPasswordUrl =
        format("%s%s/%s", baseUrl, PasswordResetController.BASE_TOKEN_URL, resetKey);

    notificationService.sendPendingNotifications();

    WiserMessage message = wiser.getMessages().get(2);

    assertThat(message.getMimeMessage().isMimeType("text/plain"), is(true));
    String content = message.getMimeMessage().getContent().toString();
    assertThat(content, containsString(resetPasswordUrl));
  }

  @Test
  public void testAdminNotificationMailShouldContainsDashboardLink() throws Exception {

    String username = "test_user";

    createRegistrationRequest(username);
    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);

    String dashboardUrl = format("%s/dashboard#/requests", baseUrl);

    notificationService.sendPendingNotifications();

    WiserMessage message = wiser.getMessages().get(1);

    assertThat(message.getMimeMessage().isMimeType("text/plain"), is(true));
    String content = message.getMimeMessage().getContent().toString();
    assertThat(content, containsString(dashboardUrl));
  }

  @Test
  public void testPasswordResetMailContainsUsername() throws Exception {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    String confirmationKey = generator.getLastToken();
    confirmRegistrationRequest(confirmationKey);
    approveRequest(reg.getUuid());
    passwordResetService.createPasswordResetToken(reg.getEmail());

    notificationService.sendPendingNotifications();

    List<WiserMessage> msgList = wiser.getMessages();
    WiserMessage message = wiser.getMessages().get(msgList.size() - 1);

    assertThat(message.getMimeMessage().isMimeType("text/plain"), is(true));
    String content = message.getMimeMessage().getContent().toString();
    assertThat(content, containsString(username));
  }

  private RegistrationRequestDto createRegistrationRequest(String username) throws Exception {

    String email = username + "@example.org";
    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Test");
    request.setFamilyname("User");
    request.setEmail(email);
    request.setUsername(username);
    request.setNotes("Some short notes...");
    request.setPassword("password");

    // @formatter:off
    String response = mvc
      .perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    return objectMapper.readValue(response, RegistrationRequestDto.class);
  }

  private void confirmRegistrationRequest(String confirmationKey) throws Exception {

    // @formatter:off
    mvc.perform(get("/registration/confirm/{token}", confirmationKey))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(CONFIRMED.name())));
    // @formatter:on
  }

  protected RegistrationRequestDto approveRequest(String uuid) throws Exception {

    return requestDecision(uuid, APPROVED);
  }

  protected RegistrationRequestDto rejectRequest(String uuid) throws Exception {

    return requestDecision(uuid, REJECTED);
  }

  private RegistrationRequestDto requestDecision(String uuid, IamRegistrationRequestStatus decision)
      throws Exception {

    // @formatter:off
    String response = mvc
        .perform(post("/registration/{uuid}/{decision}", uuid, decision.name())
          .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    // @formatter:on
    return objectMapper.readValue(response, RegistrationRequestDto.class);
  }
}
