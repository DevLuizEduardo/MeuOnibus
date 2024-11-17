package ifs.meuonibus.Models.User;



public enum UserRole {
    ADMIN("admin"),
    COORD("coord"),
    USER("user");


    private String role;
    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }


}
