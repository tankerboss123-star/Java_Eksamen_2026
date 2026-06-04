import db.RussRentalDBService;
import dto.PartyItem;
import dto.Person;
import dto.SportItem;
import dto.ToolItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class ReadFromFile {
    private RussRentalDBService russRentalDBService;

    public ReadFromFile() {
        this.russRentalDBService = new RussRentalDBService();
    }

    public void readFile(Scanner input) throws SQLException, FileNotFoundException {
        System.out.println("Skriv inn navn på filen, formatet på å skrive filnavn er slik: ");
        System.out.println("Eksempel: 'src//filnavn.txt' ");
        String filename = input.nextLine();
        File file = new File(filename);

        // try-with-resources sikrer at både filen og databasetilkoblingen lukkes automatisk
        try (Scanner scanner = new Scanner(file);
             Connection con = russRentalDBService.getRussRentalDS().getConnection()) {

            con.setAutoCommit(false);

            while (scanner.hasNextLine()) {
                controlPersons(scanner, con);
                controlRentalItems(scanner, con);
            }

            con.commit();

        } catch (FileNotFoundException | SQLException | IllegalArgumentException e) {
            // Hvis det oppstår formatfeil (som 15O), vil lukkingen av Connection
            // automatisk føre til en rollback i MySQL fordi con.commit() aldri ble nådd.
            System.err.println("Feil under prosessering av filen! Endringer rulles tilbake. Melding: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void controlRentalItems(Scanner scanner, Connection con) throws SQLException {
        int amountOfItems = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amountOfItems; i++) {
            String name = scanner.nextLine();
            String description = scanner.nextLine();

            // Les prisen som tekst først
            String priceLine = scanner.nextLine();
            int pricePerDay;

            try {
                // Sjekk om det er et gyldig tall (Kaster feil på "15O")
                pricePerDay = Integer.parseInt(priceLine.trim());
            } catch (NumberFormatException e) {
                // Stopper alt, retter ingenting, og ber brukeren til å fikse .txt-filen
                throw new IllegalArgumentException("Formatfeil i tekstfilen under innlesing av utleieobjektet '" + name + "'. " +
                        "Pris per dag må være et rent tall, men fant verdien: '" + priceLine + "'. " +
                        "Vennligst åpne tekstfilen, rett opp feilen og kjør programmet på nytt.", e);
            }

            int ownerPersonId = Integer.parseInt(scanner.nextLine());
            String type = scanner.nextLine();

            switch (type) {
                case "SPORT" -> {
                    String season = scanner.nextLine();
                    int numberOfUsers = Integer.parseInt(scanner.nextLine());
                    SportItem sportItem = new SportItem(name, description, pricePerDay, ownerPersonId, season, numberOfUsers);
                    System.out.println(sportItem);
                    russRentalDBService.addSportItem(sportItem, con);
                }
                case "TOOL" -> {
                    String powerType = scanner.nextLine();
                    boolean requiresSafetyEquipment = Boolean.parseBoolean(scanner.nextLine());
                    ToolItem toolItem = new ToolItem(name, description, pricePerDay, ownerPersonId, powerType, requiresSafetyEquipment);
                    System.out.println(toolItem);
                    russRentalDBService.addToolItem(toolItem, con);
                }
                case "PARTY" -> {
                    int amount = Integer.parseInt(scanner.nextLine());
                    boolean requiresElectricity = Boolean.parseBoolean(scanner.nextLine());
                    boolean requiresCleaning = Boolean.parseBoolean(scanner.nextLine());
                    PartyItem partyItem = new PartyItem(name, description, pricePerDay, ownerPersonId, amount, requiresElectricity, requiresCleaning);
                    System.out.println(partyItem);
                    russRentalDBService.addPartyItem(partyItem, con);
                }
                default -> System.out.println("Legg inn riktig type " + type);
            }
            scanner.nextLine(); // Hopper over --
        }
    }

    public void controlPersons(Scanner scanner, Connection con) throws SQLException {
        int amountOfPersons = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < amountOfPersons; i++) {
            int id = Integer.parseInt(scanner.nextLine());
            String name = scanner.nextLine();
            String address = scanner.nextLine();
            String phoneNumber = scanner.nextLine();

            // Sjekker her om da personen allerede er registrert i databasen fra en tidligere fil
            if (!russRentalDBService.personExists(id, con)) {
                Person person = new Person(id, name, address, phoneNumber);
                System.out.println("Registrerer ny person: " + person);
                russRentalDBService.addPerson(person, con);
            } else {
                System.out.println("Personen " + name + " (ID: " + id + ") finnes allerede. Hopper over.");
            }

            scanner.nextLine(); // Hopper over --
        }
    }


}
