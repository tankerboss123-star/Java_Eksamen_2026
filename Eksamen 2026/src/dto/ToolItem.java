package dto;

public class ToolItem extends RentalItem {
    private String powerType;
    private boolean requiresSafetyEquipment;

    public ToolItem(String name, String description, int pricePerDay, int ownerPersonId,
                    String powerType, boolean requiresSafetyEquipment) {
        super(name, description, pricePerDay, ownerPersonId, "TOOL");
        this.powerType = powerType;
        this.requiresSafetyEquipment = requiresSafetyEquipment;
    }

    public String getPowerType() {
        return powerType;
    }

    public boolean isRequiresSafetyEquipment() {
        return requiresSafetyEquipment;
    }

    @Override
    public String toString() {
        return "ToolItem{" +
                super.toString() +
                ", powerType='" + powerType + '\'' +
                ", requiresSafetyEquipment=" + requiresSafetyEquipment +
                '}';
    }
}
