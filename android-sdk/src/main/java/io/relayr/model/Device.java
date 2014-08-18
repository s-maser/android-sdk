package io.relayr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** The Device class is a representation of the device entity.
 * A device entity is any external entity capable of gathering measurements
 * or one which is capable of receiving information from the relayr platform.
 * Examples would be a thermometer, a gyroscope or an infrared sensor.
 * */
public class Device implements Serializable {

	/** Auto generated uid */
	private static final long serialVersionUID = 1L;
	public final String id;
	private String name;
	private final Model model;
	private String owner;
    private String firmwareVersion;
    private final String secret;
    @SerializedName("public") private boolean isPublic;

    public Device(String id, String name, Model model, String owner,
                  String firmwareVersion, String secret, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.owner = owner;
        this.firmwareVersion = firmwareVersion;
        this.secret = secret;
        this.isPublic = isPublic;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Model getModel() {
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
