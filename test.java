import java.util.*;
public class test <T>{
    public static void main(String[] args){
        System.out.println("H Print Help Message");
        System.out.println("J Join the waitlist (FirstName LastName PartySize)");
        System.out.println("V View the current waitlist");
        System.out.println("H Print Help Message");
        System.out.println("Policy A: Seats only the first party in line, and if they don't fit, nobody else gets seated until the next time step.");
        System.out.println("Policy B: Scans the entire waitlist and seats the first party that can fit, skipping those who can't without losing their place in line.");
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Would you like to implement Policy A or Policy B? (Type in A or B)");
        String inputPolicy = keyboard.nextLine();
        
    }
}
