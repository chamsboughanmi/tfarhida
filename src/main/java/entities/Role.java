package entities;

public enum Role {
    client,
    proprietaire;


    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.toString().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }


}

