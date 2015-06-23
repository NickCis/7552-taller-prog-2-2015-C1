/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author umm194
 */
public class LoginEntity {
    private Integer loginId;
    private String accessToken;

    public LoginEntity(Integer loginId, String accessToken)
    {
        this.loginId = loginId;
        this.accessToken = accessToken;
    }
    
    /**
     * @return the loginId
     */
    public Integer getLoginId() {
        return this.loginId;
    }

    /**
     * @param loginId the mediaId to set
     */
    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the username to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof LoginEntity))
            return false;
        LoginEntity other = (LoginEntity) obj;
        if(!this.loginId.equals(other.loginId)) return false;
        if(!this.accessToken.equals(other.accessToken)) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.loginId != null ? this.loginId.hashCode() : 0);
        hash = 41 * hash + (this.accessToken != null ? this.accessToken.hashCode() : 0);
        return hash;
    }
    
    
}
