package entities;

import java.sql.Date;
import java.util.Objects;

public class ReponseReclamation {
    private reclamation reclamation;
    private User user;
    private String description;
    private Date closureDate;

    public ReponseReclamation() {
    }

    public ReponseReclamation(reclamation reclamation, User user, String description, Date closureDate) {
        this.reclamation = reclamation;
        this.user = user;
        this.description = description;
        this.closureDate = closureDate;
    }

    public reclamation getClaim() {
        return reclamation;
    }

    public void setClaim(reclamation reclamation) {
        this.reclamation = reclamation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(Date closureDate) {
        this.closureDate = closureDate;
    }

    @Override
    public String toString() {
        return "FieldownerResponse{" +
                "claim=" + reclamation +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", closureDate=" + closureDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReponseReclamation that)) return false;
        return Objects.equals(reclamation, that.reclamation) && Objects.equals(user, that.user) && Objects.equals(description, that.description) && Objects.equals(closureDate, that.closureDate);
    }



}
