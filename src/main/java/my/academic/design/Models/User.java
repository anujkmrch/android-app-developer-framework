package my.academic.design.Models;

/**
 * Created by anuj on 04/04/17.
 */

import java.io.Serializable;

public class User implements Serializable {
    String id;
    String name;
    String username;
    String email;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    String avatar;
    String _type;
    public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(String id, String name, String email, String username,String _type)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.username = username;
        this._type = _type;
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

    public void setType(String _type){ this._type = _type; }

    public String getType(){ return _type; }

}