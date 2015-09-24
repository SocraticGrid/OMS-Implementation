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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.types.order.MedicationOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Integration.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ServiceBeansCheckIT {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@InjectMocks
	private OrderManagementIFace service;

	//@Mock(name = "dlvMgr")
	//DeliveryManager deliveryMgr;
	//@Mock(name = "eventLogger")
	//EventLogger mockEventLogger;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	
	@Ignore
	@Test
	public void testTestLoad() {
	
		OrderModel<MedicationOrder> order = new OrderModel<MedicationOrder>(new MedicationOrder());
		Identifier id = new Identifier();
		id.setValue("ITTest");
		order.getType().setOrderIdentity(id);
		try {
			service.createOrder(order);
		} catch (OrderingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
