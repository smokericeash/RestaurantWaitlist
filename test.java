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
        try{
            Scanner fileReader = new Scanner("tables.txt");
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
            }
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
                Party party = new Party();
                party.setName(parts[1] + " " + parts[2]);
                party.setPartySize(Integer.parseInt(parts[3]));
                party.setArrivalTime(currentTime);
            }

        }
    }

    public static void seatPartyPolicyA(QueueInterface<Party> waitList, Table[] tables){

    }
}
