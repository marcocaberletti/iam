package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_GROUP_MEMBERSHIP;

import java.util.Collection;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupMembershipRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -5424437637208297754L;

  private final Collection<IamGroup> groups;

  public GroupMembershipRemovedEvent(Object source, IamAccount account,
      Collection<IamGroup> groups) {
    super(source, account, ACCOUNT_REMOVE_GROUP_MEMBERSHIP,
        buildMessage(ACCOUNT_REMOVE_GROUP_MEMBERSHIP, groups));
    this.groups = groups;
  }

  public Collection<IamGroup> getGroups() {
    return groups;
  }

  protected static String buildMessage(UpdaterType t, Collection<IamGroup> groups) {
    return String.format("%s: %s", t.getDescription(), groups);
  }
}
