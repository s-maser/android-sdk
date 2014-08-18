package io.relayr.model;

import java.io.Serializable;
/** The first basic entity in the relayr platform is the user.
 * Every user registers with an email address,
 * a respective name and password and is assigned a unique userId.
 * A user can be both an application owner (a publisher) and an end user.
 * A user is required in order to add other entities to the relayr platform. */
public class User implements Serializable {

	/** Auto generated uid */
	private static final long serialVersionUID = 1L;

	public final String id;
	private String name;
    public final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + "\'" +
                ", name='" + name + "\'" +
                ", email='" + email + "\'" +
                "}";
    }
}
