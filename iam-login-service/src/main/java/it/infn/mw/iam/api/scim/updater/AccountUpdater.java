package it.infn.mw.iam.api.scim.updater;

import it.infn.mw.iam.audit.events.account.AccountEvent;
import it.infn.mw.iam.persistence.model.IamAccount;

public interface AccountUpdater<T, E extends AccountEvent> extends Updater<T> {

  public IamAccount getAccount();

  public E buildEvent(Object source);
}
