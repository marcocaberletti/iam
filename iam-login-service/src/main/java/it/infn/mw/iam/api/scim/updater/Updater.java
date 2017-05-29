package it.infn.mw.iam.api.scim.updater;

/**
 * And updater attempts to update something, and returns true if that something was actually updated
 *
 */
public interface Updater<T> {

  /**
   * The updater update logic
   * 
   * @return
   *         <ul>
   *         <li><code>true</code>, if the object was modified by the update</li>
   *         <li><code>false</code>, otherwise</li>
   *         </ul>
   */
  boolean update();

  /**
   * The updater type
   *
   * @return the updater type (see {@link UpdaterType})
   */
  UpdaterType getType();

  /**
   * The new value set by this updater
   * 
   * @return the new value
   */
  T getNewValue();
}
