package com.relayr.core.user;

import java.io.Serializable;

public class Relayr_User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String email;

    public Relayr_User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("[\n" +
				"\tid:\t" + getId() + "\n" +
				"\tname:\t" + getName() + "\n" +
				"\temail:\t" + getEmail() + "\n" +
				"]");

		return stringBuilder.toString();
	}

}
