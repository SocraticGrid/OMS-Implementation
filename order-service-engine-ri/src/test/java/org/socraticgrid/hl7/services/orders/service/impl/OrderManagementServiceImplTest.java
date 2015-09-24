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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.functional.EventLogger;
import org.socraticgrid.hl7.services.orders.functional.OrderDispatcher;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.logging.EventLevel;
import org.socraticgrid.hl7.services.orders.logging.LogEntryType;
import org.socraticgrid.hl7.services.orders.model.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.SubjectModel;
import org.socraticgrid.hl7.services.orders.model.operationresults.CancelOrderResult;
import org.socraticgrid.hl7.services.orders.model.operationresults.ChangeOrderResult;
import org.socraticgrid.hl7.services.orders.model.operationresults.CreateOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.CollectionRequirement;
import org.socraticgrid.hl7.services.orders.model.status.CancellationStatus;
import org.socraticgrid.hl7.services.orders.model.status.ChangeStatus;
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
public class OrderManagementServiceImplTest {
	@Autowired
	private ApplicationContext applicationContext;

	
	@Autowired
	@Qualifier("OrderManagementServiceImpl")
	@InjectMocks
	private OrderManagementIFace service;

	// @Mock(name = "dlvMgr") DeliveryManager deliveryMgr;
	@Mock(name = "eventLogger")
	EventLogger mockEventLogger;
	
	@Mock(name = "Dispatcher")
	OrderDispatcher dispatcher;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateLabOrder() {
		Code labCd = new Code();
		labCd.setCode("20202");
		labCd.setCodeSystem("Test");
		LabOrder labOrder = createPrototypeLabOrder(labCd);

		OrderModel<LabOrder> order = new OrderModel<LabOrder>(labOrder);
		try {
			CreateOrderResult result = service.createOrder(order);
			assertNotNull(result);
			// Make sure it is calling event logger
			verify(mockEventLogger).logSummaryEvent(
					eq(LogEntryType.User_CreateOrder), eq(EventLevel.info),
					any(String.class), eq("Create Order"), any(String.class));
		} catch (OrderingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyQueryReturn() {
		Subject unkSubject = new Patient();

		List<OrderSummary> result = service.queryOrders(unkSubject);
		assertNotNull("Query for orders returned a null when a Empty list is expected",result);
		assertTrue("Query for Orders list has contents for a unknown subject",result.isEmpty());

	}

	@Test
	public void testNonExistentOrder() {
		Identifier unkId = new Identifier();

		OrderModel<? extends Order> result = service.retrieveOrder(unkId);

		assertNull("Expected a null return order when a unknown id is used",
				result);

	}

	@Test
	public void testNonExistentResults() throws OrderingException {
		Identifier unkId = new Identifier();

		List<Result> result = service.retrieveResults(unkId);

		assertNotNull(
				"Result list returned as Null when it should be an empty list",
				result);

	}

	public LabOrder createPrototypeLabOrder(Code labCd)
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

	@Test
	public void testChangeOrder() throws Exception {
		Code labCd = new Code();
		labCd.setCode("10001");
		labCd.setCodeSystem("Test_Create");
		LabOrder labOrder = createPrototypeLabOrder(labCd);

		OrderModel<LabOrder> order = new OrderModel<LabOrder>(labOrder);
		try {
			CreateOrderResult result = service.createOrder(order);
			assertNotNull(result);
			// Make sure it is calling event logger
			verify(mockEventLogger).logSummaryEvent(
					eq(LogEntryType.User_CreateOrder), eq(EventLevel.info),
					any(String.class), eq("Create Order"), any(String.class)); 
			
			OrderModel<? extends Order> result1 = service.retrieveOrder(labOrder.getOrderIdentity());
			LabOrderDetail details = (LabOrderDetail) result1.getType().getOrderDetails();
			Assert.assertEquals("Test_Create", details.getLab().getCodeSystem());
			Assert.assertEquals("10001", details.getLab().getCode());
			
			labCd = new Code();
			labCd.setCode("20001");
			labCd.setCodeSystem("Test_Update"); 
			((LabOrderDetail)result1.getType().getOrderDetails()).setLab(labCd);
			
			ChangeOrderResult changeResult = new ChangeOrderResult();
			changeResult.setStatus(ChangeStatus.Sucessful);
			Mockito.when(dispatcher.changeOrder(labOrder.getOrderIdentity(),result1)).thenReturn(changeResult);
			
			ChangeOrderResult changeResult1 = service.changeOrder(labOrder.getOrderIdentity(), result1); 
			Assert.assertEquals(ChangeStatus.Sucessful, changeResult1.getStatus());
			
		} catch (OrderingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		Code labCd = new Code();
		labCd.setCode("30001");
		labCd.setCodeSystem("Test_Create");
		LabOrder labOrder = createPrototypeLabOrder(labCd);

		OrderModel<LabOrder> order = new OrderModel<LabOrder>(labOrder);
		try {
			CreateOrderResult result = service.createOrder(order);
			assertNotNull(result);
			// Make sure it is calling event logger
			verify(mockEventLogger).logSummaryEvent(
					eq(LogEntryType.User_CreateOrder), eq(EventLevel.info),
					any(String.class), eq("Create Order"), any(String.class)); 

			CancelOrderResult cancelResult = new CancelOrderResult();
			cancelResult.setStatus(CancellationStatus.Sucessful); 
			
			Mockito.when(dispatcher.cancelOrder(labOrder.getOrderIdentity())).thenReturn(cancelResult);
			CancelOrderResult cancelResult1 = service.cancelOrderById(labOrder.getOrderIdentity());   
			Assert.assertEquals(CancellationStatus.Sucessful, cancelResult1.getStatus());
			
		} catch (OrderingException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
