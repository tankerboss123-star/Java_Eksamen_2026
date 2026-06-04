package dto;

public class PartyItem extends RentalItem {
    private int amount;
    private boolean requiresElectricity;
    private boolean requiresCleaning;

    public PartyItem(String name, String description, int pricePerDay, int ownerPersonId,
                     int amount, boolean requiresElectricity, boolean requiresCleaning) {
        super(name, description, pricePerDay, ownerPersonId, "PARTY");
        this.amount = amount;
        this.requiresElectricity = requiresElectricity;
        this.requiresCleaning = requiresCleaning;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isRequiresElectricity() {
        return requiresElectricity;
    }

    public boolean isRequiresCleaning() {
        return requiresCleaning;
    }

    @Override
    public String toString() {
        return "PartyItem{" +
                super.toString() +
                ", amount=" + amount +
                ", requiresElectricity=" + requiresElectricity +
                ", requiresCleaning=" + requiresCleaning +
                '}';
    }
}
