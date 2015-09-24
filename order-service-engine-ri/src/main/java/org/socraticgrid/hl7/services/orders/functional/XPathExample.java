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
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

public class XPathExample {

	/**
	 * args[0] Should be the name of the collection to access args[1] Should be
	 * the XPath expression to execute
	 */
	public static void main(String args[]) throws Exception {
		if (args.length < 2) {
			System.out.println("usage: XPathExample collection-path XPath");
			System.out.println("example: XPathExample /db/apps/HeD/modules/data/ /bookstore/book[price>30.00]");
			System.exit(1);
		}
		ExistDatabaseManager existDatabaseManager = new ExistDatabaseManager();
		Collection col = null;
		try {
			existDatabaseManager.initializeDatabase();
			col = existDatabaseManager.getCollection(args[0]);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			ResourceSet result = xpqs.query(args[1]);
			ResourceIterator i = result.getIterator();
			Resource res = null;
			while (i.hasMoreResources()) {

				res = i.nextResource();
				System.out.println(res.getContent());

			}
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
