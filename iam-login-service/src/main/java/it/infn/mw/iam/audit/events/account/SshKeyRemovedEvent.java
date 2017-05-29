package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SSH_KEY;

import java.util.Collection;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSshKey;

public class SshKeyRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 5329049223148007948L;

  private final Collection<IamSshKey> sshKeys;

  public SshKeyRemovedEvent(Object source, IamAccount account, Collection<IamSshKey> sshKeys) {
    super(source, account, ACCOUNT_REMOVE_SSH_KEY, buildMessage(ACCOUNT_REMOVE_SSH_KEY, sshKeys));
    this.sshKeys = sshKeys;
  }

  public Collection<IamSshKey> getSshKeys() {
    return sshKeys;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamSshKey> sshKeys) {
    return String.format("%s: %s", t.getDescription(), sshKeys);
  }
}
