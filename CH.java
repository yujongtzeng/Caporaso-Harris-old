import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.lang.StringBuilder;
/**
 * This CH class implements the recursive formula of Caporaso-Harris in 
 * "Counting plane curves of any genus" for computing the number of nodal 
 * curves on the projective plane.
 * <p>
 * The user needs to enter the maximal degree and the maximal number of 
 * nodes one wants to compute for the curves. 
 * <p>
 * The program will print the number of degree d curves on the projective
 * plane with r nodes (strictly speaking, geometric genus (d-1)*(d-2)/2 - r)
 * which satisfy tangency conditions (alpha, beta) with a given line for  
 * <br />
 * 1) d = the biggest 5 positive integers less or equal to max degree 
 *    (5 is the default number and can be changed by modifying the instance 
 *    variable printLast),
 * <br />   
 * 2) all nonnegative integers r less or equal to max number of nodes, <br />   
 * 3) all valid tangency conditions alpha and beta 
 *    (valid = satisfy I(alpha)+I(beta) = d). 
 * <p>
 * alpha : tangency conditions at assigned points.  <br />
 * beta : tangency conditions at unassigned points. 
 * beta = (beta_1, beta_2,....).
 * <p>
 * Notes on algorithm: <br />
 * This class and F0Table uses the same algorithm. 
 * @author Yu-jong Tzeng
 * @version 3.0
 * @since June 19, 2019.
 */

public class CH {
    private static int deg;
    private static int maxNode;
    private static int maxLength;
    private static arrayOP arrOP;   
    /**
     * The number of different degrees which will be printed out. 
     * The output will will contain CH invariants for d = 
     * (maxNode -printLast +1) to maxNode. 
     */
    public static int printLast;        
    public static int wDeg;        
    private HashMap<ArrayList<Integer>, Long> prevMap;
    private HashMap<ArrayList<Integer>, Long> curMap;    
    private partitions parArr; 

    /**
    * The constructor of the class.
    * @param deg The maximal degree of the curve. 
    * @param maxNode The maximal number of nodes of the curves in query.
    */
    public CH (int deg, int maxNode) {
        this.deg = deg;
        this.maxNode = maxNode; 
        // maxLength = |beta'| <= d
        maxLength = deg;           
        arrOP = new arrayOP(maxLength);      
        printLast = 5;
        wDeg = 10;
        parArr = new partitions(deg, maxLength, arrOP);
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
        int deg = reader.nextInt();
        System.out.println("Enter the max number of nodes:");        
        System.out.println("maxNode = ");
        int maxNode = reader.nextInt();        
        System.out.format("The output will be written in the directory" + 
                           "../output/CH\n");
        reader.close();
                 
        CH ch = new CH(deg, maxNode);
        ch.compute();
    }
    
    /** 
     * Put N(O(d), r, alpha and beta) into dictionary 
     * for given d, r = 0,...,maxNode and all valid alpha and beta.
     * If d >= deg - printLast, write the result in the output file. 
     */
    private void compute() {
        for (int d = 1; d <= deg - printLast; d++) {
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();                        
            // Compute N and put in the table
            for (int r = 0; r <= maxNode; r++) {                
                    for (int j = d; j >= 0; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            for (int[] beta : parArr.get(d - j)) {
                            long ansN = N(d, r, alpha, beta);
                            curMap.put(Key.make(d, r, alpha, beta), ansN);
                        }
                    }
                }
            }            
        }       
        for (int d = Math.max(deg - printLast + 1, 1); d <= deg; d++) {
            prevMap = curMap;
            curMap = new HashMap<ArrayList<Integer>, Long>();
            for (int r = 0; r <= maxNode; r++) {
                if (d == deg) {curMap = new HashMap<ArrayList<Integer>, Long>();}
                try {
                    File outputfile = new File("output/CH/O("+ d + ")_r=" 
                        + r + ".txt");
                    File genFun = new File("output/genFunCH/O("+ d + ")_r=" 
                        + r + ".txt"); 
                    outputfile.getParentFile().mkdirs();
                    genFun.getParentFile().mkdirs();
                    PrintWriter pw = new PrintWriter(outputfile, "UTF-8");
                    PrintWriter gen = new PrintWriter(genFun, "UTF-8");
                    for (int j = d; j > 4; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            for (int[] beta : parArr.get(d - j)) {
                                long ansN = N(d, r, alpha, beta);
                                curMap.put(Key.make(d, r, alpha, beta), ansN);
                                pw.printf("N(O(%d), %d, %s, %s) = %d\n", 
                                    d, r, myf.str(alpha), myf.str(beta), ansN);
                            }
                        }    
                    }
                    for (int j = Math.min(4, d); j >= 0; j--) {
                        for (int[] alpha : parArr.get(j)) {
                            gen.println("alpha = " + myf.str(alpha));
                            for (int[] beta : parArr.get(d - j)) {
                                long ansN = N(d, r, alpha, beta);
                                curMap.put(Key.make(d, r, alpha, beta), ansN);
                                pw.printf("N(O(%d), %d, %s, %s) = %d\n", 
                                    d, r, myf.str(alpha), myf.str(beta), ansN);
                                if (d - j - beta[0] <= wDeg) {
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
    private long N(int d, int r, int[] alpha, int[] beta) {  
        if (arrOP.I(alpha) + arrOP.I(beta) != d) {
            System.out.format("I(%s) + I(%s) must equal to %d\n", 
                               myf.str(alpha), myf.str(beta), d);
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
                if (curMap.containsKey(Key.make(d, r, tempAlpha, tempBeta))) 
                    ans = ans + (k + 1) * 
                          curMap.get(Key.make(d, r, tempAlpha, tempBeta));
                else {
                    System.out.format("N(%d, %d, %s, %s) can't be found.\n", 
                                d, r, myf.str(tempAlpha), myf.str(tempBeta));
                }                                     
            }                
        }        
        if (d > 0) {                              // the second term
            for (int j = arrOP.sum(beta) - r + d- 1; j < d; j++) {
                for (int[] bP : parArr.get(j)) {
                    for (int[] aP : parArr.get(d - 1 - j)) {
                        if (arrOP.greater(alpha, aP) && arrOP.greater(bP, beta)) {
                            int[] gamma = arrOP.substract(bP, beta);
                            int rP = r + arrOP.sum(gamma) - d + 1;
                            if (rP >= 0 && rP <= maxNode) {
                                if (prevMap.containsKey(Key.make(d - 1, rP, aP, bP))) {
                                    long coeff = arrOP.J(gamma) * 
                                        arrOP.binom(alpha, aP) * arrOP.binom(bP, beta);
                                    ans = ans + 
                                        coeff * prevMap.get(Key.make(d - 1, rP, aP, bP));
                                }
                                else { // Table doesn't contain this term
                                    System.out.format("Finding N(%d, %d, %s, %s)\n", 
                                               d, r, myf.str(alpha), myf.str(beta));
                                    System.out.format("N(%d, %d, %s, %s) not found.\n", 
                                              d - 1, rP, myf.str(aP), myf.str(bP));
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

