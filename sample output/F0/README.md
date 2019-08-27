# How to use

The files in this directory is the output of F0table.java. For all i, j and k, the file O(i,j)_r=k.txt contains all numbers of genus k 
curves in |O(i,j)| on P^1*P^1.  

Some of files are too large so we use the split tool in mac to split the file into several parts by the command
>> split -b 20m "O(24, 27)_g=594.txt" "O(24, 27)_g=594".

or loop 

>> for i in {594..597}; do split -b 20m "O(24, 27)_g={i}.txt" "O(24, 27)_g={i}"; done

For example, the O(24, 27)_g=594.txt file is splitted into five files: <br>
O(24, 27)_g=594aa <br>
O(24, 27)_g=594ab <br>
O(24, 27)_g=594ac <br>
O(24, 27)_g=594ad <br>
O(24, 27)_g=594ae

Each of the splitted file still can be read directly from text editor, or can be merged again by
>> cat "O(24, 27)_g=594"?? > "O(24, 27)_g=594.txt"
