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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.functional.ExistDatabaseManager;
import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.CatalogItemType;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Period;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

@Repository
public class OrderCatalogDaoImpl implements OrderCatalogDao {

	private final Logger logger = LoggerFactory
			.getLogger(OrderCatalogDaoImpl.class);

	@Autowired
	private ExistDatabaseManager databaseManager;

	@Override
	public List<CatalogItemType> getAllCatalogItemTypes()
			throws XMLDBException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		List<CatalogItemType> catalogItemTypes = new ArrayList<CatalogItemType>();
		Collection col = null;
		try {
			databaseManager.initializeDatabase();
			col = databaseManager.getCollection(null);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			String xPath = "/catalogItemTypes/catalogItemType/text()";
			ResourceSet result = xpqs.query(xPath);
			ResourceIterator i = result.getIterator();
			Resource res = null;
			while (i.hasMoreResources()) {
				res = i.nextResource();
				logger.debug("Type : " + res.getContent());
				CatalogItemType catalogItemType = new CatalogItemType();
				catalogItemType.setType(res.getContent().toString());
				catalogItemTypes.add(catalogItemType);
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
		return catalogItemTypes;
	}

	@Override
	public CatalogItem getItemDetail(Identifier itemId)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, XMLDBException, JAXBException {
		CatalogItem catalogItem = null;
		Collection col = null;
		try {
			databaseManager.initializeDatabase();
			col = databaseManager.getCollection(null);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			String xPath = "/catalogItem[identifier/value='"
					+ itemId.getValue() + "']";
			ResourceSet result = xpqs.query(xPath);
			ResourceIterator i = result.getIterator();
			Resource res = null;
			if (i.hasMoreResources()) {
				res = i.nextResource();
				logger.debug("Catalog Item : " + res.getContent());
				JAXBContext jaxbContext = JAXBContext
						.newInstance(CatalogItem.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext
						.createUnmarshaller();
				InputStream stream = new ByteArrayInputStream(res.getContent()
						.toString().getBytes(StandardCharsets.UTF_8));
				catalogItem = (CatalogItem) jaxbUnmarshaller.unmarshal(stream);
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
		return catalogItem;
	}

	@Override
	public CatalogItem getCatalogItem(Identifier itemId)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, XMLDBException, JAXBException {
		CatalogItem catalogItem = null;
		Collection col = null;
		try {
			databaseManager.initializeDatabase();
			col = databaseManager.getCollection(null);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			String xPath = "/catalogItem[identifier/value='"
					+ itemId.getValue() + "']";
			ResourceSet result = xpqs.query(xPath);
			ResourceIterator i = result.getIterator();
			Resource res = null;
			if (i.hasMoreResources()) {
				res = i.nextResource();
				logger.debug("Catalog Item : " + res.getContent());
				catalogItem = unmarshallCatalogItem(res.getContent().toString());
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
		return catalogItem;
	}

	private CatalogItem unmarshallCatalogItem(String xmlCatalogItem)
			throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(CatalogItem.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		InputStream stream = new ByteArrayInputStream(xmlCatalogItem.toString()
				.getBytes(StandardCharsets.UTF_8));
		CatalogItem catalogItem = (CatalogItem) jaxbUnmarshaller
				.unmarshal(stream);
		return catalogItem;
	}

	@Override
	public UpdateStatus createCatalogItem(CatalogItem catalogItem)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, XMLDBException, JAXBException {
		Collection col = null;
		UpdateStatus status = UpdateStatus.Sucessful;
		try {
			databaseManager.initializeDatabase();
			col = databaseManager.getCollection(null);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			String xPath = "/catalogItem[identifier/value='"
					+ catalogItem.getId().getValue() + "']";
			ResourceSet result = xpqs.query(xPath);
			ResourceIterator i = result.getIterator();
			if (i.hasMoreResources()) {
				status = UpdateStatus.ResourceAlreadyExist;
			} else {
				createOrUpdateCatalogItem(catalogItem);
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
		return status;
	}

	@Override
	public UpdateStatus updateCatalogItem(CatalogItem catalogItem)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, XMLDBException, JAXBException {
		Collection col = null;
		UpdateStatus status = UpdateStatus.Sucessful;
		try {
			databaseManager.initializeDatabase();
			col = databaseManager.getCollection(null);
			XPathQueryService xpqs = (XPathQueryService) col.getService(
					"XPathQueryService", "1.0");
			xpqs.setProperty("indent", "yes");
			String xPath = "/catalogItem[identifier/value='"
					+ catalogItem.getId().getValue() + "']";
			ResourceSet result = xpqs.query(xPath);
			ResourceIterator i = result.getIterator();
			Resource res = null;
			if (i.hasMoreResources()) {
				res = i.nextResource();
				logger.debug("Catalog Item : " + res.getContent());
				CatalogItem oldCatalogItem = unmarshallCatalogItem(res
						.getContent().toString());
				catalogItem
						.getId()
						.setLabel(
								(catalogItem.getId().getLabel() != null && catalogItem
										.getId().getLabel().trim().length() > 0) ? catalogItem
										.getId().getLabel() : oldCatalogItem
										.getId().getLabel());
				catalogItem
						.getId()
						.setUse((catalogItem.getId().getUse() != null && catalogItem
								.getId().getUse().trim().length() > 0) ? catalogItem
								.getId().getUse() : oldCatalogItem.getId()
								.getUse());
				catalogItem
						.getId()
						.setSystem(
								(catalogItem.getId().getSystem() != null && catalogItem
										.getId().getSystem().trim().length() > 0) ? catalogItem
										.getId().getSystem() : oldCatalogItem
										.getId().getSystem());
				if (catalogItem.getId().getPeriod() != null) {
					catalogItem
							.getId()
							.getPeriod()
							.setStart(
									catalogItem.getId().getPeriod().getStart() != null ? catalogItem
											.getId().getPeriod().getStart()
											: (oldCatalogItem.getId()
													.getPeriod() != null ? oldCatalogItem
													.getId().getPeriod()
													.getStart()
													: null));
					catalogItem
							.getId()
							.getPeriod()
							.setEnd(catalogItem.getId().getPeriod().getEnd() != null ? catalogItem
									.getId().getPeriod().getEnd()
									: (oldCatalogItem.getId().getPeriod() != null ? oldCatalogItem
											.getId().getPeriod().getEnd()
											: null));
				} else {
					Period period = new Period();
					period.setStart(oldCatalogItem.getId().getPeriod() != null ? oldCatalogItem
							.getId().getPeriod().getStart()
							: null);
					period.setEnd(oldCatalogItem.getId().getPeriod() != null ? oldCatalogItem
							.getId().getPeriod().getEnd()
							: null);
					catalogItem.getId().setPeriod(period);
				}
				createOrUpdateCatalogItem(catalogItem);
			} else {
				status = UpdateStatus.ResourceDoesNotExist;
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
		return status;
	}

	private void createOrUpdateCatalogItem(CatalogItem catalogItem)
			throws XMLDBException, JAXBException {
		Collection col = null;
		XMLResource res = null;
		try {
			col = databaseManager.getOrCreateCollection(null);

			// create new XMLResource; an id will be assigned to the new
			// resource
			String id = catalogItem.getId().getValue();
			if (id == null || id.trim().length() <= 0) {
				id = null;
			} else {
				if (!id.endsWith(".xml")) {
					id += ".xml";
				}
			}
			res = (XMLResource) col.createResource(id, "XMLResource");
			if (id == null || id.trim().length() <= 0) {
				catalogItem.getId().setValue(
						res.getId().substring(0,
								res.getId().lastIndexOf(".xml")));
			}

			// Marshaling POJO into XML
			JAXBContext jaxbContext = JAXBContext
					.newInstance(CatalogItem.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(catalogItem, arrayOutputStream);
			String xmlCatalogItem = arrayOutputStream.toString();

			// Saving into Exist-DB
			res.setContent(xmlCatalogItem);
			logger.debug("Storing document " + res.getId() + "...");
			col.storeResource(res);
			logger.debug("ok.");
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

	@Override
	public UpdateStatus deleteCatalogItem(Identifier itemId) throws IOException {
		UpdateStatus status = UpdateStatus.Sucessful;
		if (!databaseManager.deleteDatabaseResource(itemId.getValue())) {
			status = UpdateStatus.ResourceDoesNotExist;
		}
		return status;
	}
}
