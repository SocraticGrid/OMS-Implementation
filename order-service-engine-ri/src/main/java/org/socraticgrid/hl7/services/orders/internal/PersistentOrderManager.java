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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.dao.ClinicalPractitionerDao;
import org.socraticgrid.hl7.services.orders.dao.OrderDao;
import org.socraticgrid.hl7.services.orders.dao.PromiseDao;
import org.socraticgrid.hl7.services.orders.dao.SubjectDao;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderItem;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.SubjectModel;
import org.socraticgrid.hl7.services.orders.model.entity.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.entity.Code;
import org.socraticgrid.hl7.services.orders.model.entity.OrderDetail;
import org.socraticgrid.hl7.services.orders.model.entity.Provenance;
import org.socraticgrid.hl7.services.orders.model.entity.Requirement;
import org.socraticgrid.hl7.services.orders.model.mapper.ModelEntityMapper;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.primatives.Period;
import org.socraticgrid.hl7.services.orders.model.primatives.Ratio;
import org.socraticgrid.hl7.services.orders.model.requirements.CollectionRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.CounsellingRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.EndorsementRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.PresenceRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.ProceduralRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.RequirementType;
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

/**
 * @author Basit Azemm Sheikh
 *
 */
public class PersistentOrderManager implements OrderManagerIFace {

	private static final Logger log = LoggerFactory
			.getLogger(PersistentOrderManager.class);

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private ClinicalPractitionerDao clinicalPractitionerDao;

	@Autowired
	private SubjectDao subjectDao;

	@Autowired
	private PromiseDao promiseDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace
	 * #saveOrder(org.socraticgrid.hl7.services.orders.model.OrderModel)
	 */
	@Override
	public <T extends Order> void saveOrder(OrderModel<T> order) {
		log.debug(">> saveOrder");
		org.socraticgrid.hl7.services.orders.model.entity.Order persistedOrder = new org.socraticgrid.hl7.services.orders.model.entity.Order();
		String orderType = "";
		if (order.getType() instanceof LabOrder) {
			orderType = "lab";
			LabOrder labOrder = (LabOrder) order.getType();
			persistedOrder
					.setLab(new Code(null, labOrder.getOrderDetails().getLab()
							.getCode(), labOrder.getOrderDetails().getLab()
							.getCodeSystem(), labOrder.getOrderDetails()
							.getLab().getText(), labOrder.getOrderDetails()
							.getLab().getLabel()));
		} else if (order.getType() instanceof MedicationOrder) {
			orderType = "medication";
			persistedOrder.setMedication(((MedicationOrder) order.getType())
					.getOrderDetails().getMedication());
		} else if (order.getType() instanceof NursingOrder) {
			orderType = "nursing";
			persistedOrder.setAction(((NursingOrder) order.getType())
					.getOrderDetails().getAction());
		} else if (order.getType() instanceof NutritionOrder) {
			orderType = "nutrition";
			persistedOrder.setDetails(((NutritionOrder) order.getType())
					.getOrderDetails().getDetails());
		}
		persistedOrder.setType(orderType);
		Order orderBrief = order.getType();
		persistedOrder.setOrderTime(orderBrief.getOrderTime());
		org.socraticgrid.hl7.services.orders.model.entity.Identifier clinicalPractitionerIdentity = new org.socraticgrid.hl7.services.orders.model.entity.Identifier();
		clinicalPractitionerIdentity.setValue(orderBrief.getOrderedBy().getId()
				.getValue());
		ClinicalPractitioner clinicalPractitioner = clinicalPractitionerDao
				.getByIdentity(clinicalPractitionerIdentity);
		if (clinicalPractitioner == null) {
			clinicalPractitionerIdentity.setLabel(orderBrief.getOrderedBy()
					.getId().getLabel());
			clinicalPractitionerIdentity.setEndDate(orderBrief.getOrderedBy()
					.getId().getPeriod().getEnd());
			clinicalPractitionerIdentity.setStartDate(orderBrief.getOrderedBy()
					.getId().getPeriod().getStart());
			clinicalPractitionerIdentity.setSystem(orderBrief.getOrderedBy()
					.getId().getSystem());
			clinicalPractitionerIdentity.setUse(orderBrief.getOrderedBy()
					.getId().getUse());
			clinicalPractitioner = new ClinicalPractitioner();
			clinicalPractitioner.setIdentity(clinicalPractitionerIdentity);
			clinicalPractitioner.setName(orderBrief.getOrderedBy().getName());
			Long id = clinicalPractitionerDao.save(clinicalPractitioner);
			clinicalPractitioner.setId(id);
		}
		persistedOrder.setOrderedBy(clinicalPractitioner);
		persistedOrder.setOrderEnteredBy(ModelEntityMapper
				.getIdentifierEntity(orderBrief.getOrderEnteredBy()));
		persistedOrder.setOrderIdentity(ModelEntityMapper
				.getIdentifierEntity(orderBrief.getOrderIdentity()));

		persistedOrder.setStatus(ModelEntityMapper.getCodeEntity(orderBrief
				.getStatus()));

		org.socraticgrid.hl7.services.orders.model.entity.Identifier subjectIdentity = new org.socraticgrid.hl7.services.orders.model.entity.Identifier();
		subjectIdentity.setValue(orderBrief.getSubjectdetails().getSubject()
				.getIdentity().getValue());
		org.socraticgrid.hl7.services.orders.model.entity.Subject subject = subjectDao
				.getByIdentity(subjectIdentity);
		if (subject == null) {
			subject = new org.socraticgrid.hl7.services.orders.model.entity.Subject();
			subjectIdentity.setEndDate(orderBrief.getSubjectdetails()
					.getSubject().getIdentity().getPeriod().getEnd());
			subjectIdentity.setLabel(orderBrief.getSubjectdetails()
					.getSubject().getIdentity().getLabel());
			subjectIdentity.setStartDate(orderBrief.getSubjectdetails()
					.getSubject().getIdentity().getPeriod().getStart());
			subjectIdentity.setSystem(orderBrief.getSubjectdetails()
					.getSubject().getIdentity().getSystem());
			subjectIdentity.setUse(orderBrief.getSubjectdetails().getSubject()
					.getIdentity().getUse());
			subject.setIdentity(subjectIdentity);
			if (orderBrief.getSubjectdetails().getSubject() instanceof Patient) {
				Patient patient = (Patient) orderBrief.getSubjectdetails()
						.getSubject();
				subject.setName(patient.getName());
				subject.setDateOfBirth(patient.getDateOfBirth());
				subject.setGender(ModelEntityMapper.getCodeEntity(patient
						.getGender()));
				subject.setType("patient");
			}
			Long subId = subjectDao.save(subject);
			subject.setId(subId);
		}
		persistedOrder.setSubject(subject);

		Set<Requirement> requirements = new HashSet<Requirement>();
		for (org.socraticgrid.hl7.services.orders.model.requirements.Requirement requirement : orderBrief
				.getRequirements()) {
			Requirement requirementEntity = createRequirement(requirement);
			requirementEntity.setOrder(persistedOrder);
			requirements.add(requirementEntity);
		}
		persistedOrder.setRequirements(requirements);

		Set<Provenance> provenances = new HashSet<Provenance>();
		for (org.socraticgrid.hl7.services.orders.model.Provenance provenance : orderBrief
				.getProvenance()) {
			Provenance provenanceEntity = new Provenance();
			provenanceEntity.setOrder(persistedOrder);
			provenanceEntity.setType(provenance.getType());
			provenances.add(provenanceEntity);
		}
		persistedOrder.setProvenance(provenances);
		Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();
		if (order.getType() instanceof MedicationOrder) {
			for (MedicationOrderItem medicationOrderItem : ((MedicationOrder) order
					.getType()).getOrdereditems()) {
				OrderDetail orderDetail = createOrderDetail(medicationOrderItem);
				orderDetail.setOrder(persistedOrder);
				orderDetails.add(orderDetail);
			}
		}
		persistedOrder.setOrderDetails(orderDetails);
		orderDao.save(persistedOrder);
		log.debug("saveOrder <<");
	}

	private Requirement createRequirement(
			org.socraticgrid.hl7.services.orders.model.requirements.Requirement requirement) {
		Requirement requirementEntity = new Requirement();
		requirementEntity.setReqId(requirement.getId());
		requirementEntity.setOrignator(requirement.getOrignator());
		switch (requirement.getType()) {
		case Endorsement:
			EndorsementRequirement endorsementRequirement = (EndorsementRequirement) requirement;
			requirementEntity.setScheme(endorsementRequirement.getScheme());
			requirementEntity.setSeed(endorsementRequirement.getSeed());
			requirementEntity.setSignature(endorsementRequirement
					.getSignature());
			requirementEntity.setUserId(endorsementRequirement.getUserId());
			requirementEntity.setType(RequirementType.Endorsement);
			break;
		case Collection:
			requirementEntity.setType(RequirementType.Collection);
			break;
		case Counselling:
			requirementEntity.setType(RequirementType.Counselling);
			break;
		case Presence:
			requirementEntity.setType(RequirementType.Presence);
			break;
		case Procedural:
			requirementEntity.setType(RequirementType.Procedural);
			break;
		case Unknown:
			requirementEntity.setType(RequirementType.Unknown);
			break;
		}
		requirementEntity.setType(requirement.getType());
		requirementEntity.setStatus(requirement.getStatus());
		return requirementEntity;
	}

	private OrderDetail createOrderDetail(OrderItem orderItem) {
		OrderDetail orderDetail = new OrderDetail();
		switch (orderItem.getType()) {
		case 1:
			orderDetail.setType("lab");
			break;
		case 2:
			MedicationOrderItem medicationOrderItem = (MedicationOrderItem) orderItem;
			orderDetail.setAdditionalDosageIntructions(medicationOrderItem
					.getAdditionalDosageIntructions());
			orderDetail.setComment(medicationOrderItem.getComment());
			orderDetail.setDispenseQuantity(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem
							.getDispenseQuantity()));
			orderDetail.setDosageEndTiming(medicationOrderItem
					.getDosageTimingPeriod().getEnd());
			orderDetail.setDosageStartTiming(medicationOrderItem
					.getDosageTimingPeriod().getStart());
			orderDetail.setDosageInstructions(medicationOrderItem
					.getDosageInstructions());
			orderDetail.setDosageMethod(ModelEntityMapper
					.getCodeEntity(medicationOrderItem.getDosageMethod()));
			orderDetail
					.setDosageQuantity(ModelEntityMapper
							.getQuantityEntity(medicationOrderItem
									.getDosageQuantity()));
			orderDetail.setDosageRateDenominator(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem.getDosageRate()
							.getDemoninator()));
			orderDetail.setDosageRateNumerator(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem.getDosageRate()
							.getNumerator()));
			orderDetail.setDosageSite(ModelEntityMapper
					.getCodeEntity(medicationOrderItem.getDosageSite()));
			Set<org.socraticgrid.hl7.services.orders.model.entity.Identifier> drugIdentities = new HashSet<org.socraticgrid.hl7.services.orders.model.entity.Identifier>();
			for (Identifier drugId : medicationOrderItem.getDrug()) {
				drugIdentities.add(ModelEntityMapper
						.getIdentifierEntity(drugId));
			}
			orderDetail.setDrugs(drugIdentities);
			orderDetail.setEndDate(medicationOrderItem.getEndDate());
			orderDetail.setStartDate(medicationOrderItem.getStartDate());
			orderDetail.setExpectedSupplyDuration(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem
							.getExpectedSupplyDuration()));
			orderDetail.setMaxDosePerPeriodDenominator(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem
							.getMaxDosePerPeriod().getDemoninator()));
			orderDetail.setMaxDosePerPeriodNumerator(ModelEntityMapper
					.getQuantityEntity(medicationOrderItem
							.getMaxDosePerPeriod().getNumerator()));
			Set<org.socraticgrid.hl7.services.orders.model.entity.Identifier> medicationIdentities = new HashSet<org.socraticgrid.hl7.services.orders.model.entity.Identifier>();
			for (Identifier medicationId : medicationOrderItem.getMedication()) {
				medicationIdentities.add(ModelEntityMapper
						.getIdentifierEntity(medicationId));
			}
			orderDetail.setMedications(medicationIdentities);
			orderDetail.setNumberOfRepeatsAllowed(medicationOrderItem
					.getNumberOfRepeatsAllowed());
			orderDetail.setPrescriber(ModelEntityMapper
					.getCodeEntity(medicationOrderItem.getPrescriber()));
			orderDetail.setRoute(ModelEntityMapper
					.getCodeEntity(medicationOrderItem.getRoute()));
			orderDetail.setType("medication");
			break;
		case 3:
			orderDetail.setType("nursing");
			break;
		case 4:
			orderDetail.setType("nutrition");
			break;
		}
		return orderDetail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace
	 * #retrieveOrder(org.socraticgrid.hl7.services.orders.model.primatives.
	 * Identifier)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OrderModel<? extends Order> retrieveOrder(Identifier identifier) {
		org.socraticgrid.hl7.services.orders.model.entity.Order order = orderDao
				.getOrderByIdentity(identifier);
		OrderModel<? extends Order> orderModel = null;
		if (order != null) {
			orderModel = new OrderModel<Order>();
			Order orderBrief = null;
			switch (order.getType()) {
			case "lab":
				orderBrief = new LabOrder();
				LabOrderDetail labOrderDetail = new LabOrderDetail();
				labOrderDetail.setLab(ModelEntityMapper.getCodeModel(order
						.getLab()));
				((LabOrder) orderBrief).setOrderdetails(labOrderDetail);
				((OrderModel<LabOrder>) orderModel)
						.setType(((LabOrder) orderBrief));
				// Need to add setters for List of LabOrderItem, as of now there
				// are no fields
				break;
			case "medication":
				orderBrief = new MedicationOrder();
				MedicationOrderDetail medicationOrderDetail = new MedicationOrderDetail();
				medicationOrderDetail.setMedication(order.getMedication());
				((MedicationOrder) orderBrief)
						.setOrderdetails(medicationOrderDetail);
				((OrderModel<MedicationOrder>) orderModel)
						.setType(((MedicationOrder) orderBrief));
				List<MedicationOrderItem> medicationOrderItems = new ArrayList<MedicationOrderItem>();
				for (OrderDetail orderDetail : order.getOrderDetails()) {
					MedicationOrderItem medicationOrderItem = retrieveMedicationOrderItem(orderDetail);
					medicationOrderItems.add(medicationOrderItem);
				}
				((MedicationOrder) orderBrief)
						.setOrdereditems(medicationOrderItems);
				break;
			case "nursing":
				orderBrief = new NursingOrder();
				NursingOrderDetail nursingOrderDetail = new NursingOrderDetail();
				nursingOrderDetail.setAction(order.getAction());
				((NursingOrder) orderBrief).setOrderdetails(nursingOrderDetail);
				((OrderModel<NursingOrder>) orderModel)
						.setType(((NursingOrder) orderBrief));
				// Need to add setters for List of NursingOrderItem, as of now
				// there are no fields
				break;
			case "nutrition":
				orderBrief = new NutritionOrder();
				NutritionOrderDetail nutritionOrderDetail = new NutritionOrderDetail();
				nutritionOrderDetail.setDetails(order.getDetails());
				((NutritionOrder) orderBrief)
						.setOrderdetails(nutritionOrderDetail);
				((OrderModel<NutritionOrder>) orderModel)
						.setType(((NutritionOrder) orderBrief));
				// Need to add setters for List of LabOrderItem, as of now there
				// are no fields
				break;
			}
			orderBrief.setOrderTime(order.getOrderTime());
			SubjectModel subjectModel = new SubjectModel();
			Patient subject = new Patient();
			subject.setName(order.getSubject().getName());
			subject.setDateOfBirth(order.getSubject().getDateOfBirth());
			subject.setGender(ModelEntityMapper.getCodeModel(order.getSubject()
					.getGender()));
			subject.setIdentity(ModelEntityMapper.getIdentifierModel(order
					.getSubject().getIdentity()));
			subjectModel.setSubject(subject);
			orderBrief.setSubjectdetails(subjectModel);
			orderBrief.setOrderedBy(ModelEntityMapper
					.getClinicalPractitionerModel(order.getOrderedBy()));
			List<org.socraticgrid.hl7.services.orders.model.requirements.Requirement> requirements = new ArrayList<org.socraticgrid.hl7.services.orders.model.requirements.Requirement>();
			for (Requirement requirement : order.getRequirements()) {
				org.socraticgrid.hl7.services.orders.model.requirements.Requirement reqModel = retrieveRequirement(requirement);
				if (reqModel != null) {
					requirements.add(reqModel);
				}
			}
			orderBrief.setRequirements(requirements);
			orderBrief.setOrderEnteredBy(ModelEntityMapper
					.getIdentifierModel(order.getOrderEnteredBy()));
			orderBrief.setOrderIdentity(ModelEntityMapper
					.getIdentifierModel(order.getOrderIdentity()));
			orderBrief.setStatus(ModelEntityMapper.getCodeModel(order
					.getStatus()));
			Set<org.socraticgrid.hl7.services.orders.model.Provenance> provenances = new HashSet<org.socraticgrid.hl7.services.orders.model.Provenance>();
			for (Provenance provenance : order.getProvenance()) {
				org.socraticgrid.hl7.services.orders.model.Provenance provenanceModel = new org.socraticgrid.hl7.services.orders.model.Provenance();
				provenanceModel.setType(provenance.getType());
				provenances.add(provenanceModel);
			}
			orderBrief.setProvenance(provenances);
		}
		return orderModel;
	}

	private MedicationOrderItem retrieveMedicationOrderItem(
			OrderDetail orderDetail) {
		MedicationOrderItem medicationOrderItem = new MedicationOrderItem();
		medicationOrderItem.setAdditionalDosageIntructions(orderDetail
				.getAdditionalDosageIntructions());
		medicationOrderItem.setComment(orderDetail.getComment());
		medicationOrderItem.setDispenseQuantity(ModelEntityMapper
				.getQuantityModel(orderDetail.getDispenseQuantity()));
		medicationOrderItem.setDosageInstructions(orderDetail
				.getDosageInstructions());
		medicationOrderItem.setDosageMethod(ModelEntityMapper
				.getCodeModel(orderDetail.getDosageMethod()));
		medicationOrderItem.setDosageQuantity(ModelEntityMapper
				.getQuantityModel(orderDetail.getDosageQuantity()));
		medicationOrderItem.setDosageRate(new Ratio(ModelEntityMapper
				.getQuantityModel(orderDetail.getDosageRateNumerator()),
				ModelEntityMapper.getQuantityModel(orderDetail
						.getDosageRateDenominator())));
		medicationOrderItem.setDosageSite(ModelEntityMapper
				.getCodeModel(orderDetail.getDosageSite()));
		medicationOrderItem.setDosageTiming(orderDetail.getDosageTiming());
		medicationOrderItem.setDosageTimingPeriod(new Period(orderDetail
				.getDosageStartTiming(), orderDetail.getDosageEndTiming()));
		medicationOrderItem.setEndDate(orderDetail.getEndDate());
		medicationOrderItem.setStartDate(orderDetail.getStartDate());
		medicationOrderItem.setExpectedSupplyDuration(ModelEntityMapper
				.getQuantityModel(orderDetail.getExpectedSupplyDuration()));
		medicationOrderItem.setNumberOfRepeatsAllowed(orderDetail
				.getNumberOfRepeatsAllowed());
		medicationOrderItem.setPrescriber(ModelEntityMapper
				.getCodeModel(orderDetail.getPrescriber()));
		medicationOrderItem.setRoute(ModelEntityMapper.getCodeModel(orderDetail
				.getRoute()));
		medicationOrderItem.setSchedule(orderDetail.getSchedule());
		List<Identifier> drugIdentifiers = new ArrayList<Identifier>();
		for (org.socraticgrid.hl7.services.orders.model.entity.Identifier drugId : orderDetail
				.getDrugs()) {
			drugIdentifiers.add(ModelEntityMapper.getIdentifierModel(drugId));
		}
		List<Identifier> medicationIdentifiers = new ArrayList<Identifier>();
		for (org.socraticgrid.hl7.services.orders.model.entity.Identifier medicationId : orderDetail
				.getMedications()) {
			medicationIdentifiers.add(ModelEntityMapper
					.getIdentifierModel(medicationId));
		}
		return medicationOrderItem;
	}

	private org.socraticgrid.hl7.services.orders.model.requirements.Requirement retrieveRequirement(
			Requirement requirement) {
		org.socraticgrid.hl7.services.orders.model.requirements.Requirement reqModel = null;
		switch (requirement.getType()) {
		case Endorsement:
			reqModel = new EndorsementRequirement();
			reqModel.setId(requirement.getReqId());
			reqModel.setOrignator(requirement.getOrignator());
			reqModel.setStatus(requirement.getStatus());
			reqModel.setType(requirement.getType());
			((EndorsementRequirement) reqModel).setScheme(requirement
					.getScheme());
			((EndorsementRequirement) reqModel).setSignature(requirement
					.getSignature());
			((EndorsementRequirement) reqModel).setSeed(requirement.getSeed());
			((EndorsementRequirement) reqModel).setUserId(requirement
					.getUserId());
			break;
		case Collection:
			reqModel = new CollectionRequirement();
			reqModel.setId(requirement.getId() + "");
			reqModel.setOrignator(requirement.getOrignator());
			reqModel.setStatus(requirement.getStatus());
			reqModel.setType(requirement.getType());
			break;
		case Counselling:
			reqModel = new CounsellingRequirement();
			reqModel.setId(requirement.getId() + "");
			reqModel.setOrignator(requirement.getOrignator());
			reqModel.setStatus(requirement.getStatus());
			reqModel.setType(requirement.getType());
			break;
		case Presence:
			reqModel = new PresenceRequirement();
			reqModel.setId(requirement.getId() + "");
			reqModel.setOrignator(requirement.getOrignator());
			reqModel.setStatus(requirement.getStatus());
			reqModel.setType(requirement.getType());
			break;
		case Procedural:
			reqModel = new ProceduralRequirement();
			reqModel.setId(requirement.getId() + "");
			reqModel.setOrignator(requirement.getOrignator());
			reqModel.setStatus(requirement.getStatus());
			reqModel.setType(requirement.getType());
			break;
		case Unknown:
			break;
		default:
			break;
		}
		return reqModel;
	}

	@Override
	public int getOrderSize() {
		return orderDao.count() != null ? orderDao.count().intValue() : 0;
	}

	@Override
	public List<OrderSummary> queryOrders(Subject subject) {
		List<OrderSummary> orders = new ArrayList<>();

		/**
		 * Check to see if Subject is a Patient and if so get an identifier
		 */
		if (subject instanceof Patient) {
			Patient patient = (Patient) subject;
			List<String> orderIdentityValues = orderDao
					.getIdentityValuesByPatient(patient);
			for (String orderId : orderIdentityValues) {
				OrderSummary summary = new OrderSummary();
				summary.setOrderIdentity(orderId);
				orders.add(summary);
			}
		}

		return orders;
	}

	@Override
	public <T extends Order> void updateOrder(OrderModel<T> order) {
		org.socraticgrid.hl7.services.orders.model.entity.Order orderEntity = orderDao
				.getOrderByIdentity(order.getType().getOrderEnteredBy());
		if (orderEntity != null) {
			switch (orderEntity.getType()) {
			case "lab":
				LabOrderDetail labOrderDetail = (LabOrderDetail) order
						.getType().getOrderDetails();
				orderEntity.setLab(new Code(orderEntity.getLab().getId(),
						labOrderDetail.getLab().getCode(), labOrderDetail
								.getLab().getCodeSystem(), labOrderDetail
								.getLab().getText(), labOrderDetail.getLab()
								.getLabel()));
				break;
			case "medication":
				MedicationOrderDetail medicationOrderDetail = (MedicationOrderDetail) order
						.getType().getOrderDetails();
				orderEntity
						.setMedication(medicationOrderDetail.getMedication());
				break;
			case "nursing":
				NursingOrderDetail nursingOrderDetail = (NursingOrderDetail) order
						.getType().getOrderDetails();
				orderEntity.setAction(nursingOrderDetail.getAction());
				break;
			case "nutrition":
				NutritionOrderDetail nutritionOrderDetail = (NutritionOrderDetail) order
						.getType().getOrderDetails();
				orderEntity.setDetails(nutritionOrderDetail.getDetails());
				break;
			}
			ClinicalPractitioner clinicalPractitioner = orderEntity
					.getOrderedBy();
			clinicalPractitioner
					.setIdentity(new org.socraticgrid.hl7.services.orders.model.entity.Identifier(
							clinicalPractitioner.getIdentity().getId(), order
									.getType().getOrderedBy().getId()
									.getValue(), order.getType().getOrderedBy()
									.getId().getUse(), order.getType()
									.getOrderedBy().getId().getLabel(), order
									.getType().getOrderedBy().getId()
									.getSystem(), order.getType()
									.getOrderedBy().getId().getPeriod()
									.getStart(), order.getType().getOrderedBy()
									.getId().getPeriod().getEnd()));
			clinicalPractitioner.setName(order.getType().getOrderedBy()
					.getName());
			orderEntity.setOrderedBy(clinicalPractitioner);
			orderEntity
					.setOrderEnteredBy(new org.socraticgrid.hl7.services.orders.model.entity.Identifier(
							orderEntity.getOrderEnteredBy().getId(), order
									.getType().getOrderEnteredBy().getValue(),
							order.getType().getOrderEnteredBy().getUse(), order
									.getType().getOrderEnteredBy().getLabel(),
							order.getType().getOrderEnteredBy().getSystem(),
							order.getType().getOrderEnteredBy().getPeriod()
									.getStart(), order.getType()
									.getOrderEnteredBy().getPeriod().getEnd()));
			orderEntity.setOrderTime(order.getType().getOrderTime());
			orderEntity.setStatus(new Code(orderEntity.getStatus().getId(),
					order.getType().getStatus().getCode(), order.getType()
							.getStatus().getCodeSystem(), order.getType()
							.getStatus().getText(), order.getType().getStatus()
							.getLabel()));
			org.socraticgrid.hl7.services.orders.model.entity.Subject subject = new org.socraticgrid.hl7.services.orders.model.entity.Subject();
			Patient patient = (Patient) order.getType().getSubjectdetails()
					.getSubject();
			subject.setId(orderEntity.getSubject().getId());
			subject.setDateOfBirth(patient.getDateOfBirth());
			subject.setGender(new Code(orderEntity.getSubject().getGender()
					.getId(), patient.getGender().getCode(), patient
					.getGender().getCodeSystem(),
					patient.getGender().getText(), patient.getGender()
							.getLabel()));
			subject.setIdentity(new org.socraticgrid.hl7.services.orders.model.entity.Identifier(
					orderEntity.getSubject().getIdentity().getId(), patient
							.getIdentity().getLabel(), patient.getIdentity()
							.getUse(), patient.getIdentity().getLabel(),
					patient.getIdentity().getSystem(), patient.getIdentity()
							.getPeriod().getStart(), patient.getIdentity()
							.getPeriod().getEnd()));
			subject.setName(patient.getName());
			subject.setType("patient");
			orderEntity.setSubject(subject);
			orderDao.update(orderEntity);
		}
	}

	@Override
	public void updateOrderPromise(Identifier identifier, Promise promise) {
		org.socraticgrid.hl7.services.orders.model.entity.Promise promiseEntity = promiseDao
				.getByIdentifier(identifier);
		promiseEntity
				.setFulfillmentIdentity(new org.socraticgrid.hl7.services.orders.model.entity.Identifier(
						promiseEntity.getFulfillmentIdentity().getId(), promise
								.getFulfillmentIdentity().getValue(), promise
								.getFulfillmentIdentity().getUse(), promise
								.getFulfillmentIdentity().getLabel(), promise
								.getFulfillmentIdentity().getSystem(), promise
								.getFulfillmentIdentity().getPeriod()
								.getStart(), promise.getFulfillmentIdentity()
								.getPeriod().getEnd()));
		promiseEntity
				.setOrderIdentity(new org.socraticgrid.hl7.services.orders.model.entity.Identifier(
						promiseEntity.getOrderIdentity().getId(), promise
								.getOrderIdentity().getValue(), promise
								.getOrderIdentity().getUse(), promise
								.getOrderIdentity().getLabel(), promise
								.getOrderIdentity().getSystem(), promise
								.getOrderIdentity().getPeriod().getStart(),
						promise.getOrderIdentity().getPeriod().getEnd()));
		promiseEntity.setStatus(new Code(promiseEntity.getStatus().getId(),
				promise.getStatus().getCode(), promise.getStatus()
						.getCodeSystem(), promise.getStatus().getText(),
				promise.getStatus().getLabel()));
		promiseDao.update(promiseEntity);
	}

	@Override
	public void saveOrderPromise(Identifier identifier, Promise promise) {
		org.socraticgrid.hl7.services.orders.model.entity.Promise promiseEntity = new org.socraticgrid.hl7.services.orders.model.entity.Promise();
		promiseEntity.setFulfillmentIdentity(ModelEntityMapper
				.getIdentifierEntity(promise.getFulfillmentIdentity()));
		promiseEntity.setOrderIdentity(ModelEntityMapper
				.getIdentifierEntity(promise.getOrderIdentity()));
		promiseEntity.setPromiseIdentity(ModelEntityMapper
				.getIdentifierEntity(promise.getPromiseIdentity()));
		promiseEntity.setStatus(ModelEntityMapper.getCodeEntity(promise
				.getStatus()));
		Set<OrderDetail> orderedItems = new HashSet<OrderDetail>();
		for (OrderItem orderItem : promise.getOrderedItems()) {
			OrderDetail orderDetail = createOrderDetail(orderItem);
			orderDetail.setOrderedItem(promiseEntity);
			orderedItems.add(orderDetail);
		}
		promiseEntity.setOrderedItems(orderedItems);
		Set<OrderDetail> promisedItems = new HashSet<OrderDetail>();
		for (OrderItem orderItem : promise.getPromisedItems()) {
			OrderDetail orderDetail = createOrderDetail(orderItem);
			orderDetail.setPromisedItem(promiseEntity);
			promisedItems.add(orderDetail);
		}
		promiseEntity.setPromisedItems(promisedItems);
		Set<Requirement> requirements = new HashSet<Requirement>();
		for (org.socraticgrid.hl7.services.orders.model.requirements.Requirement requirement : promise
				.getRequirements()) {
			Requirement requirementEntity = createRequirement(requirement);
			requirementEntity.setPromise(promiseEntity);
			requirements.add(requirementEntity);
		}
		promiseEntity.setRequirements(requirements);
		promiseDao.save(promiseEntity);

	}

	@Override
	public Promise retrievePromise(Identifier identifier) {
		Promise promise = new Promise();
		org.socraticgrid.hl7.services.orders.model.entity.Promise promiseEntity = promiseDao
				.getByIdentifier(identifier);
		if (promiseEntity != null) {
			promise.setFulfillmentIdentity(ModelEntityMapper
					.getIdentifierModel(promiseEntity.getFulfillmentIdentity()));
			promise.setOrderIdentity(ModelEntityMapper
					.getIdentifierModel(promiseEntity.getOrderIdentity()));
			promise.setPromiseIdentity(ModelEntityMapper
					.getIdentifierModel(promiseEntity.getPromiseIdentity()));
			promise.setStatus(ModelEntityMapper.getCodeModel(promiseEntity
					.getStatus()));
			List<OrderItem> orderedItems = new ArrayList<OrderItem>();
			for (OrderDetail orderDetail : promiseEntity.getOrderedItems()) {
				switch (orderDetail.getType()) {
				case "lab":
					LabOrderItem labOrderItem = new LabOrderItem();
					labOrderItem.setType(1);
					orderedItems.add(labOrderItem);
					break;
				case "medication":
					MedicationOrderItem medicationOrderItem = retrieveMedicationOrderItem(orderDetail);
					medicationOrderItem.setType(2);
					orderedItems.add(medicationOrderItem);
					break;
				case "nursing":
					NursingOrderItem nursingOrderItem = new NursingOrderItem();
					nursingOrderItem.setType(4);
					orderedItems.add(nursingOrderItem);
					break;
				case "nutrition":
					NutritionOrderItem nutritionOrderItem = new NutritionOrderItem();
					nutritionOrderItem.setType(5);
					orderedItems.add(nutritionOrderItem);
					break;
				}
			}
			promise.setOrderedItems(orderedItems);

			List<OrderItem> promisedItems = new ArrayList<OrderItem>();
			for (OrderDetail orderDetail : promiseEntity.getPromisedItems()) {
				switch (orderDetail.getType()) {
				case "lab":
					LabOrderItem labOrderItem = new LabOrderItem();
					labOrderItem.setType(1);
					promisedItems.add(labOrderItem);
					break;
				case "medication":
					MedicationOrderItem medicationOrderItem = retrieveMedicationOrderItem(orderDetail);
					medicationOrderItem.setType(2);
					promisedItems.add(medicationOrderItem);
					break;
				case "nursing":
					NursingOrderItem nursingOrderItem = new NursingOrderItem();
					nursingOrderItem.setType(4);
					promisedItems.add(nursingOrderItem);
					break;
				case "nutrition":
					NutritionOrderItem nutritionOrderItem = new NutritionOrderItem();
					nutritionOrderItem.setType(5);
					promisedItems.add(nutritionOrderItem);
					break;
				}
			}
			promise.setPromisedItems(promisedItems);

			List<org.socraticgrid.hl7.services.orders.model.requirements.Requirement> requirements = new ArrayList<org.socraticgrid.hl7.services.orders.model.requirements.Requirement>();
			for (Requirement requirement : promiseEntity.getRequirements()) {
				requirements.add(retrieveRequirement(requirement));
			}
			promise.setRequirements(requirements);
		}
		return promise;
	}
}
