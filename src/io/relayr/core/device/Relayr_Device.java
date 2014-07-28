package io.relayr.core.device;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Relayr_Device implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String id;
	private String name;
	private final Relayr_DeviceModel model;
	private String owner;
    private String firmwareVersion;
    private final String secret;
    @SerializedName("public") private boolean isPublic;

    public Relayr_Device(String id, String name, Relayr_DeviceModel model, String owner,
                         String firmwareVersion, String secret, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.owner = owner;
        this.firmwareVersion = firmwareVersion;
        this.secret = secret;
        this.isPublic = isPublic;
    }

    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Relayr_DeviceModel getModel() {
		return model;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

    public String getSecret() {
        return secret;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @Override
    public String toString() {
        return "Relayr_Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", model=" + model +
                ", owner='" + owner + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", secret='" + secret + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}
