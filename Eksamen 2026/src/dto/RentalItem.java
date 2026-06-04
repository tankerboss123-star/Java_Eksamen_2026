package dto;

public abstract class RentalItem {
    private String name;
    private String description;
    private int pricePerDay;
    private int ownerPersonId;
    private String type;

    public RentalItem(String name, String description, int pricePerDay, int ownerPersonId, String type) {
        this.name = name;
        this.description = description;
        this.pricePerDay = pricePerDay;
        this.ownerPersonId = ownerPersonId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPricePerDay() {
        return pricePerDay;
    }

    public int getOwnerPersonId() {
        return ownerPersonId;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", ownerPersonId=" + ownerPersonId +
                ", type='" + type + '\'';
    }
}
