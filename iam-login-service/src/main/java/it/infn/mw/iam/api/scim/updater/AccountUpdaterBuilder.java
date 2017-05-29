package it.infn.mw.iam.api.scim.updater;

import it.infn.mw.iam.audit.events.account.AccountEvent;

@FunctionalInterface
public interface AccountUpdaterBuilder<T, E extends AccountEvent> {
  AccountUpdater<T, E> build(T value);
}
