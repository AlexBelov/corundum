##Corundum language

Ruby-like language for Parrot VM. Compiler build with ANTLR4.  
Moved from [old repo](https://github.com/AlexBelov/ruby-antlr4/tree/master/generator)  
Next step - compilation into deb package and maybe adding object oriented features  

### Prerequisites

1. [ANTLR4](http://www.antlr.org/)
2. [Parrot VM](https://github.com/parrot/parrot)

### Running Corundum

``` bash
scripts/build.sh
./cor test/lab4.rb
```