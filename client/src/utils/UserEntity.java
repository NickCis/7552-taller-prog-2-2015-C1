/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author umm194
 */
public class UserEntity {
    private Integer userId;
    private Integer phone;
    private String name;
    private Short status;

    public UserEntity(Integer userId, String name, Integer phone, Short status)
    {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }
    
    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the phone
     */
    public Integer getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    public Short getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Short status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof UserEntity))
            return false;

        UserEntity other = (UserEntity) obj;
        if(!this.userId.equals(other.userId)) return false;
        if(!this.name.equals(other.name)) return false;
        if(!this.phone.equals(other.phone)) return false;
        if(!this.status.equals(other.status)) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        return hash;
    }
}
