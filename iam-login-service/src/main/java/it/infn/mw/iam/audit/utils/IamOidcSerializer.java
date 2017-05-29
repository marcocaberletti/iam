package it.infn.mw.iam.audit.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamOidcId;

public class IamOidcSerializer extends JsonSerializer<IamOidcId> {

  @Override
  public void serialize(IamOidcId value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("issuer", value.getIssuer());
    gen.writeStringField("subject", value.getSubject());
    gen.writeEndObject();
  }

}
