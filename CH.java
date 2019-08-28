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
 * This CH class implements the recursive formula of Caporaso-Harris in 
 * "Counting plane curves of any genus" for computing the number of nodal 
 * curves on the projective plane.
 * <p>
 * The user needs to enter the maximal degree and the maximal number of 
 * nodes one wants to compute for the curves. <br>
 * 
 * The program will print the number of degree d curves on the projective
 * plane with r nodes (strictly speaking, geometric genus (d-1)*(d-2)/2 - r)
 * which satisfy tangency conditions (alpha, beta) with a given line for  
 * <br>
 * 1) d = the biggest 5 positive integers less or equal to max degree 
 *    (5 is the default number and can be changed by modifying the instance 
 *    variable printLast), <br>  
 * 2) all nonnegative integers r less or equal to max number of nodes, <br>   
 * 3) all valid tangency conditions alpha and beta 
 *    (valid = satisfy I(alpha)+I(beta) = d). 
 * <p>
 * alpha : tangency conditions at assigned points.  <br>
 * beta : tangency conditions at unassigned points. 
 * beta = (beta_1, beta_2,....).
 * <p> 
 * The output numbers will be located at output/CH <br>
 * All terms in the generating series satisfying total degree <= 5 and 
 * weighted degree <= 10 will be written in files at output/genFunCH. <br>
 * wdeg r = 0, wdeg b = 2, wdeg c = 3, .....
 * <p>
 * Notes on algorithm: <br>
 * This class and F0Table uses the same algorithm. 
 * @author Yu-jong Tzeng
 * @version 3.0
 * @since August 24, 2019.
 */

public class CH {
    private static int deg;
    private static int maxNode;
    private static int maxLength;
    private static ArrayOp arrOP;   
    /**
     * The number of different degrees which will be printed out. 
     * The output will will contain CH invariants for d = 
     * (maxNode -printLast +1) to maxNode. 
     */
    public static int printLast;        
    private static int wDeg;        
    private HashMap<ArrayList<Integer>, Long> prevMap;
    private HashMap<ArrayList<Integer>, Long> curSave;   
    private HashMap<ArrayList<Integer>, Long> curDump;   
    private Partitions parArr; 
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
        arrOP = new ArrayOp(maxLength);      
        printLast = deg;
        wDeg = 10;
        parArr = new Partitions(deg, maxLength);
        prevMap = new HashMap<>();
        curSave = new HashMap<>();
        curDump = new HashMap<>();
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
            System.out.println("Computing d = " + d);
            prevMap = curSave;
            curSave = new HashMap<>();  
            curDump = new HashMap<>();   
            HashMap<ArrayList<Integer>, Long> cur = curDump;
            // Compute N and put in the table
            for (int r = 0, j = d; r <= maxNode && j >=0; r++, j--) {       
                if (j <= maxNode) {cur = curSave;}
                for (int[] alpha : parArr.get(j)) {
                    for (int[] beta : parArr.get(d - j)) {
                        long ansN = N(d, r, alpha, beta);
                        cur.put(Key.make(r, alpha, beta), ansN);
                    }
                }
            }     
        }       
        for (int d = Math.max(deg - printLast + 1, 1); d <= deg; d++) {
            System.out.println("Computing d = " + d);
            prevMap = curSave;
            curSave = new HashMap<>();
            curDump = new HashMap<>();
            HashMap<ArrayList<Integer>, Long> cur = curDump;
            for (int r = 0; r <= maxNode; r++) {
                if (d == deg) {
                    curSave = new HashMap<>();
                    curDump = new HashMap<>();
                    cur = curDump;
                }
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
                        if (j <= maxNode) {cur = curSave;}
                        for (int[] alpha : parArr.get(j)) {
                            for (int[] beta : parArr.get(d - j)) {
                                long ansN = N(d, r, alpha, beta);
                                cur.put(Key.make(r, alpha, beta), ansN);
                                pw.printf("N(O(%d), %d, %s, %s) = %d\n", 
                                    d, r, MyF.str(alpha), MyF.str(beta), ansN);
                            }
                        }    
                    }
                    for (int j = Math.min(4, d); j >= 0; j--) {
                        if (j <= maxNode) {cur = curSave;}
                        for (int[] alpha : parArr.get(j)) {
                            gen.println("alpha = " + MyF.str(alpha));
                            for (int[] beta : parArr.get(d - j)) {
                                long ansN = N(d, r, alpha, beta);
                                cur.put(Key.make(r, alpha, beta), ansN);
                                pw.printf("N(O(%d), %d, %s, %s) = %d\n", 
                                    d, r, MyF.str(alpha), MyF.str(beta), ansN);
                                if (d - j - beta[0] <= wDeg) {
                                    gen.printf(ansN + MyF.toVar(beta) + "+" );                                
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
            /*System.out.println("curSave:");
            for (ArrayList<Integer> key: curSave.keySet()) {
                System.out.println(key + curSave.get(key));
            }
            System.out.println("curDump:");
            for (ArrayList<Integer> key: curDump.keySet()) {
                System.out.println(key + curDump.get(key));
            }
            System.out.println("prevMap:");
            for (ArrayList<Integer> key: prevMap.keySet()) {
                System.out.println(key + prevMap.get(key));
            }*/
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
                ArrayList<Integer> key = Key.make(r, tempAlpha, tempBeta);
                if (curSave.containsKey(key)) {
                    ans = ans + (k + 1) * 
                        curSave.get(key);
                }
                else if (curDump.containsKey(key)) {
                    ans = ans + (k + 1) * 
                        curDump.get(key);
                }
                else {
                    System.out.format("Finding N(%d, %d, %s, %s)\n", 
                                    d, r, MyF.str(alpha), MyF.str(beta));
                    System.out.format("N(%d, %d, %s, %s) can't be found.\n", 
                        d, r, MyF.str(tempAlpha), MyF.str(tempBeta));
                }                                    
            }                
        }        
        // the second term
        for (int j = Math.max(0, arrOP.sum(beta) - r + d- 1); j < d; j++) {
            for (int[] bP : parArr.get(j)) {
                for (int[] aP : parArr.get(d - 1 - j)) {
                    if (arrOP.greater(alpha, aP) && arrOP.greater(bP, beta)) {
                        int[] gamma = arrOP.substract(bP, beta);
                        int rP = r + arrOP.sum(gamma) - d + 1;
                        if (rP >= 0 && rP <= maxNode) {
                            ArrayList<Integer> key = Key.make(rP, aP, bP);
                            if (prevMap.containsKey(key)) {
                                long coeff = arrOP.J(gamma) * 
                                    arrOP.binom(alpha, aP) * arrOP.binom(bP, beta);
                                ans = ans + 
                                    coeff * prevMap.get(key);
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
        return ans;                                     
    }                                
}
