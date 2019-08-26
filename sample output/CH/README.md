# How to use

The files in this directory is the output of CH.class. For all i and j, the file O(i)_r=j.txt contains all Caporaso-Harris invariants
on P^2 with degree i and j nodes. 

For i >= 26, the file size is too large so we use the split tool in mac to split the file into several parts by the command
>> split -b 20m "O(30)_r=1.txt" "O(30)_r=1".

or loop 

>> for i in {0..4}; do split -b 20m "O(30)_r=${i}.txt" "O(30)_r=${i}"; done

For example, the O(30)_r=4.txt file is splitted into five files: <br>
O(30)_r=4aa <br>
O(30)_r=4ab <br>
O(30)_r=4ac <br>
O(30)_r=4ad <br>
O(30)_r=4ae

Each of the splitted file still can be read directly from text editor, or can be merged again by
>> cat "O(30)_r=0"?? > "O(30)_r=0.txt"
