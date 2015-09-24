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
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceFeature;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.OrderManagementService;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.model.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.SubjectModel;
import org.socraticgrid.hl7.services.orders.model.operationresults.CreateOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Quantity;
import org.socraticgrid.hl7.services.orders.model.requirements.CollectionRequirement;
import org.socraticgrid.hl7.services.orders.model.status.CreateStatus;
import org.socraticgrid.hl7.services.orders.model.types.order.LabOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.MedicationOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.NursingOrder;
import org.socraticgrid.hl7.services.orders.model.types.order.NutritionOrder;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.LabOrderDetail;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.MedicationOrderDetail;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.NursingOrderDetail;
import org.socraticgrid.hl7.services.orders.model.types.orderdetail.NutritionOrderDetail;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.LabOrderItem;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.MedicationOrderItem;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.NursingOrderItem;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.NutritionOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author steven
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:Test-OrderManagementServiceImplTest.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderManagementService_WS_TestIT {

	private static final Logger log = LoggerFactory.getLogger(OrderManagementService_WS_TestIT.class);
	
	public final static QName SERVICE = new QName(
			"org.socraticgrid.hl7.services.orders",
			"OrderManagementServiceService");
	
	public final static QName OrderManagementPort = new QName("org.socraticgrid.hl7.services.orders", "ordermanagementPort");

	 	    
	protected static Endpoint server;
	protected static String address;
	protected static List<Identifier> identifiers = new ArrayList<>();
	protected static Deque<OrderModel<? extends Order>> orders = new ArrayDeque<>();
	protected static URL wsdl;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("OrderManagementService")
	@InjectMocks
	private OrderManagementService service;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			address = "http://localhost:9090/ordermanagement";
			server = Endpoint.create(service);
			server.publish(address);
			wsdl = new URL(address+"?WSDL");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		try{
			server.stop();
			server = null;
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}


	@Test
	public void doNothing()
	{
		
	}

	@Test
	public void a_test() {		
		MedicationOrder medicationOrder = createPrototypeMedOrder();
		medicationOrder.setSubjectdetails(getNewSubject());
		OrderModel<MedicationOrder> medicationModel = new OrderModel<>();
		medicationModel.setType(medicationOrder);
		orders.add(medicationModel);


		LabOrder labOrder =  createPrototypeLabOrder();
		OrderModel<LabOrder> labModel = new OrderModel<>();
		labOrder.setSubjectdetails(getNewSubject());
		labModel.setType(labOrder);
		orders.add(labModel);


		NutritionOrder nutritionOrder = createPrototypeNutOrder();
		nutritionOrder.setSubjectdetails(getNewSubject());
		OrderModel<NutritionOrder> nutritionModel = new OrderModel<>();
		nutritionModel.setType(nutritionOrder);
		orders.add(nutritionModel);

		NursingOrder nursingOrder = createPrototypeNurseOrder();
		nursingOrder.setSubjectdetails(getNewSubject());	
		OrderModel<NursingOrder> nursingModel = new OrderModel<>();
		nursingModel.setType(nursingOrder);
		orders.add(nursingModel);


		ClientServiceProxy ss = new ClientServiceProxy(wsdl);
		
		OrderManagementIFace port = ss.getOrderManagementPort();
		((BindingProvider)port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				address);

		CreateOrderResult result = null;
		try{
			for(OrderModel<? extends Order> orderModel : orders){
				result = port.createOrder(orderModel);
				assertTrue("Create failed ("+result.getStatus()+")",result.getStatus()==CreateStatus.Sucessful);
				Identifier identifier = result.getOrderIdentity();
				assertNotNull("createOrder has returned a null Identifier.",identifier);
				if(identifier != null) identifiers.add(identifier);
				orderModel.getType().setOrderIdentity(identifier);
				
				log.info(orderModel.getType()+" : orderModel.identity = "+identifier.getValue()+" orderModel.subjectIdentity = "+
						orderModel.getType().getSubjectdetails().getSubject().getIdentity().getValue());
			}
		}
		catch(Exception oe){
			oe.printStackTrace();
		}

		System.out.println("Result status = "+result.getStatus());

	}

	@Test
	public void b_test() {	

		ClientServiceProxy ss = new ClientServiceProxy(wsdl);
		
		OrderManagementIFace port = ss.getOrderManagementPort();
		((BindingProvider)port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				address);

		try{
			Iterator<OrderModel<? extends Order>> itr = orders.iterator();
			for(Identifier identifier : identifiers){				
				OrderModel<? extends Order> orderModel = port.retrieveOrder(identifier);
				assertNotNull("retrieveOrder has returned a null OrderModel.",orderModel);

				OrderModel<? extends Order> storedOrderModel = itr.next();
				
				assertTrue(storedOrderModel.getType().getClass().equals(orderModel.getType().getClass()));
			}
		}
		catch(Exception oe){
			oe.printStackTrace();
		}

	}

	@Test
	public void c_test() {
		OrderModel<? extends Order> testModel = orders.poll();
		assertNotNull(testModel);
		Subject c_testSubject = testModel.getType().getSubjectdetails().getSubject();
		assertNotNull(c_testSubject);
		Identifier c_testIdentifier = testModel.getType().getOrderIdentity();
		assertNotNull("Missing order identity",c_testIdentifier);

		assertNotNull("TestSubject is NULL !!!",c_testSubject);


		ClientServiceProxy ss = new ClientServiceProxy(wsdl);
		
		OrderManagementIFace port = ss.getOrderManagementPort();
		((BindingProvider)port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				address);

		try{
			List<OrderSummary> summaries = port.queryOrders(c_testSubject);
			assertTrue("queryOrder has returned no OrderSummaries or found more than 1.",summaries.size()==1);
			for(OrderSummary summary : summaries) {
				assertTrue("Order identity does not match the query value!",summary.getOrderIdentity().equals(c_testIdentifier.getValue()));
			}
		}
		catch(Exception oe){
			oe.printStackTrace();
		}

	}
	
	private SubjectModel getNewSubject() {
		Patient patient = new Patient();
		Identifier id = new Identifier();
		id.setValue(UUID.randomUUID().toString());
		id.setSystem("PatientId");
		id.setLabel("PatientId");
		patient.setIdentity(id);
		
		SubjectModel subjectModel = new SubjectModel();
		subjectModel.setSubject(patient);
		return subjectModel;
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
	
	public MedicationOrder createPrototypeMedOrder()
	{
		MedicationOrder out = new MedicationOrder();
		
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
		MedicationOrderDetail details = new MedicationOrderDetail();
		out.setOrderdetails(details);
		//Ordered Items
		MedicationOrderItem item = new MedicationOrderItem();
	
		//Drug
		LinkedList<Identifier> drugs = new LinkedList<Identifier>();
		Identifier drug = new Identifier();
		drug.setLabel("Asprin");
		drug.setValue("ASPRIN");
		drug.setSystem("TEST");
		drugs.add(drug);
		item.setDrug(drugs);
		
		//Med
		LinkedList<Identifier> meds = new LinkedList<Identifier>();
		Identifier med = new Identifier();
		med.setLabel("Asprin");
		med.setValue("ASPRIN");
		med.setSystem("TEST");
		meds.add(med);
		item.setDrug(meds);

		Quantity dispenseQuantity = new Quantity();
		dispenseQuantity.setUnits("TAB");
		dispenseQuantity.setValue("1");
		item.setDispenseQuantity(dispenseQuantity);
		
		Quantity doseQuantity = new Quantity();
		doseQuantity.setUnits("mg");
		doseQuantity.setValue("325");
		item.setDosageQuantity(doseQuantity);
		
		out.getOrdereditems().add(item);
		
		return out;
	}
	
	public NutritionOrder createPrototypeNutOrder()
	{
		NutritionOrder out = new NutritionOrder();
		
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
		NutritionOrderDetail details = new NutritionOrderDetail();
		out.setOrderdetails(details);
		//Ordered Items
		NutritionOrderItem item = new NutritionOrderItem();
		
		out.getOrdereditems().add(item);
		
		return out;
	}
	
	public NursingOrder createPrototypeNurseOrder()
	{
		NursingOrder out = new NursingOrder();
		
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
		NursingOrderDetail details = new NursingOrderDetail();
		out.setOrderdetails(details);
		//Ordered Items
		NursingOrderItem item = new NursingOrderItem();
		
		out.getOrdereditems().add(item);
		
		return out;
	}
	
	class ClientServiceProxy extends Service {


	    public ClientServiceProxy(URL wsdlLocation) {
	        super(wsdlLocation, SERVICE);
	    }

	    public ClientServiceProxy(URL wsdlLocation, QName serviceName) {
	        super(wsdlLocation, serviceName);
	        
	    }
	    
	    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
	    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
	    //compliant code instead.
	    public ClientServiceProxy(URL wsdlLocation, WebServiceFeature ... features) {
	        super(wsdlLocation, SERVICE, features);
	    }

	    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
	    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
	    //compliant code instead.
	    public ClientServiceProxy(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
	        super(wsdlLocation, serviceName, features);
	    }

	    /**
	     *
	     * @return
	     *     returns OrderManagementSEI
	     */
	    @WebEndpoint(name = "ordermanagementPort")
	    public OrderManagementIFace getOrderManagementPort() {
	        return super.getPort(OrderManagementPort, OrderManagementIFace.class);
	    }

	    /**
	     * 
	     * @param features
	     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	     * @return
	     *     returns OrderManagement
	     */
	    @WebEndpoint(name = "ordermanagementPort")
	    public OrderManagementIFace getOrderManagementPort(WebServiceFeature... features) {
	        return super.getPort(OrderManagementPort, OrderManagementIFace.class, features);
	    }

	}

}
