# Coder Application

## Description

Coder app is a simple web tool that lets the user :
- select a file, 
- compress it using arithmetic encoding, 
- download the compressed file
- select a file to decompress
- decompress the selected file
- download the decompressed file

## Build&Run

Install maven and Java 17.

To build the application run

`mvn install`

To run the application run

`mvn spring-boot:run`

Application will be started on port 8080

## Test

Open the app landing page. Select a sample text file to compress and click the compress button. 

A `Download Result` link will be displayed if compression is successful - clicking this link will download the compressed file. Download the file and open it with a text editor - it's a simple text file

Select the compressed file and click the Decompress button. If decompression is successful, the `Download Result` link will be displayed. Download the file and compare it with original input file - it should be a perfect match

## Implementation details

Coder tool exposes the following api endpoints:
- /compress - compresses the provided text file and returns the compressed file id (file is saved to temp directory)
- /decompress - decompresses the provided text file and returns the decompressed file id (file is saved to temp directory)
- /download - downloads the file with the provided ID

Compression algorithm implementation is taken from [here](https://rosettacode.org/wiki/Arithmetic_coding/As_a_generalized_change_of_radix#Java)

Original file cache is calculated using [apache commons codec](https://commons.apache.org/proper/commons-codec/)

Compressed file will have four sections, separated by '.'

     * Section 1 - the arithmetic coding frequency table - list of "charCode:frequency" separated by ','
     * Section 2 - the arithmetic coding power (integer)
     * Section 3 - the compressed content (big integer)
     * Section 4 - the sha1 hex code of the original file content
     
Sample input file

```
some simple test

another test line

does this work
```

Compressed file content

```
32:6,97:1,100:1,101:7,104:2,105:3,10:4,107:1,108:2,109:2,13:4,110:2,111:4,112:1,114:2,115:6,116:6,119:1.31.43953767923303507165188077526816412059590175539700217228266189852.e1830c8d2159b01ce06307355de2c141c4e3c366
```

Decompressed file content is identical to the input file

## Tech stack

- Spring boot - serves the api endpoints and static content
- HTML/JS - static content - frontend is fairly simple

## Future improvements

Current implementations does not pay attention to any of the following:

- performance - input files aren't limited in size (no validation), but compression/decompression will likely fail on larger input files
- input validation - just basic input validation is implemented
- error handling - no particular handling for system level errors like memory/disk issues
- input file is expected to be a plain ascii file - will not support custom file encodings 