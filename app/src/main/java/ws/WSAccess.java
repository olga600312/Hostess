package ws;

import beans.Login;
import beans.User;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class WSAccess {
    private static final String[] DUMMY_CREDENTIALS = new String[]{
           "1:1:Olga", "olga@example.com:hello:Olga", "bar@example.com:world:Hello, World"
    };

    public User fetchUser(Login login) {
        User user = null;
        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return null;
        }
        for (int i = 0; i < DUMMY_CREDENTIALS.length && user == null; i++) {
            String[] pieces = DUMMY_CREDENTIALS[i].split(":");
            if (pieces[0].equals(login.getUserName()) && pieces[1].equals(login.getPassword())) {
                // Account exists, return true if the password matches.
                user = new User();
                user.setLogin(login);
                user.setName(pieces[2]);
            }
        }

        return user;
    }
}
