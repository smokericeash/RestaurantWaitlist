import java.util.*;
import java.io.*;
public class test{
    public static void main(String[] args){
        System.out.println("H Print Help Message");
        System.out.println("J Join the waitlist (FirstName LastName PartySize)");
        System.out.println("V View the current waitlist");
        System.out.println("Q Quit");
        System.out.println("Policy A: Seats only the first party in line, and if they don't fit, nobody else gets seated until the next time step.");
        System.out.println("Policy B: Scans the entire waitlist and seats the first party that can fit, skipping those who can't without losing their place in line.");

        Table[] tables = new Table[6];
        int count = 0;
        int queueSize=0;
        try{
            Scanner fileReader = new Scanner(new File("tables.txt"));
            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine().trim();
                String[] parts = line.split("\\s+");
                int tableID = Integer.parseInt(parts[0]);
                int seats = Integer.parseInt(parts[1]);
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
        QueueInterface<Party> waitList = new LinkedQueue<>();

        if(!inputPolicy.equalsIgnoreCase("A") && !inputPolicy.equalsIgnoreCase("B")){
            throw new IllegalArgumentException();
        }

        boolean running = true;
        int currentTime = 1;
        while(running){
            System.out.println("Time" + currentTime + ":");
            System.out.print("Please enter your choice: ");
            String input = keyboard.nextLine().trim();
            String[] parts = input.split("\\s+");

            currentTime++;

            if(input.equalsIgnoreCase("Q")){
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
                System.out.println("Current Waitlist:");

            }
            if(input.toUpperCase().startsWith("J")){
                int arrivalTime = currentTime-1;
                Party party = new Party();
                party.setName(parts[1] + " " + parts[2]);
                party.setPartySize(Integer.parseInt(parts[3]));
                party.setArrivalTime(arrivalTime);
                waitList.enqueue(party);
                queueSize++;
                System.out.println(party.getName() + " (" + party.getPartySize() + ") has joined the waitlist.");        
            }

            for (int i = 0; i < tables.length; i++) {
                if (!tables[i].isAvailable() && tables[i].getAvailableAtTime() <= currentTime) {
                    tables[i].setAvailable(true);
                }
            }
                //checking seatPolicyA/B
            if(inputPolicy.equalsIgnoreCase("A")){
                if(seatPartyPolicyA(waitList, tables, currentTime))
                    queueSize--;
            }
            else{
                if(seatPartyPolicyB(waitList, tables, currentTime, queueSize))
                    queueSize--;
            }            

        }
    }

    public static boolean seatPartyPolicyA(QueueInterface<Party> waitList, Table[] tables, int currentTime){
        if(waitList.isEmpty()){
            return false;
        }
        Party front = waitList.getFront();
        int partySize = front.getPartySize();

        //finding single table
        for(int i = 0; i < tables.length; i++){
            if(tables[i].isAvailable() && tables[i].getSeats() >= partySize){
                waitList.dequeue();
                front.setSeatingTime(currentTime);
                tables[i].setAvailable(false);
                int doneAtTime = currentTime + (10+2 * partySize);
                tables[i].setAvailableAtTime(doneAtTime);
                System.out.println("Now Seating: " + front.getName() + "(" + front.getPartySize() + ")" + " at Table " + tables[i].getTableID());
                return true;
            }
        }
        return false;
    }

    public static boolean seatPartyPolicyB(QueueInterface<Party> waitList, Table[] tables, int currentTime, int queueSize){
        if(waitList.isEmpty()){ //checking for empty queue 
            return false;
        }

        for(int i = 0; i < queueSize; i++){
            Party currentParty = waitList.dequeue();
            
            boolean seated = false;
            for(int j = 0; j < tables.length; j++){
                if(tables[j].isAvailable() && tables[j].getSeats() >= currentParty.getPartySize()){
                    currentParty.setSeatingTime(currentTime);
                    tables[j].setAvailable(false);
                    int doneAtTime = currentTime + (10+2 * currentParty.getPartySize());
                    tables[j].setAvailableAtTime(doneAtTime);
                    System.out.println("Now Seating: " + currentParty.getName() + "(" + currentParty.getPartySize() + ")" + " at Table " + tables[j].getTableID());
                    seated = true;
                    queueSize--;
                    return true;
                }
                
            }

            if(!seated){
                waitList.enqueue(currentParty);
            }
         
        }

        return false;
    }

}
