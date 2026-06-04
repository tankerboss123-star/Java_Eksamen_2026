package dto;

public class SportItem extends RentalItem {
    private String season;
    private int numberOfUsers;

    public SportItem(String name, String description, int pricePerDay, int ownerPersonId,
                     String season, int numberOfUsers) {
        super(name, description, pricePerDay, ownerPersonId, "SPORT");
        this.season = season;
        this.numberOfUsers = numberOfUsers;
    }

    public String getSeason() {
        return season;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    @Override
    public String toString() {
        return "SportItem{" +
                super.toString() +
                ", season='" + season + '\'' +
                ", numberOfUsers=" + numberOfUsers +
                '}';
    }
}
