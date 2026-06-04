import db.RussRentalDBService;
import dto.PartyItem;
import dto.SportItem;
import dto.ToolItem;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Program {

    private boolean running = true;
    private RussRentalDBService russRentalDBService = new RussRentalDBService();

    public void run() {
        Scanner input = new Scanner(System.in);
        while (running) {
            inputText();
            int choice = Integer.parseInt(input.nextLine());
            meny(choice, input);
        }
    }

    public void inputText() {
        System.out.println("\n--- RussRental Meny ---");
        System.out.println("1: Registrere personer og gjenstander");
        System.out.println("2: Se informasjon om alle utleiegjenstander");
        System.out.println("3: Registrere utleie av en gjenstand");
        System.out.println("4: Registrere innlevering av en gjenstand");
        System.out.println("5: Se antall registrerte personer");
        System.out.println("6: Avslutt");
        System.out.print("Velg et alternativ: ");
    }

    public void meny(int choice, Scanner input) {
        switch (choice) {
            case 1 -> registrerePersonerOgGjenstander(input);
            case 2 -> seInformasjonOmAlleUtleieGjenstander(input);
            case 3 -> RegistrereUtleieAvEnGjenstand(input);
            case 4 -> RegistrereInnleveringAvEnGjenstand(input);
            case 5 -> seAntallRegistrertePersoner();
            case 6 -> avslutt();
            default -> System.out.println("Ugyldig valg, prøv igjen.");
        }
    }

    private void registrerePersonerOgGjenstander(Scanner input) {
        ReadFromFile rfd = new ReadFromFile();
        try {
            rfd.readFile(input);
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void seInformasjonOmAlleUtleieGjenstander(Scanner input) {
        System.out.println("\n==================================================");
        System.out.println("       OVERSIKT OVER ALLE UTLEIEGJENSTANDER       "); // Valgte å style litt så det ser fint ut.
        System.out.println("==================================================");

        // Bruker try-with-resources for å lukke koblingen automatisk etter bruk
        try (Connection con = russRentalDBService.getRussRentalDS().getConnection()) {

            System.out.println("\nSPORT OG FRITID:");
            List<SportItem> sportItems = russRentalDBService.getAllSportItems(con);
            if (sportItems.isEmpty()) {
                System.out.println("   Ingen registrerte gjenstander.");
            } else {
                sportItems.forEach(item -> System.out.println("   - " + item));
            }

            System.out.println("\nVERKTØY:");
            List<ToolItem> toolItems = russRentalDBService.getAllToolItems(con);
            if (toolItems.isEmpty()) {
                System.out.println("   Ingen registrerte gjenstander.");
            } else {
                toolItems.forEach(item -> System.out.println("   - " + item));
            }

            System.out.println("\nPARTYUTSTYR:");
            List<PartyItem> partyItems = russRentalDBService.getAllPartyItems(con);
            if (partyItems.isEmpty()) {
                System.out.println("   Ingen registrerte gjenstander.");
            } else {
                partyItems.forEach(item -> System.out.println("   - " + item));
            }

            System.out.println("\n==================================================");

        } catch (SQLException e) {
            System.err.println("Kunne ikke hente utleiegjenstander fra databasen: " + e.getMessage());
        }
    }

    private void RegistrereUtleieAvEnGjenstand(Scanner input) {
        System.out.println("\n--- REGISTRERE UTLEIE ---");
        System.out.println("Velg kategori:\n1: Sport og Fritid\n2: Verktøy\n3: Partyutstyr");
        System.out.print("Valg: ");
        int kategoriValg = Integer.parseInt(input.nextLine());

        // Setter tabellnavn dynamisk basert på menyvalget over
        String tableName = switch (kategoriValg) {
            case 1 -> "SportAndLeisureItem";
            case 2 -> "ToolItem";
            case 3 -> "PartyItem";
            default -> throw new IllegalArgumentException("Ugyldig kategori valgt: " + kategoriValg);
        };

        String idColumn = "ItemID";
        System.out.print("Tast inn ID på gjenstanden: ");
        int itemId = Integer.parseInt(input.nextLine());

        try (Connection con = russRentalDBService.getRussRentalDS().getConnection()) {
            con.setAutoCommit(false); // Slår av autocommit for å ha kontroll på transaksjonen

            // Sjekker om ID-en finnes i tabellen før vi gjør noe mer
            if (!russRentalDBService.itemExists(tableName, idColumn, itemId, con)) {
                System.out.println("Feil: Gjenstanden ble ikke funnet i databasen.");
                return;
            }

            java.sql.Date rentOutDate = russRentalDBService.getRentOutDate(tableName, idColumn, itemId, con);

            // Sjekker om gjenstanden allerede har en registrert utleiedato
            if (rentOutDate != null) {
                System.out.println("Advarsel: Gjenstanden er allerede utleid! (Utleid siden: " + rentOutDate + ")");
            } else {
                // Genererer dagens dato og lagrer den i databasen
                java.sql.Date dagensDato = new java.sql.Date(System.currentTimeMillis());
                russRentalDBService.updateRentOutDate(tableName, idColumn, itemId, dagensDato, con);

                con.commit(); // Lagrer endringene permanent hvis alt gikk bra
                System.out.println("Suksess: Gjenstanden med ID " + itemId + " er nå registrert som utleid i dag (" + dagensDato + ").");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Feil: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Databasefeil under utleie: " + e.getMessage());
        }
    }

    private void RegistrereInnleveringAvEnGjenstand(Scanner input) {
        System.out.println("\n--- REGISTRERE INNLEVERING ---");
        System.out.println("Velg kategori:\n1: Sport og Fritid\n2: Verktøy\n3: Partyutstyr");
        System.out.print("Valg: ");
        int kategoriValg = Integer.parseInt(input.nextLine());

        String tableName = switch (kategoriValg) {
            case 1 -> "SportAndLeisureItem";
            case 2 -> "ToolItem";
            case 3 -> "PartyItem";
            default -> throw new IllegalArgumentException("Ugyldig kategori valgt: " + kategoriValg);
        };

        String idColumn = "ItemID";
        System.out.print("Tast inn ID på gjenstanden som leveres inn: ");
        int itemId = Integer.parseInt(input.nextLine());

        try (Connection con = russRentalDBService.getRussRentalDS().getConnection()) {
            con.setAutoCommit(false);

            // Verifiserer at gjenstanden faktisk finnes i valgt kategori
            if (!russRentalDBService.itemExists(tableName, idColumn, itemId, con)) {
                System.out.println("Feil: Gjenstanden ble ikke funnet i databasen.");
                return;
            }

            java.sql.Date rentOutDate = russRentalDBService.getRentOutDate(tableName, idColumn, itemId, con);

            // Kan bare levere inn gjenstander som faktisk er registrert som utleid (har en dato)
            if (rentOutDate == null) {
                System.out.println("Advarsel: Denne gjenstanden er ikke registrert som utleid i systemet, og kan ikke leveres inn.");
            } else {
                // Setter utleiedatoen til null for å markere at den er ledig igjen
                russRentalDBService.updateRentOutDate(tableName, idColumn, itemId, null, con);

                con.commit();
                System.out.println("Suksess: Gjenstanden var levert inn og RentOutDate er satt til null.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Feil: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Databasefeil under innlevering: " + e.getMessage());
        }
    }

    private void seAntallRegistrertePersoner(){
        try (Connection con = russRentalDBService.getRussRentalDS().getConnection()){
            int antall = russRentalDBService.getPersonCount(con);
            System.out.println("Antall personer registrert: " + antall + " i databasen.");
        } catch (SQLException e) {
            System.err.println("Databasefeil! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    private void avslutt() {
        System.out.println("Avslutter...");
        running = false;
    }
}
