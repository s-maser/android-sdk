package io.relayr.core.model;

import java.io.Serializable;

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
