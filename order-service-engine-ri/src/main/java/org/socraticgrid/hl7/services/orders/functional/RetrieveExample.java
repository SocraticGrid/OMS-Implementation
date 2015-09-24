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

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class RetrieveExample {

	/**
	 * args[0] Should be the name of the collection to access args[1] Should be
	 * the name of the resource to read from the collection
	 */
	public static void main(String args[]) throws Exception {
		if (args.length < 2) {
			System.out.println("usage: RetrieveExample collection-path resource-name");
			System.out.println("example: RetrieveExample /db/apps/HeD/modules/data/ books.xml");
			System.exit(1);
		}
		ExistDatabaseManager existDatabaseManager = new ExistDatabaseManager();
		Collection col = null;
		try {
			existDatabaseManager.initializeDatabase();
			col = existDatabaseManager.getCollection(args[0]);
			XMLResource res = null;

			res = (XMLResource) col.getResource(args[1]);

			if (res == null) {
				System.out.println("document not found!");
			} else {
				System.out.println(res.getContent());
			}
		} finally {
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
