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
package org.socraticgrid.hl7.services.orders.dao;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.CatalogItemType;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.xmldb.api.base.XMLDBException;

public interface OrderCatalogDao {

	List<CatalogItemType> getAllCatalogItemTypes() throws XMLDBException, InstantiationException, IllegalAccessException, ClassNotFoundException;

	CatalogItem getItemDetail(Identifier itemId) throws InstantiationException, IllegalAccessException, ClassNotFoundException, XMLDBException, JAXBException;
	
	CatalogItem getCatalogItem(Identifier itemId) throws InstantiationException, IllegalAccessException, ClassNotFoundException, XMLDBException, JAXBException;

	UpdateStatus createCatalogItem(CatalogItem catalogItem) throws InstantiationException, IllegalAccessException, ClassNotFoundException, XMLDBException, JAXBException;

	UpdateStatus updateCatalogItem(CatalogItem catalogItem) throws InstantiationException, IllegalAccessException, ClassNotFoundException, XMLDBException, JAXBException;

	UpdateStatus deleteCatalogItem(Identifier itemId) throws IOException;

}
