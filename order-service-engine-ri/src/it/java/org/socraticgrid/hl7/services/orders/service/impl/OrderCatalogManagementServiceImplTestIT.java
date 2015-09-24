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
package org.socraticgrid.hl7.services.orders.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.socraticgrid.hl7.services.orders.interfaces.OrderCatalogManagementIFace;
import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Period;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Test-OrderCatalogManagementServiceImplTest.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderCatalogManagementServiceImplTestIT {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("OrderCatalogManagementServiceImpl")
	@InjectMocks
	private OrderCatalogManagementIFace service;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	private static final String TEST_ITEM_ID = "TestCatalogItem";

	@Test
	public void test_A_getCatalogItemDetail() {
		try {
			Identifier identifier = new Identifier();
			identifier.setValue("catalogitem-3");
			CatalogItem catalogItem = service.getCatalogItem(identifier);
			assertNotNull(catalogItem);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_B_createCatalogItem_Unique() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setValue(TEST_ITEM_ID);
			identifier.setLabel("Effexor");
			identifier.setSystem("Test System");
			identifier
					.setUse("Effexor is used to treat major depressive disorder, anxiety, and panic disorder.");
			Period period = new Period();
			period.setStart(new Date());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.YEAR, 2);
			period.setEnd(calendar.getTime());
			identifier.setPeriod(period);
			catalogItem.setId(identifier);
			UpdateStatus status = service.createCatalogItem(catalogItem);
			assertNotNull(status);
			assertEquals(UpdateStatus.Sucessful, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_C_createCatalogItem_Duplicate() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setValue("catalogitem-3");
			identifier.setLabel("Hydrocortisone sodium succinate - injection");
			identifier.setSystem("Test System");
			identifier
					.setUse("This medication is used to treat various conditions such as severe allergic reactions, arthritis, "
							+ "blood diseases, breathing problems, certain cancers, eye diseases, intestinal disorders, and skin diseases. "
							+ "It decreases your body's natural defensive response and reduces symptoms such as swelling and allergic-type reactions. "
							+ "Hydrocortisone is a corticosteroidentifier hormone (glucocorticoidentifier). "
							+ "This injectable form of hydrocortisone is used when a similar drug cannot be taken by mouth or when a very fast treatment is needed, "
							+ "especially in patients with severe medical conditions.This drug may also be used with other medications as a replacement for certain hormones.");
			catalogItem.setId(identifier);
			UpdateStatus status = service.createCatalogItem(catalogItem);
			assertNotNull(status);
			assertEquals(UpdateStatus.ResourceAlreadyExist, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_D_updateCatalogItem_Existing() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setValue(TEST_ITEM_ID);
			Period period = new Period();
			period.setStart(new Date());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.YEAR, 3);
			period.setEnd(calendar.getTime());
			identifier.setPeriod(period);
			catalogItem.setId(identifier);
			UpdateStatus status = service.updateCatalogItem(catalogItem);
			assertNotNull(status);
			assertEquals(UpdateStatus.Sucessful, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_E_updateCatalogItem0_NonExisting() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setLabel("My Label");
			catalogItem.setId(identifier);
			UpdateStatus status = service.updateCatalogItem(catalogItem);
			assertNotNull(status);
			assertEquals(UpdateStatus.ResourceDoesNotExist, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_F_deleteCatalogItem_Existing() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setValue(TEST_ITEM_ID);
			catalogItem.setId(identifier);
			UpdateStatus status = service.deleteCatalogItem(identifier);
			assertNotNull(status);
			assertEquals(UpdateStatus.Sucessful, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test_G_deleteCatalogItem_NonExisting() {
		try {
			CatalogItem catalogItem = new CatalogItem();
			Identifier identifier = new Identifier();
			identifier.setValue("RandomId");
			catalogItem.setId(identifier);
			UpdateStatus status = service.deleteCatalogItem(identifier);
			assertNotNull(status);
			assertEquals(UpdateStatus.ResourceDoesNotExist, status);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
