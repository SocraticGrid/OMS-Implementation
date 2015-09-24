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

import java.io.File;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class CreateUpdateExample {

	/**
	 * args[0] Should be the name of the collection to access args[1] Should be
	 * the name of the file to read and store in the collection
	 */
	public static void main(String args[]) throws Exception {

		if (args.length < 2) {
			System.out.println("usage: CreateUpdateExample collection-path document");
			System.out.println("example: CreateUpdateExample /db/apps/HeD/modules/data/ D://StoreData.xml");
			System.exit(1);
		}
		ExistDatabaseManager existDatabaseManager = new ExistDatabaseManager();
		Collection col = null;
		XMLResource res = null;
		try {
			existDatabaseManager.initializeDatabase();
			col = existDatabaseManager.getOrCreateCollection(null);

			// create new XMLResource; an id will be assigned to the new
			// resource
			res = (XMLResource) col.createResource(null, "XMLResource");
			File f = new File(args[1]);
			if (!f.canRead()) {
				System.out.println("Cannot read file " + args[1]);
				return;
			}

			res.setContent(f);
			System.out.print("Storing document " + res.getId() + "...");
			col.storeResource(res);
			System.out.println("ok.");
		} finally {
			// dont forget to cleanup
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

}
