package it.infn.mw.iam.api.scim.updater;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import it.infn.mw.iam.api.scim.updater.util.AccountEventBuilder;
import it.infn.mw.iam.audit.events.account.AccountEvent;
import it.infn.mw.iam.persistence.model.IamAccount;

public class DefaultAccountUpdater<T, E extends AccountEvent> extends DefaultUpdater<T>
    implements AccountUpdater<T, E> {

  private final IamAccount account;
  private final AccountEventBuilder<T, E> eventBuilder;

  public DefaultAccountUpdater(IamAccount account, UpdaterType type, Supplier<T> supplier,
      Consumer<T> consumer, T newVal, AccountEventBuilder<T, E> eventBuilder) {
    super(type, supplier, consumer, newVal);
    this.account = account;
    this.eventBuilder = eventBuilder;
  }

  public DefaultAccountUpdater(IamAccount account, UpdaterType type, Consumer<T> consumer, T newVal,
      Predicate<T> predicate, AccountEventBuilder<T, E> eventBuilder) {
    super(type, consumer, newVal, predicate);
    this.account = account;
    this.eventBuilder = eventBuilder;
  }

  @Override
  public IamAccount getAccount() {
    return this.account;
  }

  @Override
  public E buildEvent(Object source) {

    return eventBuilder.buildAccountEvent(source, account, newValue);
  }


}
