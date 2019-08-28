import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This HirTable class uses dynamic programming approach to implement the 
 * recursive formula of Vakil in "Counting curves on rational surfaces" to 
 * compute the number of singular curves on Hirzebruch surfaces F_n which 
 * satisfy tangency conditions with the divisor E = h-nf.
 * <p>
 * The user needs to enter n, a, b and gdiff. 
 *  <p>
 * n : The Hirzebruch surface is F_n. <br />
 * (a,b): the curve class is ah + bf with h^2 = n, f = fiber class. <br />
 * gdiff:  max difference between the arithmetic genus and geometric genus of 
 * the curve the program will compute. <br />
 * <p>
 * The program will compute the number of genus g curves in the linear system 
 * |ih + jf| on F_n which satisfy tangency conditions (alpha, beta) with the 
 * divisor E = h - nf. <br />
 * 1) ih + jf = ah + bf - kE for k = 0,...,a
 *    (due to the recursive nature of Vakil's formula), <br>
 * 2) all valid tangency conditions alpha and beta (valid = satisfy 
 * I(alpha) + I(beta) = j), <br />
 * 3) all g between arithmetic genus of ih +jf and (arithmetic genus of ih +jf) -gdiff. <br />
 * <p>
 * alpha : tangency condition at assigned points. <br>
 * beta : tangency condition at unassigned points. beta = (beta_1, beta_2,....) <br />
 *  <p>
 * The output numbers will be located at output/Hir <br>
 * <p>
 * Note on algorithm: <br />
 * alpha and beta are stored by ArrayList of Integers. The length of them 
 * (and all alphaP, betaP etc...) are of fixed length which equals
 * maxlength = b + a * n+ gdiff + 1. This is because max number of fiber 
 * class is in (ah+bf)-aE = bf+a*nf = (b+a*n)f and <br />
 * g' = g - |gamma| + 1, => |gamma| = g - g' +1 = gdiff +1 <br />
 * maxlength = |beta'| = |beta+gamma|<= |beta| + |gamma| <= b+a * n+ gdiff +1. 
 * <p>
 * Operations of integer sequences are handled by the binom class, which can 
 * take arrayList of any length, as long as they satisfy required assumptions 
 * (c>=d) for substract(c,d) and binom(c,d).
 * <p>
 * For possble future modification: If alpha and beta (and their variations) 
 * can take different length, ex: (1,2) = (1,2,0,0,..). 
 * Then they have to be trimmed while making key (the key class needs to be 
 * modified). 
 *  <p>
 * @author Yu-jong Tzeng
 * @version 2.1
 * @since August 25, 2019.
 */

public class HirTable {    
    private int n;  
    private int a;
    private int b;         
    private int gdiff;
    private int maxlength;    
    private static ArrayList<Integer> zeros;
    
    private static HashMap<ArrayList<Integer>, Long> table;
    private SeqOp seq;

    /**
     * The constructor of the class.
     * @param n The Hirzebruch surface is F_n, n >= 0. 
     * @param a The number of ample class h in the curve class ah + bf.
     * @param b The number of fiber class h in the curve class ah + bf.
     * @param gdiff The max difference between arithmetic genus and geometric 
     * genus of the curve we'll compute.
     */
    public HirTable(int n, int a, int b, int gdiff) {        
        this.n = n;
        this.a = a;
        this.b = b;
        this.gdiff = gdiff;
        maxlength = b + a * n + gdiff + 1;     
                
        // All binomial coefficients needed will have parameters <= maxlength
        seq = new SeqOp(maxlength);   
        table = new HashMap<ArrayList<Integer>, Long>(); //store computed results
        //create an ArrayList with all zeros of size maxlength
        zeros = new ArrayList<Integer>();   
        for (int i = 0; i < maxlength; i++) {
            zeros.add(0);
        }
    }
    
    /** The main method of the class.
     *  Paramaters a, b, gdiff is initialzed by user input. Then a class
     * with these parameters is constructed and compute() is called.
     *  @param args Unused
     */
    public static void main(String[] args)
    {        
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("This program computes the number of singular" + 
                           "curves on Hirzebrunch surfaces.");
        System.out.println("Notation: F_n = Hirzebruch surface with n>=0,");
        System.out.println("h is the hyperplane class with h^2 = n,");
        System.out.println("f is the fiber class.");
        System.out.println("Please choose the Hirzebruch surface. n = ");
        System.out.println("n = ");
        int n = reader.nextInt();
        System.out.println("Enter the number of ample class a in curve class ah+bf:");        
        System.out.println("a = ");
        int a = reader.nextInt();
        System.out.println("Enter the number of fiber class b in curve class ah+bf:");        
        System.out.println("b = ");
        int b = reader.nextInt();
        System.out.println("Enter the max number of (arithmetic genus - geometric genus):");        
        System.out.println("gdiff = ");
        int gdiff = reader.nextInt();        
        System.out.format("The output will be written in the directory" + 
                           "../output/Hir\n");
        reader.close();
                 
        HirTable T = new HirTable(n, a, b, gdiff);
        T.compute();        
    }    
    /** 
     * Run this method to compute and create output file.  
     */
    private void compute() {
        // just setting the length
        ArrayList<Integer> alphaPP = new ArrayList<>(zeros);         
        ArrayList<Integer> betaPP = new ArrayList<>(zeros);        
                       
        // put N(ah+bf - (a-i)E, all possible alpha and beta) on F_n into dictionary. 
        // Let D = ah+bf - (a-i)E, 
        // D = ah+bf - (a-i)E = ah+bf-(a-i)(h-nf) = ih + (b+n(a-i))f
        // tangency condition satisfy I\alpha + I\beta = D.E = b+n(a-i)
        // for i = 0,...,a, g= (g_a(D)- gdiff) to g_a(D). g_a(D) = arithmetic genus of D
        try {            
            File outputfile = new File("output/Hir/HirTable_n=" + n+"_a=" 
                                + a+"_b=" + b + ".txt");          
            outputfile.getParentFile().mkdirs();
            PrintWriter pw = new PrintWriter(outputfile, "UTF-8");   
            for (int i = 0; i<= a; i++) {
                ArrayList<ArrayList<Integer>> Aj = new ArrayList<ArrayList<Integer>>();
                ArrayList<ArrayList<Integer>> Bj = new ArrayList<ArrayList<Integer>>();
                ablist(b+n*(a-i), alphaPP, betaPP, 0, Aj, Bj); //create all tangency conditions
                for (int g = g_a(n, i, b + n*(a - i)) - gdiff; g <= g_a(n, i, b + n*(a-i)); g++) {
                    for (int j = 0; j < Aj.size(); j++) {
                        long N = N(i, b+ n*(a-i), g, Aj.get(j), Bj.get(j));
                        table.put(Key.make(i, b+ n*(a-i), g ,Aj.get(j), Bj.get(j)), N);
                        pw.printf("N(%dh+%df, %d, ", i, b+ n * (a - i), g);
                        pw.println(Aj.get(j) +", "+ Bj.get(j) + ") = "+ N);
                    }
                }
            }
            pw.close();
        }
        catch (IOException e) {
            System.out.println("There is an error in I/O.");
        }                    
    }
    
    /** 
     * The recursive formula is implemented here. 
     */
    private long N(int a, int b, int g, ArrayList<Integer> alpha, ArrayList<Integer> beta) { //skip n for simpleness
        long ans = 0 ;
        
        // invalid parameters
        if (SeqOp.I(alpha) + SeqOp.I(beta) != b || a < 0 || b < 0 || n < 0) {
            return 0;             
        }
        // Base case. Only fiber class passing through points. 
        else if (a == 0 && SeqOp.sum(beta) == 0) { 
            // from the beginning of Section 8. 
            // These will come before those with beta !=0 in ablist by implementation.  
            if (alpha.get(0) == SeqOp.sum(alpha) && g == 1 - b) return 1; //alpha = (k,0,0,0,....)
            else return 0;            
        }
        
        // the first term, alpha+ e_k, beta - e_k
        // this k is position
        for (int k = 0; k< beta.size(); k++) {    
            if (beta.get(k) > 0){
                ArrayList<Integer> tempAlpha = new ArrayList<Integer>(alpha);
                ArrayList<Integer> tempBeta = new ArrayList<Integer>(beta);
                tempAlpha.set(k, alpha.get(k) + 1);
                tempBeta.set(k, beta.get(k) - 1);                
                ans = ans +(k + 1) * table.get(Key.make(a, b, g, tempAlpha, tempBeta));                   
            }                
        }
        
        if (a > 0) { // the second term
            ArrayList<Integer> alphaP = new ArrayList<>(zeros);
            ArrayList<ArrayList<Integer>> resultAlpha = new ArrayList<ArrayList<Integer>>();
            // generate all alpha' and put it in resultAlpha
            generateA(alpha, alphaP, 0, resultAlpha);    
           
            for(int i = 0; i < resultAlpha.size() ; i++) {
                ArrayList<Integer> Ai = new ArrayList<Integer>(resultAlpha.get(i));                
                ArrayList<Integer> gamma = new ArrayList<>(zeros);
                ArrayList<ArrayList<Integer>> resultGamma = new ArrayList<ArrayList<Integer>>();
                // generate all valid gamma for given alpha = Ai, beta 
                // Satisfy I\alpha + I(beta+gamma) = b+n then put in resultGamma
                generateG(Ai, beta, gamma, 0, b + n, resultGamma);  

                for (int j = 0; j< resultGamma.size(); j++){
                    ArrayList<Integer> gammai = new ArrayList<Integer>(resultGamma.get(j));
                    int gP = g - SeqOp.sum(gammai) + 1;
                    ArrayList<Integer> bP = SeqOp.add(beta, gammai);                   
                    
                    if (gP <= g_a(n, a - 1,b + n) && gP >= g_a(n, a - 1, b + n) - gdiff){  
                        if(table.containsKey(Key.make(a - 1, b + n, gP, Ai, bP))) {
                            ans = ans + SeqOp.J(gammai)*seq.binom(alpha, Ai)*seq.binom(bP, beta)
                                         *table.get(Key.make(a - 1, b + n, gP, Ai, bP));
                        }
                        else {   // Table doesn't contain this term
                            System.out.println("Finding N(" + a + ", "+ b +", " + g + ", "+ alpha + ", " + beta );
                            System.out.format("N(%d, %d, %d, ", a - 1, b + n, gP);
                            System.out.println(Ai + ", " + bP +") can't be found."); 
                        }
                    }       
                }        
            }        
        }
        return ans; 
    }                    
    
    /**
     * This method create all valid tangency condition alpha and beta so that 
     * I(alpha)+I(beta) = b.
     * tempA and tempB are the working arraylist (working on index current). 
     * Once finished working, the result is put in Aj and Bj. 
     * (Aj.get(j), B_j.get(j)) is a pair of valid (alpha, beta).
     */
    private void ablist(int b, ArrayList<Integer> alphaPP, ArrayList<Integer> betaPP, int current, 
                        ArrayList<ArrayList<Integer>> Aj,  ArrayList<ArrayList<Integer>> Bj) {   
        int l = alphaPP.size();
        if (current == 2 * l) {
            if (SeqOp.I(alphaPP)+ SeqOp.I(betaPP) == b) {
                Aj.add(new ArrayList<Integer>(alphaPP));
                Bj.add(new ArrayList<Integer>(betaPP));
                return;
            }         
        }
        if (current <= l - 1) {                
            for(int i = (b - SeqOp.I(alphaPP, current)) / (current + 1); i >= 0; i--) {
                alphaPP.set(current, i);            
                ablist(b, alphaPP, betaPP, current + 1, Aj, Bj);
            }                                    
        }
        if (current >= l  && current <= 2 * l - 1) {
            for(int i = (b - SeqOp.I(alphaPP) - SeqOp.I(betaPP, current - l)) / (current - l + 1); i >= 0; i--) {
                betaPP.set(current - l, i);            
                ablist(b, alphaPP, betaPP, current + 1, Aj, Bj);
            } 
        }        
    }
    
    // Given alpha, find all alphaP with alphaP <= alpha and put the result in resultAlpha   
    private void generateA(ArrayList<Integer> alpha, ArrayList<Integer> alphaP,  int current, 
                          ArrayList<ArrayList<Integer>> resultAlpha) {
        if (current == alpha.size()) {
            resultAlpha.add(new ArrayList<Integer>(alphaP));
            return;
        }
        for (int i = 0 ; i <= alpha.get(current); i++) {
            alphaP.set(current, i);            
            generateA(alpha, alphaP, current + 1, resultAlpha);
        }
    }
    
    private void generateG(ArrayList<Integer> alphaP, ArrayList<Integer> beta, ArrayList<Integer> gamma, int current, 
    int b, ArrayList<ArrayList<Integer>> resultGamma) {
        if(current == 0) {                                       //clear gamma to the zero sequence
            for (int i = 0; i < gamma.size(); i++) {
                    gamma.set(i, 0);
            }
        }
        
        if(current == gamma.size()) {
            if (SeqOp.I(alphaP) + SeqOp.I(beta) + SeqOp.I(gamma) == b) {
                resultGamma.add(new ArrayList<Integer>(gamma));
            }   
            return;
        }
       
        for(int i = (b -  SeqOp.I(alphaP) - SeqOp.I(beta) - SeqOp.I(gamma, current)) / (current + 1); i >= 0; i--) {
            gamma.set(current, i);
            generateG(alphaP, beta, gamma, current + 1, b, resultGamma);
        }
    }  
    /**
     * Compute the arithmetic genus of the curve class ah + bf on F_n.
     */
    private static int g_a(int n, int a, int b) {  
        return (a - 1) * (b - 1)+ a * (a - 1) * n / 2;
    }
}    

