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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.model.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.SubjectModel;
import org.socraticgrid.hl7.services.orders.model.operationresults.CreateOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.CollectionRequirement;
import org.socraticgrid.hl7.services.orders.model.types.order.LabOrder;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.LabOrderDetail;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.LabOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Test-OrderManagementServiceImplTest.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderManagementServiceImplTestIT {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("OrderManagementServiceImpl")
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

	@Test
	public void testCreateMessageLogging() {
	
		LabOrder labOrder = createPrototypeLabOrder();
		OrderModel<LabOrder> order = new OrderModel<LabOrder>(labOrder);
		try {
			CreateOrderResult result = service.createOrder(order);
			assertNotNull(result);

		} catch (OrderingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}


	}
	public LabOrder createPrototypeLabOrder()
	{
		LabOrder out = new LabOrder();
		
		//Entered By
		Identifier enteredBy = new Identifier();
		enteredBy.setLabel("The Tester");
		enteredBy.setSystem("test");
		enteredBy.setUse("test");
		enteredBy.setValue("tester1");	
		out.setOrderEnteredBy(enteredBy);
		
		//Clinical Ordered By
		ClinicalPractitioner cp = new ClinicalPractitioner();
		cp.setId(enteredBy);
		cp.setName("The Tester");
		out.setOrderedBy(cp);
		
		//Subject
		SubjectModel sm = new SubjectModel();
		Patient pt = new Patient();
		pt.setName("Test Name");
		Identifier ptId = new Identifier();
		ptId.setValue("101010");
		ptId.setLabel("Test patient");
		ptId.setSystem("Test");
		pt.setIdentity(ptId);
		sm.setSubject(pt);
		out.setSubjectdetails(sm);
		
		//Order time.
		out.setOrderTime(new Date());
		
		//Order Details
		LabOrderDetail details = new LabOrderDetail();
		Code labCd = new Code();
		labCd.setCode("20202");
		labCd.setCodeSystem("Test");
		details.setLab(labCd);
		out.setOrderdetails(details);
		//Ordered Items
		LabOrderItem item = new LabOrderItem();
		item.setType(2);
		
		CollectionRequirement req = new CollectionRequirement();
		req.setId("9999");

		out.getRequirements().add(req);
		out.getOrdereditems().add(item);
		
		return out;
	}
}
