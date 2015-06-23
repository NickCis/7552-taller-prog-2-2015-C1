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
    private String username;

    public LoginEntity(Integer loginId, String username)
    {
        this.loginId = loginId;
        this.username = username;
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
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof LoginEntity))
            return false;
        LoginEntity other = (LoginEntity) obj;
        if(!this.loginId.equals(other.loginId)) return false;
        if(!this.username.equals(other.username)) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.loginId != null ? this.loginId.hashCode() : 0);
        hash = 41 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }
    
    
}
