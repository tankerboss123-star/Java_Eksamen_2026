package db;

import com.mysql.cj.jdbc.MysqlDataSource;
import dto.PartyItem;
import dto.Person;
import dto.SportItem;
import dto.ToolItem;
import props.RussRentalDBPropertiesProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RussRentalDBService {
    private final MysqlDataSource russRentalDS;

    private static final String ADD_PERSONS = "INSERT INTO Person VALUES (?,?,?,?)";
    private static final String ADD_SPORT_ITEM = "INSERT INTO SportAndLeisureItem (Name, Description, DailyPrice, PersonID, Season, NumberOfUsers) VALUES (?,?,?,?,?,?)";
    private static final String ADD_TOOL_ITEM = "INSERT INTO ToolItem (Name, Description, DailyPrice, PersonID, PowerType, RequiresSafetyGear) VALUES (?,?,?,?,?,?)";
    private static final String ADD_PARTY_ITEM = "INSERT INTO PartyItem (Name, Description, DailyPrice, PersonID, Quantity,isElectronic, RequiresCleaning) VALUES (?,?,?,?,?,?,?)";

    private static final String GET_ALL_SPORTS = "SELECT * FROM SportAndLeisureItem";
    private static final String GET_ALL_TOOL_ITEM = "SELECT * FROM ToolItem";
    private static final String GET_ALL_PARTY_ITEM = "SELECT * FROM PartyItem";


    public RussRentalDBService() {
        russRentalDS = new MysqlDataSource();
        russRentalDS.setServerName(RussRentalDBPropertiesProvider.PROPERTIES.getProperty("host"));
        russRentalDS.setPortNumber(Integer.parseInt(RussRentalDBPropertiesProvider.PROPERTIES.getProperty("port")));
        russRentalDS.setDatabaseName(RussRentalDBPropertiesProvider.PROPERTIES.getProperty("db_name"));
        russRentalDS.setUser(RussRentalDBPropertiesProvider.PROPERTIES.getProperty("uname"));
        russRentalDS.setPassword(RussRentalDBPropertiesProvider.PROPERTIES.getProperty("pwd"));
    }

    public MysqlDataSource getRussRentalDS() {
        return russRentalDS;
    }


    public void addPerson(Person person, Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(ADD_PERSONS)) {
            stmt.setInt(1, person.getId());
            stmt.setString(2, person.getName());
            stmt.setString(3, person.getAddress());
            stmt.setString(4, person.getPhoneNumber());
            stmt.executeUpdate();
        }
    }

    public void addSportItem(SportItem sportItem, Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(ADD_SPORT_ITEM)) {
            stmt.setString(1, sportItem.getName());
            stmt.setString(2, sportItem.getDescription());
            stmt.setInt(3, sportItem.getPricePerDay());
            stmt.setInt(4, sportItem.getOwnerPersonId());
            stmt.setString(5, sportItem.getSeason());
            stmt.setInt(6, sportItem.getNumberOfUsers());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Kunne ikke legge inn sport item");
            }


        }
    }

    public void addToolItem(ToolItem toolItem, Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(ADD_TOOL_ITEM)) {
            stmt.setString(1, toolItem.getName());
            stmt.setString(2, toolItem.getDescription());
            stmt.setInt(3, toolItem.getPricePerDay());
            stmt.setInt(4, toolItem.getOwnerPersonId());
            stmt.setString(5, toolItem.getPowerType());
            stmt.setBoolean(6, toolItem.isRequiresSafetyEquipment());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Kunne ikke legge inn tool item");
            }
        }
    }

    public void addPartyItem(PartyItem partyItem, Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(ADD_PARTY_ITEM)) {
            stmt.setString(1, partyItem.getName());
            stmt.setString(2, partyItem.getDescription());
            stmt.setInt(3, partyItem.getPricePerDay());
            stmt.setInt(4, partyItem.getOwnerPersonId());
            stmt.setInt(5, partyItem.getAmount());
            stmt.setBoolean(6, partyItem.isRequiresElectricity());
            stmt.setBoolean(7, partyItem.isRequiresCleaning());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Kunne ikke legge inn party item");
            }
        }
    }

    public boolean personExists(int id, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Person WHERE PersonID = ?"; // Juster kolonnenavn om nødvendig
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public List<SportItem> getAllSportItems(Connection con) throws SQLException {
        List<SportItem> items = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GET_ALL_SPORTS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SportItem item = new SportItem(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getInt("DailyPrice"),
                        rs.getInt("PersonID"),
                        rs.getString("Season"),
                        rs.getInt("NumberOfUsers")
                );
                items.add(item);
            }
        }
        return items;
    }

    public List<ToolItem> getAllToolItems(Connection con) throws SQLException {
        List<ToolItem> items = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GET_ALL_TOOL_ITEM);
             ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                ToolItem item = new ToolItem(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getInt("DailyPrice"),
                        rs.getInt("PersonID"),
                        rs.getString("PowerType"),
                        rs.getBoolean("RequiresSafetyGear")
                );
                items.add(item);
            }
        }
        return items;
    }

    public List<PartyItem> getAllPartyItems(Connection con) throws SQLException {
        List<PartyItem> items = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GET_ALL_PARTY_ITEM)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PartyItem item = new PartyItem(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getInt("DailyPrice"),
                        rs.getInt("PersonID"),
                        rs.getInt("Quantity"),
                        rs.getBoolean("IsElectronic"),
                        rs.getBoolean("RequiresCleaning")
                );
                items.add(item);
            }
        }
        return items;
    }

    // Sjekker om gjenstanden eksisterer i den valgte tabellen
    public boolean itemExists(String tableName, String idColumn, int itemId, Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Henter RentOutDate for gjenstanden. Returnerer null hvis den ikke er utleid.
    public java.sql.Date getRentOutDate(String tableName, String idColumn, int itemId, Connection con) throws SQLException {
        String sql = "SELECT RentOutDate FROM " + tableName + " WHERE " + idColumn + " = ?"; // Jeg la inn sql strengen her for å skille de ut fra de andre strengene oppe.
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("RentOutDate");
                }
            }
        }
        return null;
    }

    // Oppdaterer RentOutDate til dagens dato (eller NULL ved innlevering senere)
    public void updateRentOutDate(String tableName, String idColumn, int itemId, java.sql.Date date, Connection con) throws SQLException {
        String sql = "UPDATE " + tableName + " SET RentOutDate = ? WHERE " + idColumn + " = ?"; // Jeg la inn sql strengen her for å skille de ut fra de andre strengene oppe.
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, date);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        }
    }

    // jeg legger nå inn tellemetoden (ekstra funksjonalitet for del 2)
    public int getPersonCount(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Person"; // Skriver sql strengen direkte her så jeg slipper å skrive oppe.
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }


}