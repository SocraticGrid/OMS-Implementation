/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.socraticgrid.hl7.services.orders.functional;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.utils.ReadConfigurationFile;
import org.springframework.stereotype.Component;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

@Component
public class ExistDatabaseManager {

	private final Logger logger = LoggerFactory
			.getLogger(ExistDatabaseManager.class);

	private static String URI = "xmldb:exist://192.168.1.180:8081/exist/xmlrpc";
	private static String COLLECTION = "/db/apps/HeD/modules/data/";
	private static String REST_URL = "http://192.168.1.180:8081/exist/rest";
	private final String USER_AGENT = "Mozilla/5.0";

	private static final Properties props = ReadConfigurationFile
			.getProperties("exist-db.properties");

	public void initializeDatabase() throws XMLDBException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final String driver = "org.exist.xmldb.DatabaseImpl";

		// Getting Exist DB properties
		if (props.getProperty("url") != null) {
			URI = props.getProperty("url");
		}

		// initialize database driver
		@SuppressWarnings("unchecked")
		Class<Database> cl = (Class<Database>) Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
	}

	public Collection getCollection(String collection) throws XMLDBException {
		// get the collection
		if (collection == null || collection.trim().length() <= 0) {
			if (props.getProperty("collection") != null) {
				COLLECTION = props.getProperty("collection");
			}
			collection = COLLECTION;
		}
		Collection col = DatabaseManager.getCollection(URI + collection);
		col.setProperty(OutputKeys.INDENT, "yes");
		return col;
	}

	public Collection getOrCreateCollection(String collection)
			throws XMLDBException {
		return getOrCreateCollection(collection, 0);
	}

	private Collection getOrCreateCollection(String collectionUri,
			int pathSegmentOffset) throws XMLDBException {
		if (collectionUri == null || collectionUri.trim().length() <= 0) {
			if (props.getProperty("collection") != null) {
				COLLECTION = props.getProperty("collection");
			}
			collectionUri = COLLECTION;
		}
		Collection col = DatabaseManager.getCollection(URI + collectionUri);
		if (col == null) {
			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}
			String pathSegments[] = collectionUri.split("/");
			if (pathSegments.length > 0) {
				StringBuilder path = new StringBuilder();
				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}
				Collection start = DatabaseManager.getCollection(URI + path);
				if (start == null) {
					// collection does not exist, so create
					String parentPath = path
							.substring(0, path.lastIndexOf("/"));
					Collection parent = DatabaseManager.getCollection(URI
							+ parentPath);
					CollectionManagementService mgt = (CollectionManagementService) parent
							.getService("CollectionManagementService", "1.0");
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parent.close();
				} else {
					start.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
		} else {
			return col;
		}
	}

	public boolean deleteDatabaseResource(String resourceId) throws IOException {
		boolean deleted = true;
		if (props.getProperty("restUrl") != null) {
			REST_URL = props.getProperty("restUrl");
		}
		if (props.getProperty("collection") != null) {
			COLLECTION = props.getProperty("collection");
		}
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						props.getProperty("username"), props.getProperty(
								"password").toCharArray());
			}
		});
		String url = REST_URL + COLLECTION + resourceId + ".xml";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// setting Request method to Delete (!Important)
		con.setRequestMethod("DELETE");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		logger.debug("\nSending 'DELETE' request to URL : " + url);
		logger.debug("Response Code : " + responseCode);
		if (responseCode >= 400) {
			deleted = false;
		}
		return deleted;
	}
}
