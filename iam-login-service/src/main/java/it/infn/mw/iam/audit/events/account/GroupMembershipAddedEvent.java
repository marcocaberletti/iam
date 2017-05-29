package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_GROUP_MEMBERSHIP;

import java.util.Collection;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupMembershipAddedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 473330811526818772L;

  private final Collection<IamGroup> groups;

  public GroupMembershipAddedEvent(Object source, IamAccount account, Collection<IamGroup> groups) {
    super(source, account, ACCOUNT_ADD_GROUP_MEMBERSHIP,
        buildMessage(ACCOUNT_ADD_GROUP_MEMBERSHIP, groups));
    this.groups = groups;
  }

  public Collection<IamGroup> getGroups() {
    return groups;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamGroup> groups) {
    return String.format("%s: %s", t.getDescription(), groups);
  }
}
