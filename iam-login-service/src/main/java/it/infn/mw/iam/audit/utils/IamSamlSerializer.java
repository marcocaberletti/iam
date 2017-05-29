package it.infn.mw.iam.audit.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class IamSamlSerializer extends JsonSerializer<IamSamlId> {

  @Override
  public void serialize(IamSamlId value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    gen.writeStartObject();
    gen.writeStringField("idpid", value.getIdpId());
    gen.writeStringField("userid", value.getUserId());
    gen.writeEndObject();
  }

}
