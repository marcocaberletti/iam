package it.infn.mw.iam.test.core.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.core.web.IamJwkSetPublishingEndpoint;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class JwkEndpointTests {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private final String URL = IamJwkSetPublishingEndpoint.URL;
  private final String CONTENT_TYPE = IamJwkSetPublishingEndpoint.APPLICATION_JWK_JSON;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @Test
  public void testJwkEndpoint() throws Exception {
    // @formatter:off
    mvc.perform(get(URL))
      .andExpect(status().isOk())
      .andExpect(content().contentType(CONTENT_TYPE))
      .andExpect(jsonPath("$.keys").exists())
      .andExpect(jsonPath("$.keys").isArray())
      .andExpect(jsonPath("$.keys").isNotEmpty())
      ;
    // @formatter:on
  }

}
