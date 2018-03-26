# nell2rdf


NELL2RDF is a tool to convert NELL data and metadata into RDF using different annotation approaches. It takes as input the ontology and data files provided by NELL (http://rtw.ml.cmu.edu/rtw/resources) and produces as output separated files for them.

## Compiling

In order to compile NELL2RDF into an executable jar file run `mvn clean package`. It will create the file `NellConverter-0.0.1-SNAPSHOT.jar` in the *target* folder.

## Usage

Once compiled, run the executable jar file using the following command:

```
java -jar NellConverter-0.0.1-SNAPSHOT.jar FORMAT MODEL IONTOLOGY OONTOLOGY IDATA ODATA DELETE 
```

Where each parameter stands for the following:

```
FORMAT		- Desired serialization format: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", "TTL", "N3"
MODEL		- Desired model to represent meta-data: "none", "reification", "n-ary", "quads", "singleton-property"
IONTOLOGY	- Absolute path to nell csv ontology file. E.g. "./NELL.08m.1075.ontology.csv"
OONTOLOGY	- Absolute path where to serialize the RDF model. E.g. "./NELL.08m.1075.ontology.ttl"
IDATA		- Absolute path to nell csv instance file. E.g. "./NELL.08m.1070.cesv.csv"
ODATA		- Absolute path where to serialize the RDF instances. E.g. "./NELL.08m.1070.cesv.ttl"
DELETE		- Where to delete original triple (optional, true by default, not applicable for quads)
```

In order to facilitate the automatic generation of the data we include a bash script that takes as input the paths to the ontology, promoted, and candidates files and generates the output files for the three of them using all annotation models. The file can be found in `src/main/resources/nell2rdf.sh`. To run it use the following command: 


```
./nell2rdf.sh [OPTION]* ONTOLOGY PROMOTED CANDIDATES

  OPTIONS:
    -c, --compress  compress the output in the desired format (gzip|bzip|lzop)
    -f, --format    serialization format to encode the RDF triples (default N-TRIPLES)
                            (RDF/XML | RDF/XML-ABBREV | N-TRIPLE | TURTLE | TTL | N3 )
    -h, --help      shows this help and exists
    -j, --jar       path to NELL2RDF java jar file (default $jarFile)
    -p, --params    parameters for java command (i.e., -Xmx10g)
    
  ONTOLOGY    path to NELL's ontology file
  PROMOTED    path to NELL's promoted beliefs file
  CANDIDATES  path to NELL's candidates file