package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_X509_CERTIFICATE;

import java.util.Collection;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamX509Certificate;

public class X509CertificateRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -7577841399089648131L;

  private final Collection<IamX509Certificate> x509certificates;

  public X509CertificateRemovedEvent(Object source, IamAccount account,
      Collection<IamX509Certificate> x509certificates) {
    super(source, account, ACCOUNT_REMOVE_X509_CERTIFICATE,
        buildMessage(ACCOUNT_REMOVE_X509_CERTIFICATE, x509certificates));
    this.x509certificates = x509certificates;
  }

  public Collection<IamX509Certificate> getX509certificates() {
    return x509certificates;
  }

  protected static String buildMessage(UpdaterType t,
      Collection<IamX509Certificate> x509certificates) {
    return String.format("%s: %s", t.getDescription(), x509certificates);
  }
}
