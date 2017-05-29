package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.utils.IamOidcSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public class OidcAccountRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -7770125927230458782L;

  @JsonSerialize(using = IamOidcSerializer.class)
  private final Collection<IamOidcId> oidcIds;

  public OidcAccountRemovedEvent(Object source, IamAccount account, Collection<IamOidcId> oidcIds) {
    super(source, account, ACCOUNT_REMOVE_OIDC_ID, buildMessage(ACCOUNT_REMOVE_OIDC_ID, oidcIds));
    this.oidcIds = oidcIds;
  }

  public Collection<IamOidcId> getOidcIds() {
    return oidcIds;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamOidcId> oidcIds) {
    return String.format("%s: %s", t.getDescription(), oidcIds);
  }
}
