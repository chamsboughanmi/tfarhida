package entities;

import java.sql.Date;
import java.util.Objects;

public class reclamation {

    private int id;
    private User user;
    private String description;
    private Date date;
    private String status;
    private String type;
    private String image ;
    private String satisfaction;


    public reclamation() {
    }

    public reclamation(User user, String description, Date date, String status, String type, String image, String satisfaction) {
        this.user = user;
        this.description = description;
        this.date = date;
        this.status = status;
        this.type = type;
        this.image = image;
        this.satisfaction = satisfaction;
    }

    public reclamation(int id, User user, String description, Date date, String status, String type, String image, String satisfaction) {
        this.id = id;
        this.user = user;
        this.description = description;
        this.date = date;
        this.status = status;
        this.type = type;
        this.image = image;
        this.satisfaction = satisfaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", satisfaction=" + satisfaction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof reclamation reclamation)) return false;
        return id == reclamation.id && satisfaction == reclamation.satisfaction && Objects.equals(user, reclamation.user) && Objects.equals(description, reclamation.description) && Objects.equals(date, reclamation.date) && Objects.equals(status, reclamation.status) && Objects.equals(type, reclamation.type) && Objects.equals(image, reclamation.image);
    }

}
