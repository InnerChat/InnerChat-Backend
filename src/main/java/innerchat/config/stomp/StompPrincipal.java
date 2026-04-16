package innerchat.config.stomp;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final String name; //userId

    public StompPrincipal(Long userId) {
        this.name = userId.toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
