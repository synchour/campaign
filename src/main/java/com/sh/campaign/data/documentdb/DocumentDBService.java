package com.sh.campaign.data.documentdb;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.RequestOptions;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sh.campaign.runtime.kafka.CustomSerializer;
import com.sh.campaign.runtime.kafka.Message;

@Service
//https://github.com/Azure/azure-documentdb-java
public class DocumentDBService {

	// Replace with your DocumentDB end point and master key.
    private static final String END_POINT = "";
    private static final String MASTER_KEY = "";
    
    // Define an id for your database and collection
    private static final String DATABASE_ID = "TestDB2";
    private static final String COLLECTION_ID = "TestCollection";
    
    DocumentClient documentClient;
    
    public DocumentDBService() {
    	documentClient = new DocumentClient(END_POINT, MASTER_KEY, ConnectionPolicy.GetDefault(),
                ConsistencyLevel.Session);
    }
    
    public void createDBCollection(DocumentClient documentClient) throws DocumentClientException {
    	// Define a new database using the id above.
        Database myDatabase = new Database();
        myDatabase.setId(DATABASE_ID);

        // Create a new database.
        myDatabase = documentClient.createDatabase(myDatabase, null)
                .getResource();

        System.out.println("Created a new database:");
        System.out.println(myDatabase.toString());
        
        // Define a new collection using the id above.
        DocumentCollection myCollection = new DocumentCollection();
        myCollection.setId(COLLECTION_ID);

        // Set the provisioned throughput for this collection to be 1000 RUs.
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(1000);

        // Create a new collection.
        myCollection = documentClient.createCollection(
                "dbs/" + DATABASE_ID, myCollection, requestOptions)
                .getResource();

        System.out.println("Created a new collection:");
        System.out.println(myCollection.toString());
    }
    
    @HystrixCommand
    public void writeDocument(String payload) throws JsonProcessingException, DocumentClientException {

		// Create an object, serialize it into JSON, and wrap it into a document.
        Message<String> documentMessage = new Message<String>();
        documentMessage.setPayload("Hello, World");
        String json = CustomSerializer.ObjectToJson(documentMessage);
        Document allenDocument = new Document(json);

        // Create the document.
        allenDocument = documentClient.createDocument(
                "dbs/" + DATABASE_ID + "/colls/" + COLLECTION_ID, allenDocument, null, false)
                .getResource();
    }    
}
