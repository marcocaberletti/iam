package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_SSH_KEY;

import java.util.Collection;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSshKey;

public class SshKeyAddedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 780075417061326415L;

  private final Collection<IamSshKey> sshKeys;

  public SshKeyAddedEvent(Object source, IamAccount account, Collection<IamSshKey> sshKeys) {
    super(source, account, ACCOUNT_ADD_SSH_KEY, buildMessage(ACCOUNT_ADD_SSH_KEY, sshKeys));
    this.sshKeys = sshKeys;
  }

  public Collection<IamSshKey> getSshKeys() {
    return sshKeys;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamSshKey> sshKeys) {
    return String.format("%s: %s", t.getDescription(), sshKeys);
  }
}
