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
package org.socraticgrid.hl7.services.orders.internal;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Period;
import org.socraticgrid.hl7.services.orders.model.types.order.LabOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.MedicationOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.NursingOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.NutritionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Test-MemoryOrderManagerTest.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MemoryOrderManagerTest {

	@Autowired
	@InjectMocks
	private OrderManagerIFace  manager;

	static List<Identifier> identifiers = new ArrayList<>();
        
	@Test
	public void A_testSaveOrder() {
		MedicationOrder medicationOrder = new MedicationOrder();
		Identifier identifier = getNewIdentifier();
		medicationOrder.setOrderIdentity(identifier);
		identifiers.add(identifier);
		OrderModel<MedicationOrder> orderModel = new OrderModel<>();
		orderModel.setType(medicationOrder);
		manager.saveOrder(orderModel);
		
		LabOrder labOrder = new LabOrder();
		identifier = getNewIdentifier();
		labOrder.setOrderIdentity(identifier);
		identifiers.add(identifier);
		OrderModel<LabOrder> labModel = new OrderModel<>();
		labModel.setType(labOrder);
		manager.saveOrder(labModel);
	
		NursingOrder nursingOrder = new NursingOrder();
		identifier = getNewIdentifier();
		nursingOrder.setOrderIdentity(identifier);
		identifiers.add(identifier);
		OrderModel<NursingOrder> nursingModel = new OrderModel<>();
		nursingModel.setType(nursingOrder);
		manager.saveOrder(nursingModel);
		
		NutritionOrder nutritionOrder = new NutritionOrder();
		identifier = getNewIdentifier();
		nutritionOrder.setOrderIdentity(identifier);
		identifiers.add(identifier);
		OrderModel<NutritionOrder> nutritionModel = new OrderModel<>();
		nutritionModel.setType(nutritionOrder);
		manager.saveOrder(nutritionModel);		
		
		assertTrue("Number of orders in list should be 4 Orders but only has "+identifiers.size(), 4 == identifiers.size());
		assertTrue("Manager should have 4 Orders but only has "+manager.getOrderSize(), 4 == manager.getOrderSize());
	}
	
	@Test
	public void B_testRetrieveOrder() {
		assertTrue("Number of orders in list should be 4 Orders but only has "+identifiers.size(), 4 == identifiers.size());

		OrderModel<? extends Order> orderModel = manager.retrieveOrder(identifiers.get(0));
		assertTrue("First order should be MedicationOrder but is "+orderModel.getType().getClass().getSimpleName(),
				orderModel.getType() instanceof MedicationOrder);
		
		orderModel = manager.retrieveOrder(identifiers.get(1));
		assertTrue("First order should be LabOrder but is "+orderModel.getType().getClass().getSimpleName(),
				orderModel.getType() instanceof LabOrder);
		
		orderModel = manager.retrieveOrder(identifiers.get(2));
		assertTrue("First order should be NursingOrder but is "+orderModel.getType().getClass().getSimpleName(),
				orderModel.getType() instanceof NursingOrder);
		
		orderModel = manager.retrieveOrder(identifiers.get(3));
		assertTrue("First order should be NutritionOrder but is "+orderModel.getType().getClass().getSimpleName(),
				orderModel.getType() instanceof NutritionOrder);
		
	}

	
	private Identifier getNewIdentifier() {
		Identifier identifier = new Identifier();
		identifier.setSystem("unknown/system");
		identifier.setValue(UUID.randomUUID().toString());
		Period period = Period.get().setStart(new Date()).setEnd(new Date());
		identifier.setPeriod(period);
		
		return identifier;
	}
}
