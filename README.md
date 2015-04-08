This project is a URL shorter done using spray and cassandra DB

To work is needed you have cassandra DB installed and added as a environment variable.

Before you run you must execute the two cql scrips in if the folder src/main/resources/cqls.
Run first the keyspaces.cql and then the tables.cql

To run just use the command ./activator run (in linux) or just activator run(in windows) in the root folder of the
project.


after this you can use the following requests:

POST: http://localhost:9000/encurte/url
Content-Type: application/json
Json Example:
{
  "longUrl": "http://www.chaordicsystems.com"
}

the response for this should be something like:

{
  "id": "http://localhost:9000/CJtHNGGHIIGeR"
  "longUrl": "http://www.chaordicsystems.com"
}

and
GET: http://localhost:9000/url?shortUrl=http://localhost:9000/

the response for this should be something like:

{
  id: "http://localhost:9000/CJtHNGGHIIGeR"
  longUrl: "http://www.chaordicsystems.com"
  status: "OK"
}