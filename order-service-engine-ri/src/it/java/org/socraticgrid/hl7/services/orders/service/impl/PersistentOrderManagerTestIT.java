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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.model.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.Provenance;
import org.socraticgrid.hl7.services.orders.model.SubjectModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Period;
import org.socraticgrid.hl7.services.orders.model.requirements.EndorsementRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.Requirement;
import org.socraticgrid.hl7.services.orders.model.requirements.RequirementStatusCode;
import org.socraticgrid.hl7.services.orders.model.requirements.RequirementType;
import org.socraticgrid.hl7.services.orders.model.types.order.NursingOrder;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.NursingOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Test-PersistentOrderManagerTest.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PersistentOrderManagerTestIT {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("PersistentOrderManager")
	@InjectMocks
	private OrderManagerIFace service;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_saveOrder() {
		try {
			int initialSize = service.getOrderSize();
			OrderModel<NursingOrder> orderModel = new OrderModel<NursingOrder>();
			NursingOrder nursingOrder = new NursingOrder();
			nursingOrder.setOrderedBy(new ClinicalPractitioner(new Identifier(
					"123", "Test Use", "Test Label", "Test System", new Period(
							new Date(), new Date())), "Test Practitioner"));
			nursingOrder.setOrderTime(new Date());
			nursingOrder.setOrderEnteredBy(new Identifier("234",
					"Test Ent Use", "Test Ent label", "Test Ent system",
					new Period(new Date(), new Date())));
			nursingOrder
					.setOrderIdentity(new Identifier("11", "Test Order Id use",
							"Test Order Id label", "Test Order Id system",
							new Period(new Date(), new Date())));
			nursingOrder.setStatus(new Code("SUCCESS", "Test System",
					"Success", "Success"));
			SubjectModel subjectModel = new SubjectModel();
			Patient patient = new Patient();
			patient.setDateOfBirth(new Date());
			patient.setGender(new Code("male", "Gender", "Male", "male"));
			patient.setName("Test Patient");
			patient.setIdentity(new Identifier("222", "Test Patient Use",
					"Test Patient label", "Test Patient system", new Period(
							new Date(), new Date())));
			subjectModel.setSubject(patient);
			nursingOrder.setSubjectdetails(subjectModel);
			List<Requirement> requirements = new ArrayList<Requirement>();
			EndorsementRequirement requirement = new EndorsementRequirement();
			requirement.setId("1");
			requirement.setOrignator("Test Originator");
			requirement.setStatus(RequirementStatusCode.Complete);
			requirement.setScheme("Test Scheme");
			requirement.setSeed("Test Seed");
			requirement.setSignature("Test Signature");
			requirement.setType(RequirementType.Endorsement);
			requirement.setUserId("TestUserId");
			requirements.add(requirement);
			nursingOrder.setRequirements(requirements);
			Set<Provenance> provenances = new HashSet<Provenance>();
			Provenance provenance = new Provenance();
			provenance.setType(1);
			provenances.add(provenance);
			nursingOrder.setProvenance(provenances);
			NursingOrderDetail nursingOrderDetail = new NursingOrderDetail();
			nursingOrderDetail.setAction("Test Action");
			nursingOrder.setOrderdetails(nursingOrderDetail);
			orderModel.setType(nursingOrder);

			service.saveOrder(orderModel);

			int size = service.getOrderSize();
			assertEquals(initialSize + 1, size);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_getOrderSize() {
		try {
			int size = service.getOrderSize();
			assertEquals(0, size);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_retrieveOrder() {
		try {
			OrderModel<Order> orderModel = (OrderModel<Order>) service
					.retrieveOrder(new Identifier("11", null, null, null, null));
			assertNull(orderModel);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
