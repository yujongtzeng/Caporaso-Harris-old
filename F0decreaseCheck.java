import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * <p>
 * Let N(a, b, g, alpha, beta) be the number of  genus g curves in |O(a,b)| 
 * on P^1*P^1 which satisfy tangency conditions (alpha, beta) with a fixed 
 * line in |O(1,0)|. 
 * F0decreaseCheck is a program to check that for fixed a, b, g, beta, 
 * N(a, b, g, alpha, beta), is a non-increasing sequence in lexicographic 
 * order of alpha. 
 * If not, the counterexample will be printed out on the screen in the form
 * "N(a, b, g, alpha, beta) > last" where last is the number right before. 
 * </p>
 * 
 * (a,b): the maximal bi-degrees of the curve class <br>
 * 
 * maxNode:  max difference between the arithmetic genus and geometric genus of 
 * the curve the program will compute.  <br>
 * 
 * The program will check N(i, b, g', alpha, beta) for all i <= a, 0 <= g' <= g,
 * and all valid alpha and beta. 
 * 
 * @author Yu-jong Tzeng
 * @version 2.0
 * @since August 24, 2019.
 */
public class F0decreaseCheck {  
    private static int a;
    private static int b;         
    private static int gdiff;
    private static int maxLength;
    private static ArrayOp arrOP;
    /**
     * The number of different first degrees of the curve class which will 
     * be printed out. 
     * The output will will contain number of curves in |O(i,b)| for i = 
     * (a - printLast + 1) to a.
     */ 
    private static HashMap<ArrayList<Integer>, Long> prevMap;
    private static HashMap<ArrayList<Integer>, Long> curMap;   
    private Partitions parArr; 

    /**
     * The constructor of the class.
     * @param a the number of ample class h in the curve class O(a,b)
     * @param b the number of fiber class h in the curve class O(a,b)
     * @param gdiff the max difference between arithmetic genus and geometric 
     * genus of the curve we'll compute
     */
    public F0decreaseCheck(int a, int b, int gdiff) {        
        this.a = a;
        this.b = b;
        this.gdiff = gdiff;       
        maxLength = b;    

        arrOP = new ArrayOp(maxLength);  
        parArr = new Partitions(b, maxLength);
        prevMap = new HashMap<ArrayList<Integer>, Long>();
        curMap = new HashMap<ArrayList<Integer>, Long>();
    }
    
    /** 
     * Call this method compute the results and generate output. 
     * User needs to enter a, b, and gdiff. 
     * @param args Unused
     */
    public static void main(String[] args)
    {        
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("This program computes the number of singular"
            + "curves in |O(a,b)| on P^1*P^1");
        System.out.println("satisfy tangency conditions with a given line"
                + " in |O(1,0)|.");
        System.out.println("Enter a:");        
        System.out.println("a = ");
        int aIn = reader.nextInt();
        System.out.println("Enter b:");        
        System.out.println("b = ");
        int bIn = reader.nextInt();
        System.out.println("Enter the max number of (arithmetic genus"
            + "- geometric genus):");        
        System.out.println("gdiff = ");
        int gIn = reader.nextInt();        
        System.out.format("The output will be written in the directory" + 
                           "../output/F0\n");
        reader.close();
                 
        F0decreaseCheck check = new F0decreaseCheck(aIn, bIn, gIn);
        check.compute();        
    }
    
    /** 
     * Run this method to compute and create output file.  
     */
    private void compute() {
        // Here we put N(O(i, b), all valid alpha and beta) into dictionary
        for (int i = 0; i <= a; i++) {
            System.out.println("Computing a = " + i);
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();                                      
            // Compute N and put in the table           
            for (int g = MyF.g_a(i, b) - gdiff; g <= MyF.g_a(i, b); g++) {                
                for (int j = 0; j <= b; j++) {
                    for (int[] beta : parArr.get(j)) {
                        long lastN = Long.MAX_VALUE;
                        //System.out.print(MyF.str(beta));
                        for (int[] alpha : parArr.get(b - j)) {                                
                            long ansN =  N(i, b, g, alpha, beta);
                            curMap.put(Key.make(i, b, g, alpha, beta), ansN);
                            if (lastN < ansN) {
                                System.out.format("N(%d, %d, %d, %s, %s) > %d\n"
                                    , i, b, g, MyF.str(alpha), MyF.str(beta), lastN);
                            }
                            //System.out.print(ansN + " >= "); 
                            lastN = ansN;   
                        }  
                        //System.out.println();
                    }
                }
            }
        }                   
    }    
    /** 
     * The recursive formula is implemented here. 
     */
    private long N(int aa, int bb, int g, int[] alpha, int[] beta) {
        long ans = 0 ;        
        // invalid parameters
        if (arrOP.I(alpha) + arrOP.I(beta) != b  || aa < 0 || bb < 0) {
            return 0;             
        }
        // Base case. Only fiber class passing through points. 
        // from the beginning of Section 8. 
        else if (aa == 0 && arrOP.I(beta) == 0) { 
            if (alpha[0] == arrOP.sum(alpha) && g == 1 - bb ) {
                return 1;    
            }
            else {
                return 0;
            }
        }               
        for (int k = 0; k < maxLength; k++) {    // the first term
            if (beta[k] > 0) {
                int[] tempAlpha = alpha.clone();
                int[] tempBeta = beta.clone();
                //alpha_+e_k, beta-e_k
                tempAlpha[k] = alpha[k] + 1;
                tempBeta[k] = beta[k] - 1;  
                if (curMap.containsKey(Key.make(aa, bb, g, tempAlpha, tempBeta))) 
                    ans = ans + (k + 1) * 
                        curMap.get(Key.make(aa, bb, g, tempAlpha, tempBeta));
                else 
                    System.out.format("N(%d, %d, %d, %s, %s) can't be found.\n",
                                aa, bb, g, MyF.str(tempAlpha), MyF.str(tempBeta));
            }                
        }        
        if (aa > 0) {                              // the second term
            for (int j = arrOP.sum(beta) - MyF.g_a(aa, bb) + g + b; j <= bb; j++) {
                for (int[] bP : parArr.get(j)) {
                    for (int[] aP : parArr.get(bb - j)) {
                        if (arrOP.greater(alpha, aP) && arrOP.greater(bP, beta)) {
                            int[] gamma = arrOP.substract(bP, beta);
                            int gP = g - arrOP.sum(gamma) + 1;
                            if (gP <= MyF.g_a(aa - 1, bb) && gP >= MyF.g_a(aa - 1, bb) - gdiff) {
                                if (prevMap.containsKey(Key.make(aa - 1, bb, gP, aP, bP))) {
                                    long coeff = arrOP.J(gamma) * arrOP.binom(alpha, aP) 
                                                 * arrOP.binom(bP, beta);
                                    ans = ans + coeff * prevMap.get(Key.make(aa - 1, bb, gP, aP, bP));
                                }
                                else { // Table doesn't contain this term
                                    System.out.format("Finding N(%d, %d, %d, %s, %s)\n",
                                        aa, bb, g, MyF.str(alpha), MyF.str(beta));
                                    System.out.format("N(%d, %d, %d, %s, %s) can't be found.\n", 
                                        a - 1, bb, gP, MyF.str(aP), MyF.str(bP));
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

