import java.util.*;

public class JavaTriageApp {

    static class Patient implements Comparable<Patient> {
        private String name;
        private int severity;
        private String condition;
        private int arrivalOrder;

        public Patient(String name, int severity, String condition, int arrivalOrder) {
            this.name = name;
            this.severity = severity;
            this.condition = condition;
            this.arrivalOrder = arrivalOrder;
        }

        public String getName() {
            return name;
        }

        public int getSeverity() {
            return severity;
        }

        public String getCondition() {
            return condition;
        }

        public int getArrivalOrder() {
            return arrivalOrder;
        }

        @Override
        public int compareTo(Patient other) {
            if (this.severity != other.severity) {
                return this.severity - other.severity;
            }
            return this.arrivalOrder - other.arrivalOrder;
        }

        @Override
        public String toString() {
            return name + " | severity=" + severity + " | condition=" + condition;
        }
    }

    static class FieldHospital {
        private PriorityQueue<Patient> queue;
        private int arrivalCounter;
        private final int maxCapacity;
        private final int helicopterCapacity;
        private final int helicopterIntervalHours;
        private int currentHour;
        private int nextHelicopterHour;

        public FieldHospital() {
            queue = new PriorityQueue<>();
            arrivalCounter = 0;
            maxCapacity = 200;
            helicopterCapacity = 5;
            helicopterIntervalHours = 12;
            currentHour = 0;
            nextHelicopterHour = helicopterIntervalHours;
        }

        public boolean addPatient(String name, int severity, String condition) {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            if (severity < 1 || severity > 10) {
                return false;
            }
            if (queue.size() >= maxCapacity) {
                return false;
            }

            queue.add(new Patient(name.trim(), severity, condition == null ? "" : condition.trim(), arrivalCounter++));
            return true;
        }

        public Patient viewNextPatient() {
            return queue.peek();
        }

        public Patient treatNextPatient() {
            return queue.poll();
        }

        public List<Patient> getAllPatients() {
            List<Patient> list = new ArrayList<>(queue);
            Collections.sort(list);
            return list;
        }

        public int getCurrentHour() {
            return currentHour;
        }

        public int getNextHelicopterHour() {
            return nextHelicopterHour;
        }

        public int getCapacityUsed() {
            return queue.size();
        }

        public int getCapacityRemaining() {
            return maxCapacity - queue.size();
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public int getHelicopterCapacity() {
            return helicopterCapacity;
        }

        public int getHelicopterIntervalHours() {
            return helicopterIntervalHours;
        }

        public void advanceHours(int hours) {
            if (hours > 0) {
                currentHour += hours;
            }
        }

        public boolean isHelicopterHere() {
            return currentHour >= nextHelicopterHour;
        }

        public List<Patient> helicopterPickup() {
            List<Patient> pickedUp = new ArrayList<>();

            if (!isHelicopterHere()) {
                return pickedUp;
            }

            int count = 0;
            while (!queue.isEmpty() && count < helicopterCapacity) {
                pickedUp.add(queue.poll());
                count++;
            }

            nextHelicopterHour += helicopterIntervalHours;
            return pickedUp;
        }
    }

    static int passed = 0;
    static int failed = 0;

    public static void runTests() {
        FieldHospital h1 = new FieldHospital();
        assertTest("Empty view returns null", h1.viewNextPatient() == null);
        assertTest("Empty treat returns null", h1.treatNextPatient() == null);

        FieldHospital h2 = new FieldHospital();
        h2.addPatient("Alice", 8, "bite");
        h2.addPatient("Ben", 2, "sprained ankle");
        h2.addPatient("Cara", 5, "fever");

        Patient next = h2.viewNextPatient();
        assertTest("View shows least severe first", next != null && next.getName().equals("Ben"));

        Patient treated1 = h2.treatNextPatient();
        assertTest("Treat removes least severe first", treated1 != null && treated1.getName().equals("Ben"));

        Patient treated2 = h2.treatNextPatient();
        assertTest("Second treat gets next priority", treated2 != null && treated2.getName().equals("Cara"));

        FieldHospital h3 = new FieldHospital();
        h3.addPatient("Alex", 3, "cut");
        h3.addPatient("Brooke", 3, "bruise");
        Patient tieWinner = h3.treatNextPatient();
        assertTest("Tie uses arrival order", tieWinner != null && tieWinner.getName().equals("Alex"));

        FieldHospital h4 = new FieldHospital();
        boolean addedAll = true;
        for (int i = 1; i <= 200; i++) {
            if (!h4.addPatient("Patient" + i, 5, "stable")) {
                addedAll = false;
                break;
            }
        }
        boolean added201st = h4.addPatient("Patient201", 5, "stable");
        assertTest("Hospital accepts up to 200 patients", addedAll && h4.getCapacityUsed() == 200);
        assertTest("Hospital rejects 201st patient", !added201st);

        FieldHospital h5 = new FieldHospital();
        assertTest("Blank name rejected", !h5.addPatient("   ", 5, "cold"));
        assertTest("Severity too low rejected", !h5.addPatient("Jake", 0, "cold"));
        assertTest("Severity too high rejected", !h5.addPatient("Lena", 11, "cold"));

        FieldHospital h6 = new FieldHospital();
        h6.addPatient("Mia", 7, "injury");
        h6.addPatient("Noah", 2, "fever");
        h6.addPatient("Owen", 5, "cut");
        List<Patient> patients = h6.getAllPatients();
        boolean sortedCorrectly =
                patients.size() == 3 &&
                        patients.get(0).getName().equals("Noah") &&
                        patients.get(1).getName().equals("Owen") &&
                        patients.get(2).getName().equals("Mia");
        assertTest("Show all returns sorted list", sortedCorrectly);

        FieldHospital h7 = new FieldHospital();
        h7.addPatient("Rick", 4, "scratch");
        h7.addPatient("Tara", 2, "burn");
        h7.advanceHours(11);
        assertTest("Helicopter not here before 12 hours", !h7.isHelicopterHere());
        h7.advanceHours(1);
        assertTest("Helicopter arrives at 12 hours", h7.isHelicopterHere());
        List<Patient> pickedUp = h7.helicopterPickup();
        boolean pickupCorrect =
                pickedUp.size() == 2 &&
                        pickedUp.get(0).getName().equals("Tara") &&
                        pickedUp.get(1).getName().equals("Rick");
        assertTest("Helicopter pickup uses least severity first", pickupCorrect);

        FieldHospital h8 = new FieldHospital();
        h8.addPatient("P1", 10, "condition");
        h8.addPatient("P2", 9, "condition");
        h8.addPatient("P3", 8, "condition");
        h8.addPatient("P4", 7, "condition");
        h8.addPatient("P5", 6, "condition");
        h8.addPatient("P6", 1, "condition");
        h8.addPatient("P7", 2, "condition");
        h8.advanceHours(12);
        List<Patient> pickup5 = h8.helicopterPickup();
        boolean fiveOnly =
                pickup5.size() == 5 &&
                        pickup5.get(0).getName().equals("P6") &&
                        pickup5.get(1).getName().equals("P7") &&
                        pickup5.get(2).getName().equals("P5") &&
                        pickup5.get(3).getName().equals("P4") &&
                        pickup5.get(4).getName().equals("P3");
        assertTest("Helicopter picks up only 5 patients in priority order", fiveOnly);
        assertTest("Patients remain after 5-person pickup", h8.getCapacityUsed() == 2);

        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }

    static void assertTest(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + testName);
        } else {
            failed++;
            System.out.println("FAIL: " + testName);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FieldHospital hospital = new FieldHospital();

        System.out.print("Run tests first? (y/n): ");
        String runTestsChoice = scanner.nextLine().trim();
        if (runTestsChoice.equalsIgnoreCase("y")) {
            runTests();
        }

        boolean running = true;

        while (running) {
            System.out.println("\n=== Zombie Apocalypse Field Hospital ===");
            System.out.println("Time: Hour " + hospital.getCurrentHour());
            System.out.println("Hospital capacity: " + hospital.getCapacityUsed() + "/" + hospital.getMaxCapacity());
            System.out.println("Next helicopter at hour: " + hospital.getNextHelicopterHour());
            System.out.println("Helicopter pickup limit: " + hospital.getHelicopterCapacity());

            if (hospital.isHelicopterHere()) {
                System.out.println("🚁 HELICOPTER IS HERE — READY TO PICK UP 5 PATIENTS");
            } else {
                System.out.println("⏳ Helicopter not here yet.");
            }

            System.out.println("\n1. Add patient");
            System.out.println("2. View next patient");
            System.out.println("3. Treat next patient");
            System.out.println("4. Show all patients");
            System.out.println("5. Show hospital status");
            System.out.println("6. Advance time");
            System.out.println("7. Helicopter pickup");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter patient name: ");
                    String name = scanner.nextLine().trim();

                    int severity;
                    while (true) {
                        System.out.print("Enter severity (1-10, lower = less severe): ");
                        String severityInput = scanner.nextLine().trim();
                        try {
                            severity = Integer.parseInt(severityInput);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid whole number.");
                        }
                    }

                    System.out.print("Enter condition: ");
                    String condition = scanner.nextLine().trim();

                    if (hospital.addPatient(name, severity, condition)) {
                        System.out.println("Patient added successfully.");
                    } else {
                        System.out.println("Could not add patient. Name/severity may be invalid or hospital may be full.");
                    }
                    break;

                case "2":
                    Patient nextPatient = hospital.viewNextPatient();
                    if (nextPatient == null) {
                        System.out.println("No patients waiting.");
                    } else {
                        System.out.println("Next patient: " + nextPatient);
                    }
                    break;

                case "3":
                    Patient treatedPatient = hospital.treatNextPatient();
                    if (treatedPatient == null) {
                        System.out.println("No patients to treat.");
                    } else {
                        System.out.println("Treating patient: " + treatedPatient);
                    }
                    break;

                case "4":
                    List<Patient> allPatients = hospital.getAllPatients();
                    if (allPatients.isEmpty()) {
                        System.out.println("No patients waiting.");
                    } else {
                        System.out.println("=== Patients in Priority Order ===");
                        for (int i = 0; i < allPatients.size(); i++) {
                            System.out.println((i + 1) + ". " + allPatients.get(i));
                        }
                    }
                    break;

                case "5":
                    System.out.println("=== Hospital Status ===");
                    System.out.println("Current hour: " + hospital.getCurrentHour());
                    System.out.println("Next helicopter at hour: " + hospital.getNextHelicopterHour());
                    System.out.println("Hospital capacity used: " + hospital.getCapacityUsed() + "/" + hospital.getMaxCapacity());
                    System.out.println("Hospital capacity remaining: " + hospital.getCapacityRemaining());
                    System.out.println("Helicopter pickup limit: " + hospital.getHelicopterCapacity());
                    System.out.println("Helicopter interval: " + hospital.getHelicopterIntervalHours() + " hours");
                    if (hospital.isHelicopterHere()) {
                        System.out.println("Helicopter status: HERE NOW");
                    } else {
                        System.out.println("Helicopter status: NOT HERE");
                    }
                    break;

                case "6":
                    int hours;
                    while (true) {
                        System.out.print("Advance how many hours? ");
                        String hourInput = scanner.nextLine().trim();
                        try {
                            hours = Integer.parseInt(hourInput);
                            if (hours > 0) {
                                break;
                            } else {
                                System.out.println("Enter a number greater than 0.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid whole number.");
                        }
                    }

                    hospital.advanceHours(hours);
                    System.out.println("Time advanced to hour " + hospital.getCurrentHour() + ".");

                    if (hospital.isHelicopterHere()) {
                        System.out.println("🚁 The helicopter has arrived.");
                    } else {
                        int remaining = hospital.getNextHelicopterHour() - hospital.getCurrentHour();
                        System.out.println("⏳ Helicopter arrives in " + remaining + " hours.");
                    }
                    break;

                case "7":
                    if (!hospital.isHelicopterHere()) {
                        System.out.println("The helicopter is not here yet.");
                    } else {
                        List<Patient> pickedUp = hospital.helicopterPickup();
                        System.out.println("🚁 Helicopter pickup starting...");
                        if (pickedUp.isEmpty()) {
                            System.out.println("No patients were waiting.");
                        } else {
                            System.out.println("Picked up these patients in least-severity-first order:");
                            for (int i = 0; i < pickedUp.size(); i++) {
                                System.out.println((i + 1) + ". " + pickedUp.get(i));
                            }
                        }
                        System.out.println("Next helicopter scheduled for hour " + hospital.getNextHelicopterHour() + ".");
                    }
                    break;

                case "8":
                    running = false;
                    System.out.println("Exiting program.");
                    break;

                default:
                    System.out.println("Invalid option. Choose 1-8.");
            }
        }

        scanner.close();
    }
}