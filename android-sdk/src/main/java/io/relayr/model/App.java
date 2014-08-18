package io.relayr.model;

/** An app is a basic entity in the relayr platform. The relayr platform relates to apps in two
 * manners: Publisher Apps and User Apps.
 * Publisher apps are apps which are purchasable on an app store and are owned by a publisher.
 * User apps are apps which have been approved to the data of an end user. This approval has been
 * granted by the user.
 * relayr deploys the OAuth 2.0 standard for third party applications authorization.
 * To read about this component please visit our
 * @see <a href="https://developer.relayr.io/documents/Authorization/OAuthAndRelayr">Authorization
 * reference</a>
 */
public class App {
    public final String id;
    public final String name;
    public final String description;

    /**
     * Construction Method for an app
     * @param id the relayr assigned id for the app instance
     * @param name The name of this app
     * @param description a brief description of the app
     */
    public App(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
