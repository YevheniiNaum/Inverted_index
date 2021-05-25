# jeniknaum5/course_work_parallel_computing (Inverted Index)

---
## The purpose of the course work
The task is to make a program that implements the construction of an inverted index, and also to make it possible to use this index
### Subtasks:

- When building, you should be able to speed up the solution time by varying the number of threads.

- When building and using an index, you need to use a data structure with parallel access, while accessing it in multiple streams.

- At the same time it is necessary to address this structure from various processes, using network sockets.
---
## Installation

Clone repository
```sh
git clone https://github.com/jeniknaum5/course_work_parallel_computing.git
```
### Pre setting:

- Delete file ".gitkeep" in the "data" folder.
- Place the folders with text data in the "Data" folder.
- Open "src" folder  and open ***"Indexer"*** file using any text editor:
- - In the constructor of the ***NUMBER_THREADS*** variable, set a number that is greater than or equal to 1 (This variable is responsible for the number of threads that will build the index) 
- - In the ***InitFolders()*** method, write the paths to your folders with text data using the command line syntax of your operating system
- - In the constructor of the class itself, also write the path to the file with stop words also using the command line syntax of your operating system
    
### Compilation:

- Open folder with code
````sh
cd "Path to project"/src
````

- Compile all files
````sh
javac -d ../out/production/course_work_parallel_computing *
````
---

## Launching the program

- Open folder with classes

````sh
cd ../out/production/course_work_parallel_computing
````

- Starting the application "Server"
````sh
java Server
````
- Starting the apllication "Client" (there is a possibility of several)
````sh
java Client
````
