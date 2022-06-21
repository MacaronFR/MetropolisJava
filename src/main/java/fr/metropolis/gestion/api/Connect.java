package fr.metropolis.gestion.api;

import fr.metropolis.gestion.Utils;
import fr.metropolis.gestion.api.db.Users;

public class Connect {
    static public Users.User connect(String name, String password){
        Users u = new Users();
        Users.User user = u.getByName(name);
        if(user == null){
            return null;
        }
        String hash = Utils.hashPassword(password, user.getSalt());
        if(hash == null){
            return null;
        }
        if(hash.equals(user.getPassword())){
            return user;
        }
        return null;
    }
}
