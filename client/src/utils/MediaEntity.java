/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author umm194
 */
public class MediaEntity {
    private Integer mediaId;
    private String location;
    private Short type;

    public MediaEntity(Integer mediaId, String location, Short type)
    {
        this.mediaId = mediaId;
        this.location = location;
        this.type = type;
    }
    
    /**
     * @return the mediaId
     */
    public Integer getMediaId() {
        return mediaId;
    }

    /**
     * @param mediaId the mediaId to set
     */
    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the type
     */
    public Short getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Short type) {
        this.type = type;
    }
}
