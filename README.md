# Caporaso-Harris
This project implements the recursive formulas of Caporaso-Harris and Vakil to compute the number of nodal curves satisfying given tangency conditions with a line on the projective plane or Hirzebruch surfaces. 

## Table of contents
* [Installing](#installing)
* [How to use][how-to-use]
*

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

The documentation is under [doc](/docs) directory.

### Tests

Sample output file is provided under /sample output directory. If they match with your output file it can be almost sure your program is working fine.

## Versioning

Version 3. 

## Authors

* **Yu-jong Tzeng** 

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details


