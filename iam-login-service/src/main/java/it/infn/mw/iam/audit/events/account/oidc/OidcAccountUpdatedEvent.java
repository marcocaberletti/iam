package it.infn.mw.iam.audit.events.account.oidc;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.events.account.AccountUpdatedEvent;
import it.infn.mw.iam.audit.utils.IamOidcSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public abstract class OidcAccountUpdatedEvent extends AccountUpdatedEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  
  @JsonSerialize(using = IamOidcSerializer.class)
  private final Collection<IamOidcId> oidcIds;

  public OidcAccountUpdatedEvent(Object source, IamAccount account, UpdaterType type,
      Collection<IamOidcId> oidcIds, String message) {
    super(source, account, type, message);
    this.oidcIds = oidcIds;
  }

}
