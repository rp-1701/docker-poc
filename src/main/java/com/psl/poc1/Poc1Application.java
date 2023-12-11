package com.psl.poc1;

import com.google.cloud.discoveryengine.v1.*;
import com.google.cloud.storage.*;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
public class Poc1Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Poc1Application.class, args);
	}

	@Autowired
	RestTemplate restTemplate;

	String projectId = "661387798358";
	String location = "global";
	String collection = "default_collection";
	String dataStore = "fuel-search-db-01_1698917192044";
//	String document_id = "e75a735243174a3c36763bb2927da20a";
	String document_id = UUID.randomUUID().toString();
	String schemaId = "default_schema";
	String name = "projects/"+ projectId +"/locations/"+location+"/collections/"+collection+"/dataStores/"+dataStore+"/branches/0/documents/" + document_id;

	String fetchData(){
//		String url = "https://developer.nrel.gov/api/alt-fuel-stations/v1/nearest.json?location=LosAngeles%20%26Co&api_key=4vbCYgvks3ENrwIkme7yggy090BXi1Xle7nKAfEO";
		String url = "https://developer.nrel.gov/api/alt-fuel-stations/v1/nearest.json?location=NewYork%20%26Co&api_key=4vbCYgvks3ENrwIkme7yggy090BXi1Xle7nKAfEO";
		String result = restTemplate.getForObject(url, String.class);
		System.out.println(result);
		return result;
	}

	JsonObject convertDataToJson(String jsonData) throws IOException {
		JsonObject structJsonObject = new Gson().fromJson(jsonData, JsonObject.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("id", document_id);
		jsonObject.addProperty("schemaId", schemaId);
		jsonObject.add("structData", structJsonObject);
		jsonObject.addProperty("parentDocumentId", document_id);
		return jsonObject;
	}

	void writeDatatoFile(JsonObject jsonObject, String filePath){

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		try {
			FileWriter fileWriter = new FileWriter(filePath);
			System.out.println(fileWriter);
//			gson.toJson(jsonObject, fileWriter);
			fileWriter.write(jsonObject.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File writing done");
	}

	void uploadFile() throws IOException {
		Storage storage = StorageOptions.getDefaultInstance().getService();

// Create a bucket
		String bucketName = "test-demo-123";
//		Bucket bucket = storage.create(BucketInfo.of(bucketName));

// Upload a blob to the newly created bucket
		BlobId blobId = BlobId.of(bucketName, "jsonData.jsonl");
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		Blob blob = storage.create(blobInfo, Files.readAllBytes(Paths.get("C:\\Users\\rohan_patil\\IWC_poc\\rajan_poc_code\\poc1\\src\\main\\resources\\jsonData.jsonl")));
		System.out.println("File upload done");
		System.out.println(blob);

	}

	public void syncImportDocuments() throws Exception {
		// This snippet has been automatically generated and should be regarded as a code template only.
		// It will require modifications to work:
		// - It may require correct/in-range values for request initialization.
		// - It may require specifying regional endpoints when creating the service client as shown in
		// https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
		String inputUri = "gs://fuel_stations_struct_data/jsonData.jsonl";
		GcsSource gcsSource = GcsSource.newBuilder().addInputUris(inputUri).build();
		String errorFile = "gs://fuel_stations_struct_data/ErrorConfig";
		try (DocumentServiceClient documentServiceClient = DocumentServiceClient.create()) {
			ImportDocumentsRequest request =
					ImportDocumentsRequest.newBuilder()
							.setParent(
									BranchName.ofProjectLocationDataStoreBranchName(
											projectId, location, dataStore, "0")
											.toString())
							.setErrorConfig(ImportErrorConfig.newBuilder().setGcsPrefix(errorFile).build())
							.setGcsSource(gcsSource)
//							.setAutoGenerateIds(true)
//							.setIdField(document_id)
							.build();

			ImportDocumentsResponse response = documentServiceClient.importDocumentsAsync(request).get();
			System.out.println(response);
			System.out.println("Import file operation is completed");
		}
	}

	@Override
	public void run(String... args) throws Exception {
		String filePath = "C:\\Users\\rohan_patil\\IWC_poc\\rajan_poc_code\\poc1\\src\\main\\resources\\jsonData.jsonl";
		String jsonData = fetchData();
		JsonObject dataToWrite = convertDataToJson(jsonData);
		System.out.println(dataToWrite);
		writeDatatoFile(dataToWrite, filePath);
		uploadFile();
		//syncImportDocuments();
		return;
	}
}
