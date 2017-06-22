package it.infn.mw.iam.core.web;

import java.util.ArrayList;
import java.util.Map;

import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

@RestController
public class IamJwkSetPublishingEndpoint {
  public static final String URL = "/jwk";
  public static final String APPLICATION_JWK_JSON = "application/jwk+json";

  @Autowired
  private JWTSigningAndValidationService jwtService;

  @RequestMapping(value = URL, produces = APPLICATION_JWK_JSON)
  public String getJwk() {

    Map<String, JWK> keys = jwtService.getAllPublicKeys();
    JWKSet jwkSet = new JWKSet(new ArrayList<>(keys.values()));

    return jwkSet.toString();
  }
}
