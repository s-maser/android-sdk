package io.relayr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** The BookmarkDevice class is a representation of public device that user bookmarked. */
public class BookmarkDevice implements Serializable {

	/** Auto generated uid */
	private static final long serialVersionUID = 1L;
	public final String id;
	private String name;
	private final String model;
	private String owner;
    private String firmwareVersion;
    private final String secret;
    @SerializedName("public") private boolean isPublic;

    public BookmarkDevice(String id, String name, String model, String owner,
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

	public String getModel() {
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
