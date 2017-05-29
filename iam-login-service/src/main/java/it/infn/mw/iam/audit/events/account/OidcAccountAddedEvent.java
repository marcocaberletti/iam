package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_OIDC_ID;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.utils.IamOidcSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public class OidcAccountAddedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -2661464873840583857L;

  @JsonSerialize(using = IamOidcSerializer.class)
  private final Collection<IamOidcId> oidcIds;

  public OidcAccountAddedEvent(Object source, IamAccount account, Collection<IamOidcId> oidcIds) {
    super(source, account, ACCOUNT_ADD_OIDC_ID, buildMessage(ACCOUNT_ADD_OIDC_ID, oidcIds));
    this.oidcIds = oidcIds;
  }

  public Collection<IamOidcId> getOidcIds() {
    return oidcIds;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamOidcId> oidcIds) {
    return String.format("%s: %s", t.getDescription(), oidcIds);
  }
}
