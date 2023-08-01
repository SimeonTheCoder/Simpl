# Simpl
A brand new dynamically typed prcoessor shader programming language with no additional overhead from using If-statements and loops.
I created Simpl with the idea of an easily integratable shader language, that can be easily called from many programming languages and without the need for a graphics library and running on a CPU.
To use it, call it trough command prompt:
java -jar simpl.jar -p(your_program.smpl) -a3(var_name,var_x,var_y,var_z) -o(output_picture.extension) -d  -t8
-	Use the -t argument to specify the number of threads to use
-	Use the -a3 and -a2 arguments to inject variables directly (does not require recompiling)
-	Use the -o argument to output the result to an external file. Leave empty
-	Use the -d argument to toggle a display that shows the current progress and the result
Texture files are called directly by file name in the program file itself, requiring no additional texture linking.
All programs should start with the __tex function in order to define the textures you’re using:
```
__tex
{
	tex texture = texture.jpg
	tex texture2 = texture2.jpg _out
}
```
The _out flag at the end specifies the contents of which texture should be saved.
Then, define the __main function and put the global variables in between the square brackets []
```
__main
[

]
{

}
```
Then, end the code with an out command, followed by a variable – it contains the final color of the pixel.
## Variables
Simpl is a dynamically typed language, so the keyword for variable is var. It stores data similar to vec3,vec2 (or float3, float2) and they’re internally represented as vectors. This means that you can perform addition and all operations between 2- and 3- dimensional vectors.
Variables are defined the following way:
```
var test = [1,0.3,4]
var test2 = [0.01,3]
```
In order to access the info from the vectors, you have to use .x, .y and .z respectively.
You can also augment variables together in the following way:
```
[test.x] = [test2.y]
[test2.z] = [a.x]
[a.y] = [test.y]
```
Calculations can also be performed on the variables using the following syntax:
```
varA = varB [sign] varC //[sign] can be +, -, *, /, ^
```
## Labels & Jumping
Simpl makes use of labels & jump commands. You can jump to labels. The syntax for specifying labels is the following:
```
_label
```
Then, in order to jump to the label, you’ll have to use the following syntax:
```
jmp label
```
When you run a jmp command, a pointer is set pointing to the line after the jmp. Then, you can return to that location using the following syntax:
return
## Conditions & Loops
Conditions are a very important part of every programming language. The code that is called when the condition is true or false is not inlined:
if (test.x_<_test2.y) a b
In the previous example, a and b are names of labels. Label a is called when the condition is true, b – when it’s false.
The Boolean operators that are implemented are the following - <, >, ==, !=
You can also inline conditions in the following way:
```
if (test.x_>_test2.y) _a _b
_a
{
        jmp outif
}
_b
{
        jmp outif
}
_outif
```
If you combine an if with jmp commands, you can create while loops:
```
_cycle
{
        // your code

        counter = counter – one
        if (counter.x_!=_zero.x) cycle outcycle
}
_outcycle
```
Functions
The functions behave like pieces of code that we jump to and then return to __main. This is he syntax for functions:
```
__func
{
        //code code codde
        return
}
```
Then, to call them, we can call it by using
jmp _func
In order to use variables in the functions, we have to define them previously and make them global:
```
__tex
{
}

__main
[
        var a = [0,0,0]
]
{
        jmp func
        return
}

__func
{
        a = a + a
        return
}

out a
```
