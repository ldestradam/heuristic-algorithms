# heuristic-algorithms
***
Heuristic algorithms implementation for solve VRP. 
The application takes as input csv files with the information of the nodes and edges to produce optimal routes. Also, if specified, creates files of nodes and edges corresponding to the generated routes for display in the [Gephi](https://gephi.org/) application. 
The algorithms implemented in the application are:

1. [Artificial bee colony algorithm](https://en.wikipedia.org/wiki/Artificial_bee_colony_algorithm)
2. [Particle swarm optimization](https://en.wikipedia.org/wiki/Particle_swarm_optimization)
3. [Genetic algorithm](https://en.wikipedia.org/wiki/Genetic_algorithm)

## Usage
The application is a command line interface, its execution is through the command line.  
### Basic usage
The minimum instruction considers the algorithm to use:
```
java -jar application.jar algorithm=[genetic|abc|pso]
```
### Optional parameters
The optional parameters correspond to the file directory of input or output nodes and edges, operation of algorithms, etc.  
> Most of the parameters are defined in the properties file, so you can assign their values in the file before building the application or through the command line.  

General parameters

	file-path			#Output file directory.
	--input-file.nodes		#Nodes input file.
	--input-file.edges  	#Edges input file.

Algorithm parameters

	--params.fleet-capacity		#Vehicle capacity  
	--params.capacity-penalty	#Capacity restriction penalty

Genetic parameters

	--params.genetic.num-generations  	#Number of generations
	--params.genetic.population-size  	#Population size
	--params.genetic.mutation-rate  	#Mutation rate
	--params.genetic.crossover-rate  	#Crossover rate

ABC parameters

	--params.abc.food-source-size	#Food source size  
	--params.abc.onlookers-bees  	#Number of onlookers bees = Number of employed bees
	--params.abc.improved-limit  	#Limit of iterations where the solution does not show improvement
	--params.abc.num-iterations  	#Number of iterations

PSO parameters

	--params.pso.particles			#Number of particles
	--params.pso.num-iterations		#Number of iterations
	--params.pso.inertia			#Inertia
	--params.pso.acceleration-c1		#Acceleration constant 1
	--params.pso.acceleration-c2		#Acceleration constant 2

## Input files
A node file and an edge file must be provided for it to work correctly. Both are CSV files.  
### Nodes file
The CSV file of nodes must have the following structure:

	"Id","Label","Quantity"
	"1","Node 1","15"
	"2","Node 2","23"
	"3","Node 3","38"
	"4","Node 4","11"

### Edges file
The CSV file of edge must have the following structure:

	"Source","Target","Weight"
	"1","2","124378"
	"1","3","23502"
	"1","4","163895"
	"1","5","78389"

> Source and target must correspond to the values in the Id column of the nodes file.

## Output files
A CSV file of edges will be created for each path (solution) generated, which together with the input nodes file can be imported into Gephi to display as a graph.

