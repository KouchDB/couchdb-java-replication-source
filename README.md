# Testing procedure

1. Install CouchDB on localhost:5984. Details outside the scope of this document.

2. Launch the demo (main: com.kouchdb.dynamodb_source.AppLauncher)

3. Execute the replication command below (be sure to replace the 198.168.*.* to your local IP address and PASSWORD):

```
curl -X POST http://192.168.1.77:5984/_replicate  -d '{"source":"http://192.168.1.77:8080/mock_db13", "target":"http://dbadmin:PASSWORD@192.168.1.77:5984/mock_replica13", "create_target": true}' -H "Content-Type: application/json"
```

# Expected result:

```
{"ok":true,"session_id":"fbd317016a8d61d337230ead63b8bc20","source_last_seq":"1-a11f390ffa77a03c557ffbbc7c5fda75","replication_id_version":4,"history":[{"session_id":"fbd317016a8d61d337230ead63b8bc20","start_time":"Sun, 06 Nov 2022 20:10:03 GMT","end_time":"Sun, 06 Nov 2022 20:10:04 GMT","start_last_seq":0,"end_last_seq":"1-a11f390ffa77a03c557ffbbc7c5fda75","recorded_seq":"1-a11f390ffa77a03c557ffbbc7c5fda75","missing_checked":1,"missing_found":0,"docs_read":0,"docs_written":0,"doc_write_failures":0}]}
```

Browse to http://localhost:5984/_utils/#database/mock_replica13/id-1

Expect to see:

```
{
  "_id": "id-1",
  "_rev": "1-abcd",
  "some_data": "here"
}
```
