# Caporaso-Harris
This project implements the recursive formulas of Caporaso-Harris and Vakil to compute the number of nodal curves satisfying given tangency conditions with a line on the projective plane or Hirzebruch surfaces. 

The recursive formulae can be found here:
* [Caporaso-Harris](https://arxiv.org/pdf/alg-geom/9608025.pdf) Theorem 1.1
* [Vakil](https://link.springer.com/article/10.1007/s002291020053) Theorem 6.12

### Table of contents
* [Installing](#installing)
* [How to use](how-to-use)
* [API Reference](api-reference)

### Features
We improved the naive recursive algorithm by:
* using dynamic approach
* limiting the number of nodes in a fixed range chosen by users
* testing pre-generated partitions alpha' and beta' for the second term instead of generating valid alpha' and beta' every time
* finding effective range for the second term in recursive formulae

### Installing
No installation is needed.

### How to use
For computation on the projective plane,
```
$ javac CH.java
$ java CH
```
The program will show you instructions to enter inputs and the location for output. It will print the number of nodal curves as well as their first terms for generating functions in the output files. 

The CH class is for computations on the projectives planes. F0Table and HirTable are for the product of two projecive lines and any Hirzebruch surfaces respectively. F0Table and HirTable can be run in the same way. 

### API Reference

The documentation is under [doc](/doc) directory.

### Tests

Sample output files are provided under [sample output](/sample%20output) directory. If they match with your output file it can be almost sure your program is working fine.

### Technologies
Java 8

### Versioning

Version 3. 

### Authors

* **Yu-jong Tzeng** 

### License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details


