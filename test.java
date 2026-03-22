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
            if(!running)
                break;
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
                System.out.println("Average wait time: " + totalTime/seatedCount[0]);
                System.out.println("Maximum wait time: " + maxTime);
                System.out.println("Total parties served: " + seatedCount[0]);
                running=false;
            }
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
        // Strict FIFO: Only check the party at the front of the queue
        Party front = waitList.getFront();
        int partySize = front.getPartySize();

        // Try to find a single available table that fits the party
        for(int i = 0; i < tables.length; i++){
            if(tables[i].isAvailable() && tables[i].getSeats() >= partySize){
                waitList.dequeue();
                front.setSeatingTime(currentTime);
                tables[i].setAvailable(false);
                // Calculate when the table will become free again
                int doneAtTime = currentTime + (10+2 * partySize);
                tables[i].setAvailableAtTime(doneAtTime);
                System.out.println("Now Seating: " + front.getName() + "(" + front.getPartySize() + ")" + " at Table " + tables[i].getTableID());
                seatedParty[seatedCount[0]] = front;
                seatedCount[0]++;
                return true;
            }
        }

        // If no single table works, check for a valid combination of two tables
        int[] combination = findTableCombo(front,tables);
        if(combination!=null){
            waitList.dequeue();
            front.setSeatingTime(currentTime);
            tables[combination[0]].setAvailable(false);
            tables[combination[0]].setAvailableAtTime(currentTime + (10+2*front.getPartySize()));
            tables[combination[1]].setAvailable(false);
            tables[combination[1]].setAvailableAtTime(currentTime + (10+2*front.getPartySize()));
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

        // Iterate through the waitlist to find ANY party that can fit
        for(int i = 0; i < queueSize; i++){
            Party currentParty = waitList.dequeue();
            
            boolean seated = false;
            // Check for single table availability
            for(int j = 0; j < tables.length; j++){
                if(tables[j].isAvailable() && tables[j].getSeats() >= currentParty.getPartySize()){
                    currentParty.setSeatingTime(currentTime);
                    tables[j].setAvailable(false);
                    int doneAtTime = currentTime + (10+2 * currentParty.getPartySize());
                    tables[j].setAvailableAtTime(doneAtTime);
                    System.out.println("Now Seating: " + currentParty.getName() + "(" + currentParty.getPartySize() + ")" + " at Table " + tables[j].getTableID());
                    seated = true;
                    seatedParty[seatedCount[0]] = currentParty;
                    seatedCount[0]++;
                    queueSize--;
                    // Re-assemble the queue after a party is seated
                    while(!waitList.isEmpty()){
                        tempQueue.enqueue(waitList.dequeue());
                    }
                    while(!tempQueue.isEmpty()){
                        waitList.enqueue(tempQueue.dequeue());
                    }
                    return true;
                }
                
            }

            // Check for table combinations if no single table is found
            if(!seated){
                int[] combo = findTableCombo(currentParty, tables);
                if(combo!=null){
                    currentParty.setSeatingTime(currentTime);
                    tables[combo[0]].setAvailable(false);
                    tables[combo[0]].setAvailableAtTime(currentTime + (10+2*currentParty.getPartySize()));
                    tables[combo[1]].setAvailable(false);
                    tables[combo[1]].setAvailableAtTime(currentTime + (10+2*currentParty.getPartySize()));
                    System.out.println("Now Seating: " + currentParty.getName() + "(" + currentParty.getPartySize() + ")" + " at Table " + tables[combo[0]].getTableID() + " and at Table " + tables[combo[1]].getTableID());
                    seated = true;
                    seatedParty[seatedCount[0]] = currentParty;
                    seatedCount[0]++;
                    while(!waitList.isEmpty()){
                        tempQueue.enqueue(waitList.dequeue());
                    }
                    while(!tempQueue.isEmpty()){
                        waitList.enqueue(tempQueue.dequeue());
                    }
                    return true;
                }
            }

            // If party couldn't be seated, move them to the temporary queue to maintain order
            if(!seated){
                tempQueue.enqueue(currentParty);
            }
         
        }

        // Restore the original waitlist from the temporary queue
        while(!tempQueue.isEmpty()){
            waitList.enqueue(tempQueue.dequeue());
        }
        return false;
    }

    // Search for two tables that are both available and marked as combinable
    public static int[] findTableCombo(Party party, Table tables[]){ 
        for(int i = 0; i < tables.length; i++){
            if(!tables[i].isAvailable()){
                continue;
            }
            int[] combinableWith = tables[i].getCombinableWith();
            for(int k = 0; k < combinableWith.length ; k++){
                int partnerID = combinableWith[k];

                for(int j = 0; j < tables.length; j++){
                    // Verify the partner table exists, is available, and combined capacity is sufficient
                    if(tables[j].getTableID()==partnerID && tables[j].isAvailable()){
                        if(tables[i].getSeats() + tables[j].getSeats() >= party.getPartySize()){
                            return new int[]{i,j};
                        }
                    }
                }
            }
        }
        return null;
    }

}