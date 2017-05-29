package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SAML_ID;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.utils.IamSamlSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class SamlAccountRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -989802712962338269L;

  @JsonSerialize(using = IamSamlSerializer.class)
  private Collection<IamSamlId> samlIds;

  public SamlAccountRemovedEvent(Object source, IamAccount account, Collection<IamSamlId> samlIds) {
    super(source, account, ACCOUNT_REMOVE_SAML_ID, buildMessage(ACCOUNT_REMOVE_SAML_ID, samlIds));
    this.samlIds = samlIds;
  }

  public Collection<IamSamlId> getSamlIds() {
    return samlIds;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamSamlId> samlIds) {
    return String.format("%s: %s", t.getDescription(), samlIds);
  }
}
