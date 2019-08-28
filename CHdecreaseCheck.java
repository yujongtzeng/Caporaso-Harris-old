import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.lang.StringBuilder;

/**
 * <p>
 * Let N(d, r, alpha, beta) be the number of r-nodal degree d curves on
 * P^2 which satisfy tangency conditions (alpha, beta) with a fixed 
 * line. 
 * CHdecreaseCheck is a program to check that for fixed d, r, beta, 
 * N(a, b, g, alpha, beta) is a non-increasing sequence in lexicographic 
 * order of alpha. 
 * If not, the counterexample will be printed out on the screen in the form
 * "N(a, b, g, alpha, beta) > last" where last is the number right before. 
 * </p>
 * 
 * deg: the maximal degrees of the curve <br>
 * maxNode:  max difference between the arithmetic genus and geometric genus of 
 * the curve the program will compute.  <br>
 * 
 * The program will check N(d, r, alpha, beta) for all d <= deg, 
 * 0 <= r <= maxNode, and all valid alpha and beta. 
 * 
 * @author Yu-jong Tzeng
 * @version 2.0
 * @since August 25, 2019.
 */

public class CHdecreaseCheck {
    private static int deg;
    private static int maxNode;
    private static int maxLength;
    private static ArrayOp arrOP;   
    /**
     * The number of different degrees which will be printed out. 
     * The output will will contain CH invariants for d = 
     * (maxNode -printLast +1) to maxNode. 
     */  
    private HashMap<ArrayList<Integer>, Long> prevMap;
    private HashMap<ArrayList<Integer>, Long> curMap;    
    private Partitions parArr; 

    /**
    * The constructor of the class.
    * @param deg The maximal degree of the curve. 
    * @param maxNode The maximal number of nodes of the curves in query.
    */
    public CHdecreaseCheck (int deg, int maxNode) {
        this.deg = deg;
        this.maxNode = maxNode; 
        // maxLength = |beta'| <= d
        maxLength = deg;           
        arrOP = new ArrayOp(maxLength);      
        parArr = new Partitions(deg, maxLength);
        prevMap = new HashMap<ArrayList<Integer>, Long>();
        curMap = new HashMap<ArrayList<Integer>, Long>();
    }
    
    /** 
     * The main method of the class.
     * Paramaters deg and maxNode are initialzed by user input. Then the 
     * program will compute CH invariants and generate output. 
     * @param args Unused
     */
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("This program computes the number of singular " + 
                           "curves on the projective plane.");
        System.out.println("Enter the max degree of the curve:");        
        System.out.println("degree = ");
        int degIn = reader.nextInt();
        System.out.println("Enter the max number of nodes:");        
        System.out.println("maxNode = ");
        int maxNodeIn = reader.nextInt();        
        System.out.format("The output will be written in the directory" + 
                           "../output/CH\n");
        reader.close();
                 
        CHdecreaseCheck check = new CHdecreaseCheck(degIn, maxNodeIn);
        check.compute();
    }
    
    /** 
     * Put N(O(d), r, alpha and beta) into dictionary 
     * for given d, r = 0,...,maxNode and all valid alpha and beta.
     * If d >= deg - printLast, write the result in the output file. 
     */
    private void compute() {    
        for (int d = 1; d <= deg; d++) {
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();
            System.out.println("Checking: d = " + d);
            for (int r = 0; r <= maxNode; r++) {
                for (int j = 0; j <= d; j++) {
                    for (int[] beta : parArr.get(j)) {
                        long lastN = Long.MAX_VALUE;
                        for (int[] alpha : parArr.get(d - j)) {
                            long ansN = N(d, r, alpha, beta);
                            curMap.put(Key.make(r, alpha, beta), ansN);
                            if (lastN < ansN) {
                                System.out.format("N(%d, %d, %s, %s) > %d\n"
                                    , d, r, MyF.str(alpha), MyF.str(beta), ansN);
                            }
                            lastN = ansN;
                        }
                    }    
                }                       
            }            
        }       
    }    
    /** 
     * The recursive formula is implemented here. 
     */
    private long N(int d, int r, int[] alpha, int[] beta) {  
        if (arrOP.I(alpha) + arrOP.I(beta) != d) {
            System.out.format("I(%s) + I(%s) must equal to %d\n", 
                               MyF.str(alpha), MyF.str(beta), d);
            return 0; 
        }    
        if (d <= 0) {
            System.out.format("Degree should be positive: " + d);
            return -2;
        }
        if (r < 0) {
            System.out.format("The number of nodes can't be negative: " + r);
            return -3;
        }
        if (d == 1 && r == 0 ) return 1;
        if (d == 1 && r != 0 ) return 0;
        
        //now d >=2
        long ans = 0 ;        
        for (int k = 0; k < maxLength; k++) { // the first term
            if (beta[k] > 0) {
                int[] tempAlpha = alpha.clone();
                int[] tempBeta = beta.clone();
                tempAlpha[k] = alpha[k] + 1;  //alpha_+e_k, beta-e_k
                tempBeta[k] = beta[k] - 1;                
                if (curMap.containsKey(Key.make(r, tempAlpha, tempBeta))) 
                    ans = ans + (k + 1) * 
                          curMap.get(Key.make(r, tempAlpha, tempBeta));
                else {
                    System.out.format("N(%d, %d, %s, %s) can't be found.\n", 
                                d, r, MyF.str(tempAlpha), MyF.str(tempBeta));
                }                                     
            }                
        }        
        if (d > 0) {                              // the second term
            for (int j = arrOP.sum(beta) - r + d - 1; j < d; j++) {
                for (int[] bP : parArr.get(j)) {
                    for (int[] aP : parArr.get(d - 1 - j)) {
                        if (arrOP.greater(alpha, aP) && arrOP.greater(bP, beta)) {
                            int[] gamma = arrOP.substract(bP, beta);
                            int rP = r + arrOP.sum(gamma) - d + 1;
                            if (rP >= 0 && rP <= maxNode) {
                                if (prevMap.containsKey(Key.make(rP, aP, bP))) {
                                    long coeff = arrOP.J(gamma) * 
                                        arrOP.binom(alpha, aP) * arrOP.binom(bP, beta);
                                    ans = ans + 
                                        coeff * prevMap.get(Key.make(rP, aP, bP));
                                }
                                else { // Table doesn't contain this term
                                    System.out.format("Finding N(%d, %d, %s, %s)\n", 
                                               d, r, MyF.str(alpha), MyF.str(beta));
                                    System.out.format("N(%d, %d, %s, %s) not found.\n", 
                                              d - 1, rP, MyF.str(aP), MyF.str(bP));
                                }              
                            }
                        }
                    }
                }                
            }
        }    
        return ans;                                     
    }     
}

