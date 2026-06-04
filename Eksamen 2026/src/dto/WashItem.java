package dto;

public class WashItem extends RentalItem {

    private int amountOfWashItems;
    private boolean IsOccupied;


    public WashItem(String name, String description, int pricePerDay, int ownerPersonId, int amountOfWashItems, boolean isOccupied) {
        super(name, description, pricePerDay, ownerPersonId, "Wash");
        this.amountOfWashItems = amountOfWashItems;
        IsOccupied = isOccupied;
    }

    public int getAmountOfWashItems() {
        return amountOfWashItems;
    }

    public boolean isOccupied() {
        return IsOccupied;
    }

    @Override
    public String toString() {
        return "WashItem{" +
                "amountOfWashItems=" + amountOfWashItems +
                ", IsOccupied=" + IsOccupied +
                '}';
    }


}
