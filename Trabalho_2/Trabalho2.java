package Trabalho_2;

import java.util.Random;

/**
 * Trabalho2
 */
public class Trabalho2 {
    public static void main(String[] args) {
        Random r = new Random();
        System.out.printf("%d\n", r.ints(1, 5, 10).toArray()[0]);
    }
    
}