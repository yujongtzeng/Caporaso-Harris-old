/** 
 * The arrayOP class does operations on arrays of integers. 
 * <p>
 * The input and returned values for each methods are integers or int[] of 
 * fixed length. Error message will be 
 * thrown if the length of input is incorrect.
 * <p>
 * All components of the input are assumed to be nonnegative integers. 
 * All components of the output will be nonnegative integers too. 
 * For substract and binom, we check first that the first input is 
 * greater or equal to the second input so the operation works in the 
 * naive way. 
 * 
 * @author Yu-jong Tzeng
 * @version 1.1.1
 * @since June 10, 2019.
 */

public class arrayOP
{    
    private static long[][] C;
    private static int length;
    
    /** 
     * Before doing any operations, the class builds a table of all 
     * binomial coefficients with m (<= n) choose k 
     * elements for any k. n is the required length for inputs. 
     * 
     * @param n The max parameter can be taken for binomial coefficients.
     * It is also the required length for inputs and outputs. 
     */
    public arrayOP(int n) {
        length = n;
        C = new long[n + 1][n + 1];
        
        C[0][0] = 1;
        for (int j = 1; j <= n; j++) {
            C[0][j] = 0;
            C[j][0] = 1;
        }    
        
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (j > i) {
                    C[i][j] = 0;
                }
                else {
                    C[i][j] = C[i - 1][j - 1] + C[i - 1][j];
                }
            }
        }
    } 
    
    /** 
     * Return true if c >= d.
     * @param c int[]
     * @param d int[]
     * @return True if c.length == d.length and c[i] >= d[i] for all i. 
     */
    public boolean greater(int[] c, int[] d) {
        checkLength(c);
        checkLength(d);
        if (!checkLength(c) || !checkLength(d)) return false;
        
        // The length of c and d are both length
        for (int i = 0; i < length; i++) {
            if (c[i] < d[i]) return false;        
        }
        return true;
    }
    /**
     * Return True if the length of c equals length.
     */    
    private boolean checkLength(int[] c) {
        if (c.length != length) {
            System.out.println("The length of " + c + " should = " + length);
        }
        return c.length == length;
    } 
    
    /**
     * Add two arrays componentwisely.
     * @param c int[]
     * @param d int[]
     * @return The componentwise sum of the inputs. 
     */
    public int[] add(int[] c, int[] d) {        
        checkLength(c);
        checkLength(d);
        if (!checkLength(c) || !checkLength(d)) return new int[length];
        
        int[] ans = new int[length];        
        for (int i = 0; i < length; i++) {
            ans[i] = c[i] + d[i];      
        }     
        return ans;
    }  

    /**
     * Substract the second input from the first input. Every component 
     * of the first input must be great or equal to the second input. 
     * @param c int[]
     * @param d int[]
     * @return The componentwise difference of the inputs. Send error 
     * message if the length of c and d are not equal to n.
     */
    public int[] substract(int[] c, int[] d) {        
        if (!greater(c, d)) {
            System.out.println(c + " must be greater then " + d);      
            return new int[length];
        } 
        
        // Now  c>=d, so we only have to do c_i -d_i for i = 0,....length-1
        int[] ans = new int[length];  
        for (int i = 0; i < length; i++) {
            ans[i] = c[i] - d[i];
        }  
        return ans;   
    } 
    
    /**
     * Compute Ic. 
     * @param c int[]
     * @return If c = {c1, c2, c3,..},  return 1* c1 + 2* c2 + 3 * c3 + ....
     */
    public int I(int[] c)   
    {
        if (!checkLength(c)) return 0;
        int ans = 0;        
        for (int i = 0; i < length; i++) {
            ans = ans + (i + 1) * c[i];
        }        
        return ans; 
    }    
    
    /**
     * Compute Ic only to the given index.
     * @param c int[]
     * @param index int
     * @return If c = {c1, c2, c3,..},  
     * return 1* c1 + 2* c2 + 3 * c3 + .... + index * cindex.
     */
    public int I(int[] c, int index)    
    {
        int ans = 0;        
        for (int i = 0; i < index; i++)
        {
            ans = ans + (i + 1) * c[i];
        }        
        return ans; 
    }
    
     /**
     * Compute I^c.
     * @param c int[]
     * @return If c = {c1, c2, c3,..},  
     * return 1^(c1) + 2^(c2) + 3^(c3) + .... 
     */
    public long J(int[] c)    
    {
        if (!checkLength(c)) return 0;
        long ans = 1;        
        for (int i = 0; i < length; i++)
        {
            ans = ans * ((long) Math.pow(i + 1, c[i]));
        }        
        return ans; 
    }
    
    /**
     * Compute |c|.
     * @param c int[]
     * @return If c = {c1, c2, c3,..}, return the sum of all ci. 
     */
    public int sum(int[] c)   
    {
        int ans = 0;        
        for (int i = 0; i < length; i++) {
            ans = ans + c[i];
        }        
        return ans; 
    } 
    
    /**
     * Product of componentwise binomial coefficients. Every component 
     * of the first input must be greater or equal to the second input. 
     * @param c int[]
     * @param d int[]
     * @return The product of all C[ci][di] (C[n][k] is the binomial 
     * coeffient).  
     */
    public long binom(int[] c, int[] d) 
    {
        long ans = 1;
        // Send error message if c is not >= d
        if (!greater(c, d)) {
            System.out.println(c + " must be greater than " + d);
            return 0;        
        }
        
        // Now c >=d, only need to multiply C[c_i][d_i] in their common range
        // Outside common range, d_i must be zero. C gives 1.        
        for (int i = 0; i < length; i++) {
            if (c[i] > length) {
                System.out.println(c + " is too big." +  
                           "       Please initiate a larger object.");
                return 0;
            }
            ans = ans * C[c[i]][d[i]];
        }          
        return ans; 
    }    
}


