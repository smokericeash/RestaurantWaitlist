import java.io.*;
import java.util.*;
public class test{
    public static void main(String[] args){
        System.out.println("H Print Help Message");
        System.out.println("J Join the waitlist (FirstName LastName PartySize)");
        System.out.println("V View the current waitlist");
        System.out.println("Q Quit");
        System.out.println("Policy A: Seats only the first party in line, and if they don't fit, nobody else gets seated until the next time step.");
        System.out.println("Policy B: Scans the entire waitlist and seats the first party that can fit, skipping those who can't without losing their place in line.");

        Table[] tables = new Table[6];
        Party[] seatedParty = new Party[50];
        int[] seatedCount = {0};
        int count = 0;
        int queueSize=0;
        try{
            // Read table configuration from external text file
            Scanner fileReader = new Scanner(new File("tables.txt"));
            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine().trim();
                String[] parts = line.split("\\s+");
                int tableID = Integer.parseInt(parts[0]);
                int seats = Integer.parseInt(parts[1]);
                // Parse IDs of tables that can be combined with this one
                int[] combineableWith = new int[parts.length-2];
                for(int i = 2; i < parts.length; i++){
                    combineableWith[i-2] = Integer.parseInt(parts[i]);
                }
                tables[count] = new Table(tableID, seats, combineableWith);
                count++;
            }
            fileReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Table file was not found within the library, please restart.");
        }
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Would you like to implement Policy A or Policy B? (Type in A or B): ");
        String inputPolicy = keyboard.nextLine().trim();
        // Initialize the waitlist using a linked queue structure
        QueueInterface<Party> waitList = new LinkedQueue<>();

        if(!inputPolicy.equalsIgnoreCase("A") && !inputPolicy.equalsIgnoreCase("B")){
            throw new IllegalArgumentException();
        }

        boolean running = true;
        int currentTime = 1;
        while(running){
            System.out.println("Time " + currentTime + ":");
            System.out.print("Please enter your choice: ");
            String input = keyboard.nextLine().trim();
            String[] parts = input.split("\\s+");

            currentTime++;

            if(input.equalsIgnoreCase("Q")){
                // Calculate and display final wait time statistics before exiting
                double totalTime = 0;
                int maxTime = 0;
                for(int i = 0; i < seatedCount[0] ; i++){
                    int wait = seatedParty[i].getWaitTime();
                    totalTime+=wait;
                    if(wait > maxTime){
                        maxTime = wait;
                    }
                    System.out.println(seatedParty[i].getName() + " (" + seatedParty[i].getPartySize() + ") : Arrived: " + seatedParty[i].getArrivalTime() + "| Seated: " + seatedParty[i].getSeatingTime() + "| Wait: " + wait); 
                }
                double avgWaitTime = totalTime/seatedCount[0];
                System.out.printf("Average wait time: %.2f%n", avgWaitTime);
                System.out.println("Maximum wait time: " + maxTime);
                System.out.println("Total parties served: " + seatedCount[0]);
                running=false;
            }
            if(!running) continue;

            if(input.equalsIgnoreCase("H")){
                System.out.println("=== HELP ===");
                System.out.println("H - Display this help message");
                System.out.println("J <FirstName> <LastName> <PartySize> - Join the waitlist");
                System.out.println("    Example: J John Smith 4");
                System.out.println("V - View the current waitlist and your position");
                System.out.println("Q - Quit the program and display wait time statistics");
                System.out.println("============"); 
            }
            if(input.equalsIgnoreCase("V")){
                // Temporarily dequeue and re-enqueue to display list contents
                System.out.println("Current Waitlist:");
                if(waitList.isEmpty()){
                    System.out.println("No one is currently on the waitlist.");
                }
                else{
                    int numbered = 1;
                    for(int i = 0; i < queueSize ; i++){
                        Party current = waitList.dequeue();
                        System.out.println(numbered + ". " + current.getName() + " (" + current.getPartySize() + ")");
                        waitList.enqueue(current);
                        numbered++;
                    }
                }

            }
            if(input.toUpperCase().startsWith("J")){
                // Create new party object and add to the end of the queue
                int arrivalTime = currentTime-1;
                Party party = new Party();
                party.setName(parts[1] + " " + parts[2]);
                party.setPartySize(Integer.parseInt(parts[3]));
                party.setArrivalTime(arrivalTime);
                waitList.enqueue(party);
                queueSize++;
                System.out.println(party.getName() + " (" + party.getPartySize() + ") has joined the waitlist.");   
                System.out.println("You are #" + queueSize + " in the waitlist.");     
            }

            // Check each table to see if the dining time has elapsed
            for (int i = 0; i < tables.length; i++) {
                if (!tables[i].isAvailable() && tables[i].getAvailableAtTime() <= currentTime) {
                    tables[i].setAvailable(true);
                }
            }
            
            // Execute seating logic based on user's chosen policy
            if(inputPolicy.equalsIgnoreCase("A")){
                if(seatPartyPolicyA(waitList, tables, currentTime, seatedParty, seatedCount))
                    queueSize--;
            }
            else{
                if(seatPartyPolicyB(waitList, tables, currentTime, queueSize, seatedParty, seatedCount))
                    queueSize--;
            }            

        }
    }

public static boolean seatPartyPolicyA(QueueInterface<Party> waitList, Table[] tables, int currentTime, Party[] seatedParty, int[] seatedCount){
        if(waitList.isEmpty()){
            return false;
        }
        
        Party front = waitList.getFront();
        int partySize = front.getPartySize();

        // 1. Try to find the BEST single table (min wasted seats, then min ID)
        int bestSingleIdx = -1;
        int minWastedSingle = Integer.MAX_VALUE;

        for(int i = 0; i < tables.length; i++){
            if(tables[i].isAvailable() && tables[i].getSeats() >= partySize){
                int wasted = tables[i].getSeats() - partySize;
                if(wasted < minWastedSingle){
                    minWastedSingle = wasted;
                    bestSingleIdx = i;
                } else if(wasted == minWastedSingle){
                    if(bestSingleIdx == -1 || tables[i].getTableID() < tables[bestSingleIdx].getTableID()){
                        bestSingleIdx = i;
                    }
                }
            }
        }

        if(bestSingleIdx != -1){
            waitList.dequeue();
            front.setSeatingTime(currentTime);
            tables[bestSingleIdx].setAvailable(false);
            tables[bestSingleIdx].setAvailableAtTime(currentTime + (10 + 2 * partySize));
            System.out.println("Now Seating: " + front.getName() + "(" + front.getPartySize() + ")" + " at Table " + tables[bestSingleIdx].getTableID());
            seatedParty[seatedCount[0]] = front;
            seatedCount[0]++;
            return true;
        }

        // 2. If no single table works, find the BEST combination
        int[] combination = findTableCombo(front, tables);
        if(combination != null){
            waitList.dequeue();
            front.setSeatingTime(currentTime);
            tables[combination[0]].setAvailable(false);
            tables[combination[0]].setAvailableAtTime(currentTime + (10 + 2 * partySize));
            tables[combination[1]].setAvailable(false);
            tables[combination[1]].setAvailableAtTime(currentTime + (10 + 2 * partySize));
            System.out.println("Now Seating: " + front.getName() + "(" + front.getPartySize() + ")" + " at Table " + tables[combination[0]].getTableID() + " and at Table " + tables[combination[1]].getTableID());
            seatedParty[seatedCount[0]] = front;
            seatedCount[0]++;
            return true;
        }
        return false;
    }

    public static boolean seatPartyPolicyB(QueueInterface<Party> waitList, Table[] tables, int currentTime, int queueSize, Party[] seatedParty, int[] seatedCount){
        if(waitList.isEmpty()){ 
            return false;
        }

        QueueInterface<Party> tempQueue = new LinkedQueue<>();
        boolean foundAnyToSeat = false;

        for(int i = 0; i < queueSize; i++){
            Party currentParty = waitList.dequeue();
            
            if(!foundAnyToSeat){
                // Try single table first
                int bestS = -1;
                int minW = Integer.MAX_VALUE;
                for(int j = 0; j < tables.length; j++){
                    if(tables[j].isAvailable() && tables[j].getSeats() >= currentParty.getPartySize()){
                        int w = tables[j].getSeats() - currentParty.getPartySize();
                        if(w < minW){
                            minW = w;
                            bestS = j;
                        } else if(w == minW){
                            if(bestS == -1 || tables[j].getTableID() < tables[bestS].getTableID()) bestS = j;
                        }
                    }
                }

                if(bestS != -1){
                    currentParty.setSeatingTime(currentTime);
                    tables[bestS].setAvailable(false);
                    tables[bestS].setAvailableAtTime(currentTime + (10 + 2 * currentParty.getPartySize()));
                    System.out.println("Now Seating: " + currentParty.getName() + "(" + currentParty.getPartySize() + ")" + " at Table " + tables[bestS].getTableID());
                    seatedParty[seatedCount[0]] = currentParty;
                    seatedCount[0]++;
                    foundAnyToSeat = true;
                    continue; // Do not enqueue, party is seated
                }

                // Try combo if single fails
                int[] combo = findTableCombo(currentParty, tables);
                if(combo != null){
                    currentParty.setSeatingTime(currentTime);
                    tables[combo[0]].setAvailable(false);
                    tables[combo[0]].setAvailableAtTime(currentTime + (10 + 2 * currentParty.getPartySize()));
                    tables[combo[1]].setAvailable(false);
                    tables[combo[1]].setAvailableAtTime(currentTime + (10 + 2 * currentParty.getPartySize()));
                    System.out.println("Now Seating: " + currentParty.getName() + "(" + currentParty.getPartySize() + ")" + " at Table " + tables[combo[0]].getTableID() + " and at Table " + tables[combo[1]].getTableID());
                    seatedParty[seatedCount[0]] = currentParty;
                    seatedCount[0]++;
                    foundAnyToSeat = true;
                    continue; // Do not enqueue
                }
            }
            tempQueue.enqueue(currentParty);
        }

        while(!tempQueue.isEmpty()){
            waitList.enqueue(tempQueue.dequeue());
        }
        return foundAnyToSeat;
    }

    public static int[] findTableCombo(Party party, Table tables[]){ 
        int bestI = -1, bestJ = -1;
        int minWasted = Integer.MAX_VALUE;

        for(int i = 0; i < tables.length; i++){
            if(!tables[i].isAvailable()) continue;
            
            int[] combinableWith = tables[i].getCombinableWith();
            for(int partnerID : combinableWith){
                for(int j = 0; j < tables.length; j++){
                    if(tables[j].getTableID() == partnerID && tables[j].isAvailable()){
                        int totalSeats = tables[i].getSeats() + tables[j].getSeats();
                        if(totalSeats >= party.getPartySize()){
                            int wasted = totalSeats - party.getPartySize();
                            
                            // Determine tie-break order for this specific pair
                            int smallID = Math.min(tables[i].getTableID(), tables[j].getTableID());
                            int largeID = Math.max(tables[i].getTableID(), tables[j].getTableID());

                            if(wasted < minWasted){
                                minWasted = wasted;
                                bestI = i; bestJ = j;
                            } else if(wasted == minWasted){
                                // Tie-break: compare smallest first ID, then second ID
                                if(bestI == -1){
                                    bestI = i; bestJ = j;
                                } else {
                                    int currentSmall = Math.min(tables[bestI].getTableID(), tables[bestJ].getTableID());
                                    int currentLarge = Math.max(tables[bestI].getTableID(), tables[bestJ].getTableID());
                                    if(smallID < currentSmall || (smallID == currentSmall && largeID < currentLarge)){
                                        bestI = i; bestJ = j;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return (bestI != -1) ? new int[]{bestI, bestJ} : null;
    }

}