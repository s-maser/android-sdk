package com.relayr.core.user;

import java.io.Serializable;

public class Relayr_User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String firstName;
	private String lastName;
	private String email;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
				"\ttitle:\t" + getFirstName() + "\n" +
				"\tmodel:\t" + getLastName().toString() + "\n" +
				"\towner:\t" + getEmail() + "\n" +
				"]");

		return stringBuilder.toString();
	}

}
