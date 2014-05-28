package com.relayr.core.device;

public class Relayr_Device {

	private String id;
	private String title;
	private Relayr_DeviceModel model;
	private String owner;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Relayr_DeviceModel getModel() {
		return model;
	}

	public void setModel(Relayr_DeviceModel model) {
		this.model = model;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		String message = new String();

		message += 	"[\n" +
						"\tid:\t" + getId() + "\n" +
						"\ttitle:\t" + getTitle() + "\n" +
						"\tmodel:\t" + getModel().toString() + "\n" +
						"\towner:\t" + getOwner() + "\n" +
					"]";

		return message;
	}
}
