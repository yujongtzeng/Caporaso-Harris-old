import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The F0table class uses dynamic programming approach to implement the 
 * recursive formula of Vakil in "Counting curves on rational surfaces" 
 * to compute the number of singular curves on P^1*P^1 which satisfy tangency 
 * conditions with a given line in |O(1,0)|. 
 * <p>
 * The user needs to enter a, b and gdiff. 
 * <p>
 * (a,b): the maximal bi-degrees of the curve class <br />
 * gdiff:  max difference between the arithmetic genus and geometric genus of 
 * the curve the program will compute.  <br />
 * <p>
 * The program will print our the number of genus g curves in |O(i,b)| on 
 * P^1*P^1 which satisfy tangency conditions (alpha, beta) with a given line 
 * in |O(1,0)| for  <br />
 * 1) (i,b) for i = (a - 5 + 1),..., a (due to the recursive nature 
 * of Vakil's formula) (5 is the default number and can be changed by 
 * modifying the instance variable printLast),  <br />
 * 2) all g between (arithmetic genus of O(i,b) - gdiff) and 
 *    arithmetic genus of O(i,b),  <br />
 * 3) all valid tangency conditions alpha and beta 
 *    (valid = satisfy I(alpha)+I(beta) = b). 
 * <p>
 * alpha : tangency conditions at assigned points.  <br />
 * beta : tangency conditions at unassigned points. beta = (beta_1, beta_2,....)
 * <p>
 * Note on algorithm:  <br />
 * alpha and beta are stored by integer arrays. The length of them (and 
 * variations) are of fixed length = maxLength = b + gdiff + 1. This is because 
 * <br />
 * g' = g-|gamma|+1 --> |gamma| <= g - g' + 1 = gdiff +1 
 * <br />
 * maxLength = |beta'| = |beta+gamma| <= |beta|+|gamma| <= b+ gdiff +1.
 * <br />
 * All methods in arrayOP will check if the length of inputs equals maxLength. 
 * <p>
 * @author Yu-jong Tzeng
 * @version 2.0
 * @since June 19, 2019.
 */

public class F0table {  
    private static int a;
    private static int b;         
    private static int gdiff;
    private static int maxLength;
    private static arrayOP arrOP;
    /**
     * The number of different first degrees of the curve class which will 
     * be printed out. 
     * The output will will contain number of curves in |O(i,b)| for i = 
     * (a - printLast + 1) to a.
     */
    public static int printLast;   
    public static int wDeg;       
    private static HashMap<ArrayList<Integer>, Long> prevMap;
    private static HashMap<ArrayList<Integer>, Long> curMap;   
    private partitions parArr; 

    /**
     * The constructor of the class.
     * @param a the number of ample class h in the curve class O(a,b)
     * @param b the number of fiber class h in the curve class O(a,b)
     * @param gdiff the max difference between arithmetic genus and geometric 
     * genus of the curve we'll compute
     */
    public F0table(int a, int b, int gdiff) {        
        this.a = a;
        this.b = b;
        this.gdiff = gdiff;       
        maxLength = b;    

        arrOP = new arrayOP(maxLength);  
        printLast = 3;
        wDeg = 10;
        parArr = new partitions(b, maxLength, arrOP);
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
        int a = reader.nextInt();
        System.out.println("Enter b:");        
        System.out.println("b = ");
        int b = reader.nextInt();
        System.out.println("Enter the max number of (arithmetic genus"
            + "- geometric genus):");        
        System.out.println("gdiff = ");
        int gdiff = reader.nextInt();        
        System.out.format("The output will be written in the directory" + 
                           "../output/F0\n");
        reader.close();
                 
        F0table f0Table = new F0table(a, b, gdiff);
        f0Table.compute();        
    }
    
    /** 
     * Run this method to compute and create output file.  
     */
    private void compute() {
        // Here we put N(O(i, b), all valid alpha and beta) into dictionary
        for (int i = 0; i <= a - printLast; i++) {
            System.out.println("Computing a = " + i);
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();                                      
            // Compute N and put in the table           
            for (int g = myf.g_a(i, b) - gdiff; g <= myf.g_a(i, b); g++) {                
                    for (int j = b; j >= 0; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            for (int[] beta : parArr.get(b - j)) {
                             curMap.put(Key.make(i, b, g, alpha, beta), 
                                N(i, b, g, alpha, beta));
                        }
                    }
                }
            }                   
        }       
        // Here we put N(O(i, b), all valid alpha and beta) into dictionary
        // and write output file
        for (int i = Math.max(a - printLast + 1, 0); i <= a; i++) {
            System.out.println("Computing a = " + i);
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();
            for (int g = myf.g_a(i, b) - gdiff; g <= myf.g_a(i, b); g++) {
                if (i == a) {curMap = new HashMap<ArrayList<Integer>, Long>();}
                try {
                    File outputfile = new File("output/F0/O("
                            + i + ", " + b + ")_g=" + g + ".txt");  
                    File genFun = new File("output/genFunF0/O("
                            + i + ", " + b + ")_g=" + g + ".txt");         
                    outputfile.getParentFile().mkdirs();
                    genFun.getParentFile().mkdirs();
                    PrintWriter pw = new PrintWriter(outputfile, "UTF-8"); 
                    PrintWriter gen = new PrintWriter(genFun, "UTF-8");
                    for (int j = b; j > 4; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            for (int[] beta : parArr.get(b - j)) {
                                long ansN = N(i, b, g, alpha, beta);
                                curMap.put(Key.make(i, b, g, alpha, beta), ansN);
                                pw.printf("N(O(%d, %d), %d, %s, %s) = %d\n", 
                                       i, b, g, myf.str(alpha), myf.str(beta), ansN);
                            }
                        }    
                    }
                    for (int j = Math.min(4, b); j >= 0; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            gen.println("alpha = " + myf.str(alpha));
                            for (int[] beta : parArr.get(b - j)) {
                                long ansN = N(i, b, g, alpha, beta);
                                curMap.put(Key.make(i, b, g, alpha, beta), ansN);
                                pw.printf("N(O(%d, %d), %d, %s, %s) = %d\n", 
                                       i, b, g, myf.str(alpha), myf.str(beta), ansN);
                                if (b - j - beta[0] <= wDeg) {
                                    gen.printf(ansN + myf.toVar(beta) + "+" );                                
                                }      
                            }
                            gen.println("\n");
                        }    
                    }
                    pw.close();
                    gen.close();
                } 
                catch (IOException e) {
                    System.out.println("There is an error in I/O.");
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
                                aa, bb, g, myf.str(tempAlpha), myf.str(tempBeta));
            }                
        }        
        if (aa > 0) {                              // the second term
            for (int j = arrOP.sum(beta) -myf.g_a(aa, bb) + g + b; j <= bb; j++) {
                for (int[] bP : parArr.get(j)) {
                    for (int[] aP : parArr.get(bb - j)) {
                        if (arrOP.greater(alpha, aP) && arrOP.greater(bP, beta)) {
                            int[] gamma = arrOP.substract(bP, beta);
                            int gP = g - arrOP.sum(gamma) + 1;
                            if (gP <= myf.g_a(aa - 1, bb) && gP >= myf.g_a(aa - 1, bb) - gdiff) {
                                if (prevMap.containsKey(Key.make(aa - 1, bb, gP, aP, bP))) {
                                    long coeff = arrOP.J(gamma) * arrOP.binom(alpha, aP) 
                                                 * arrOP.binom(bP, beta);
                                    ans = ans + coeff * prevMap.get(Key.make(aa - 1, bb, gP, aP, bP));
                                }
                                else { // Table doesn't contain this term
                                    System.out.format("Finding N(%d, %d, %d, %s, %s)\n",
                                        aa, bb, g, myf.str(alpha), myf.str(beta));
                                    System.out.format("N(%d, %d, %d, %s, %s) can't be found.\n", 
                                        a - 1, bb, gP, myf.str(aP), myf.str(bP));
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

