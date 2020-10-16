package org.epragati.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.common.dao.FiveDayApplicantDetailsDAO;
import org.epragati.common.dao.FiveDayFlowDAO;
import org.epragati.common.dao.FiveDayPaymentTransactionDAO;
import org.epragati.common.dao.FiveDayPermitDetailsDAO;
import org.epragati.common.dao.FlowDAO;
import org.epragati.common.dao.PropertiesDAO;
import org.epragati.common.dto.ApplicantDetails_last5d;
import org.epragati.common.dto.FlowDTO;
import org.epragati.common.dto.Flow_last5d;
import org.epragati.common.dto.PropertiesDTO;
import org.epragati.common.dto.RegistrationDetails_last5d;
import org.epragati.common.dto.TaxDetails_last5d;
import org.epragati.common.dto.aadhaar.seed.AadhaarSeedDTO;
import org.epragati.constants.Schedulers;
import org.epragati.master.dao.FTaxDetailsDAO;
import org.epragati.master.dao.RegSchedulerDashboardDAO;
import org.epragati.master.dao.RegServiceDAO;
import org.epragati.master.dao.RegistrationDetailDAO;
import org.epragati.master.dao.StagingRegServiceDetailsAutoApprovalLogBackUpDAO;
import org.epragati.master.dao.StagingRegServiceDetailsAutoApprovalLogDAO;
import org.epragati.master.dao.StagingRegistrationDetailsDAO;
import org.epragati.master.dao.TaxDetailsDAO;
import org.epragati.master.dao.UserDAO;
import org.epragati.master.dao.VehicleStoppageDetailsDAO;
import org.epragati.master.dto.ActionDetailsDTO;
import org.epragati.master.dto.ApplicantDetailsDTO;
import org.epragati.master.dto.LockedDetailsDTO;
import org.epragati.master.dto.RegSchedulerDashboardDTO;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.master.dto.StagingRegServiceDetailsAutoApprovalBackUpDTO;
import org.epragati.master.dto.StagingRegServiceDetailsAutoApprovalDTO;
import org.epragati.master.dto.StagingRegistrationDetailsDTO;
import org.epragati.master.dto.TaxDetailsDTO;
import org.epragati.master.dto.UserDTO;
import org.epragati.master.mappers.RegSchedulerDashboardMapper;
import org.epragati.master.service.LogMovingService;
import org.epragati.master.vo.RegSchedulerDashboardVO;
import org.epragati.payment.dto.PaymentTransactionDTO;
import org.epragati.payment.dto.PaymentTransaction_last5d;
import org.epragati.payments.dao.PaymentTransactionDAO;
import org.epragati.payments.service.PaymentGateWayService;
import org.epragati.permits.dao.FiveDayRegistrationDetailDAO;
import org.epragati.permits.dao.PermitDetailsDAO;
import org.epragati.permits.dto.PermitDetailsDTO;
import org.epragati.permits.dto.PermitDetails_last5d;
import org.epragati.regservice.RegistrationService;
import org.epragati.regservice.dao.AadhaarSeedDAO;
import org.epragati.regservice.dao.TokenAuthenticationDAO;
import org.epragati.regservice.dto.ActionDetails;
import org.epragati.regservice.dto.RegServiceDTO;
import org.epragati.regservice.dto.TokenAuthenticationDTO;
import org.epragati.regservice.dto.VehicleStoppageDetailsDTO;
import org.epragati.regservice.vo.VehicleStoppageMVIReportVO;
import org.epragati.reports.service.RCCancellationService;
import org.epragati.rta.service.impl.service.RegistratrionServicesApprovals;
import org.epragati.service.SchedulerService;
import org.epragati.service.notification.MessageTemplate;
import org.epragati.service.notification.NotificationUtil;
import org.epragati.sn.dto.ActionsDetails;
import org.epragati.sn.numberseries.dto.PRPoolDTO;
import org.epragati.util.RoleEnum;
import org.epragati.util.Status;
import org.epragati.util.StatusRegistration;
import org.epragati.util.payment.ServiceEnum;
import org.epragati.util.payment.ServiceEnum.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl implements SchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	@Autowired
	private RegServiceDAO regServiceDAO;

	@Autowired
	private StagingRegistrationDetailsDAO stagingRegistrationDetailsDAO;

	@Autowired
	private RegistrationDetailDAO registrationDetailDAO;

	@Autowired
	private AadhaarSeedDAO aadhaarSeedDAO;

	@Autowired
	private PropertiesDAO propertiesDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private PaymentGateWayService paymentGateWayService;

	@Autowired
	private TokenAuthenticationDAO tokenAuthenticationDAO;

	@Autowired
	private RegistratrionServicesApprovals registrationServices;

	@Autowired
	private RCCancellationService rCCancellationService;

	@Autowired
	private VehicleStoppageDetailsDAO vehicleStoppageDetailsDAO;

	@Autowired
	private NotificationUtil notifications;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private RegSchedulerDashboardDAO regSchedulerDashboardDAO;
	
	@Autowired
	private RegSchedulerDashboardMapper regSchedulerDashboardMapper;
	
	@Autowired
	private LogMovingService logMovingService;

	/********************
	 * RELATED TO LAST FIVE DAYS COLLECTION DATA SAVING *******************
	 * 
	 */

	@Autowired
	private FiveDayApplicantDetailsDAO fapplicantDetailsDAO;

	@Autowired
	private PaymentTransactionDAO  paymentTransactionDAO;
	@Autowired
	private FlowDAO flowDAO;
	
	@Autowired
	private PermitDetailsDAO permitDetailsDAO;
	

	@Autowired
	private TaxDetailsDAO taxDetailsDAO;

	@Autowired
	private FTaxDetailsDAO ftaxDetailsDAO;

	@Autowired
	private FiveDayPaymentTransactionDAO fpaymentTransactionDAO;

	@Autowired
	private FiveDayFlowDAO fflowDAO;

	@Autowired
	private FiveDayPermitDetailsDAO fpermitDetailsDAO;

	@Autowired
	private FiveDayRegistrationDetailDAO fregistrationDetailDAO;

	
	@Override
	public void approvalLockRelease() {

		Pageable pageable = new PageRequest(0, 60);
		List<RegServiceDTO> regServiceList = Collections.emptyList();
		do {
			regServiceList = regServiceDAO.findByLockedDetailsIsNotNull(pageable);
			if (!regServiceList.isEmpty()) {
				for (RegServiceDTO regService : regServiceList) {
					if (regService.getServiceType() != null && !regService.getServiceType().isEmpty() && (regService
							.getServiceType().stream().anyMatch(type -> type.equals(ServiceEnum.VEHICLESTOPPAGE))||
							regService.getServiceType().stream().anyMatch(type -> type.equals(ServiceEnum.VEHICLESTOPPAGEREVOKATION)) || regService.getServiceType().stream().anyMatch(type -> type.equals(ServiceEnum.RCFORFINANCE)))) {
						continue;
					}
					List<LockedDetailsDTO> lockedDetailsList = regService.getLockedDetails();
					if (CollectionUtils.isNotEmpty(lockedDetailsList)) {
						LockedDetailsDTO lockedDetails = lockedDetailsList.stream().findFirst().get();
						logger.info("REG Application No [{}], LockedBy [{}] is Relesed now by Schedular",
								regService.getApplicationNo(), lockedDetails.getLockedBy());
						regService.setLockedDetails(null);
						// logMovingService.moveRegServiceToLog(regService.getApplicationNo());
						regServiceDAO.save(regService);
					}
				}

			} else {
				logger.info("No Records found to Release REG Approval Lock");
			}
		} while (!regServiceList.isEmpty());
	}

	/*
	 * List<RegServiceDTO> regServiceList =
	 * regServiceDAO.findByLockedDetailsIsNotNull(); if (!regServiceList.isEmpty())
	 * { for (RegServiceDTO regService : regServiceList) { List<LockedDetailsDTO>
	 * lockedDetailsList = regService.getLockedDetails(); if
	 * (CollectionUtils.isNotEmpty(lockedDetailsList)) { LockedDetailsDTO
	 * lockedDetails = lockedDetailsList.stream().findFirst().get(); logger.
	 * info("REG Application No [{}], LockedBy [{}] is Relesed now by Schedular",
	 * regService.getApplicationNo(), lockedDetails.getLockedBy());
	 * regService.setLockedDetails(null); //
	 * logMovingService.moveRegServiceToLog(regService.getApplicationNo());
	 * regServiceDAO.save(regService); } } } else {
	 * logger.info("No Records found to Release REG Approval Lock"); }
	 */

	/*
	 * @Override public void handlePrReopenRecords() {
	 * logger.info("Started handlePrReopenRecords"); Pageable pageable = new
	 * PageRequest(0, 1000); Page<GeneratedPrDetailsDTO> generatedPrDetails = null;
	 * do { generatedPrDetails =
	 * generatedPrDetailsDAO.findByIsVerifiedIsNull(pageable);
	 * generatedPrDetails.forEach(details -> { try { if
	 * (!validateVahanDetailsInRegistrationDetails(details.getPrNo()) &&
	 * !validateVahanDetailsInStagingRegistrationDetails(details.getPrNo())) {
	 * PRPoolDTO pRPoolDTO = pRPoolDAO.findByPrNo(details.getPrNo()); if (null !=
	 * pRPoolDTO) { if (pRPoolDTO.getNumberType().equals(BidNumberType.P)) {
	 * pRPoolDTO.setPoolStatus(NumberPoolStatus.LEFTOVER); } else {
	 * pRPoolDTO.setPoolStatus(NumberPoolStatus.REOPEN); }
	 * updateActionsDetails(pRPoolDTO, "Program"); pRPoolDAO.save(pRPoolDTO);
	 * generatedPrDetailsDAO.delete(details); } else { details.setVerified(true);
	 * generatedPrDetailsDAO.save(details); }
	 * 
	 * } else { details.setVerified(true); generatedPrDetailsDAO.save(details); }
	 * 
	 * } catch (BadRequestException e) {
	 * logger.error("Exception while handling handlePrReopenRecords" +
	 * e.getMessage()); } catch (Exception e) {
	 * logger.error("Exception while handling handlePrReopenRecords" + e); } }); }
	 * while (generatedPrDetails.getSize() >= 1000);
	 * logger.info("End handlePrReopenRecords");
	 * 
	 * }
	 */

	public boolean validateVahanDetailsInRegistrationDetails(String prNo) {
		Optional<RegistrationDetailsDTO> regDetails = registrationDetailDAO.findByPrNo(prNo);
		return regDetails.isPresent();
	}

	public boolean validateVahanDetailsInStagingRegistrationDetails(String prNo) {
		Optional<StagingRegistrationDetailsDTO> stagingRegDetails = stagingRegistrationDetailsDAO.findByPrNo(prNo);
		return stagingRegDetails.isPresent();
	}

	public void updateActionsDetails(PRPoolDTO entity, String user) {
		ActionsDetails actionDetailsDTO = new ActionsDetails();
		actionDetailsDTO.setActionBy(user);
		actionDetailsDTO.setActionDatetime(LocalDateTime.now());
		actionDetailsDTO.setReason(entity.getPoolStatus().getDescription());
		actionDetailsDTO.setAction(entity.getPoolStatus().getDescription());

		if (entity.getActionDetailsLog() == null) {
			entity.setActionDetailsLog(new ArrayList<>());
		}
		entity.getActionDetailsLog().add(actionDetailsDTO);
	}

	/*
	 * @Override public void handleTrExpiredRecords() {
	 * 
	 * logger.info("Started handleTrExpiredRecords"); String status = "Active";
	 * Optional<BidConfigMaster> bidConfigMasterOptional =
	 * bidConfigMasterDAO.findByConfigStatus(status);
	 * if(bidConfigMasterOptional.isPresent()) {
	 * doTRExpiredProcess(bidConfigMasterOptional.get(),false);//process for new
	 * register vehiclea
	 * doTRExpiredProcess(bidConfigMasterOptional.get(),true);//process for re
	 * assignment vehiclea }else {
	 * logger.error("BidConfigMaster not defined in DB"); }
	 * logger.info("End handleTrExpiredRecords");
	 * 
	 * }
	 * 
	 * private void doTRExpiredProcess(BidConfigMaster bidConfigMaster,boolean
	 * isReassignment) { int count = 1; while (count < 400) {
	 * 
	 * LocalDateTime requiredDate = LocalDateTime.now()
	 * .minusDays(bidConfigMaster.getBidMaxAllowDays() - 1);
	 * 
	 * List<StagingRegistrationDetailsDTO> stagingDetails; Pageable pageable;
	 * if(!isReassignment) { //OrderByTrGeneratedDateDes pageable = new
	 * PageRequest(0, 50,new Sort(new Order(Direction.DESC, "trGeneratedDate")));
	 * stagingDetails=stagingRegistrationDetailsDAO
	 * .findByApplicationStatusAndTrGeneratedDateLessThanAndPrNoIsNullAndIsTrExpiredIsNull(
	 * StatusRegistration.SPECIALNOPENDING.getDescription(),requiredDate, pageable);
	 * }else { pageable = new PageRequest(0, 50,new Sort(new Order(Direction.DESC,
	 * "reassignmentDoneDate"))); stagingDetails=stagingRegistrationDetailsDAO
	 * .findByApplicationStatusAndIsFromReassigmentAndReassignmentDoneDateLessThanAndPrNoIsNull(
	 * StatusRegistration.SPECIALNOPENDING.getDescription(),true,requiredDate,
	 * pageable); }
	 * 
	 * 
	 * logger.info("Iteratior Count:[{}], size of staging record:,[{}]",count,
	 * stagingDetails.size()); if (stagingDetails.isEmpty()) { break; }
	 * stagingDetails.forEach(st -> { try { doTrexpiredProcess(st); } catch
	 * (BadRequestException e) {
	 * logger.error("Exception while handling handleTrExpiredRecords" +
	 * e.getMessage()); } catch (Exception e) {
	 * logger.error("Exception while handling handleTrExpiredRecords" + e); }
	 * 
	 * }); stagingDetails.clear();
	 * 
	 * pageable = null; count++; }
	 * 
	 * }
	 */
	/*
	 * private void doTrexpiredProcess(StagingRegistrationDetailsDTO st) {
	 * 
	 * Optional<SpecialNumberDetailsDTO> bidWinner = specialNumberDetailsDAO
	 * .findByVehicleDetailsTrNumberAndBidStatus(st.getTrNo(), BidStatus.BIDWIN); if
	 * (!bidWinner.isPresent() && isAlreadyExistInRegDetails(st)) { try {
	 * st.setSpecialNumberRequired(Boolean.FALSE);
	 * st.setPrType(BidNumberType.N.getCode()); st.setIsTrExpired(Boolean.TRUE);
	 * stagingRegistrationDetailsDAO.save(st); rtaService.assignPR(st);
	 * }catch(Exception e) {
	 * logger.error("Exception while handling handleTrExpiredRecords" +
	 * e.getMessage()); if(st.getSchedulerIssues()==null) {
	 * st.setSchedulerIssues(new ArrayList<>()); }
	 * st.getSchedulerIssues().add(LocalDateTime.now()+": where "+Schedulers.
	 * TREXPIRED +", Issue: "+e.getMessage());
	 * stagingRegistrationDetailsDAO.save(st); } }else {
	 * logger.warn("Skipping records _id:{}",st.getApplicationNo());
	 * 
	 * } bidWinner = null; st = null;
	 * 
	 * }
	 */

	/*
	 * private boolean isAlreadyExistInRegDetails(StagingRegistrationDetailsDTO st)
	 * { if (CollectionUtils.isNotEmpty(st.getBidAlterDetails()) &&
	 * st.getBidAlterDetails().stream().anyMatch( bidAlt ->
	 * Arrays.asList(BidStatus.SPNOTREQUIRED,
	 * BidStatus.BIDLEFT).contains(bidAlt.getChangeTo()))) { return false; }
	 * Optional<RegistrationDetailsDTO> regDetailsOptin = registrationDetailDAO
	 * .findByVahanDetailsChassisNumberAndVahanDetailsEngineNumber(st.
	 * getVahanDetails().getChassisNumber(),
	 * st.getVahanDetails().getEngineNumber()); if (regDetailsOptin.isPresent()) {
	 * return false; } return true; }
	 */
	@Override
	public void regReleaseLock() {

		Pageable pageable = new PageRequest(0, 60);
		List<StagingRegistrationDetailsDTO> stagingList = Collections.emptyList();
		do {
			stagingList = stagingRegistrationDetailsDAO.findByLockedDetailsIsNotNull(pageable);
			stagingList.stream().forEach(stagingDto -> {
				List<LockedDetailsDTO> dlLogList;
				try {
					dlLogList = stagingDto.getLockedDetails();
					logger.info("application No [{}], lockedBy[{}]", stagingDto.getApplicationNo(), dlLogList);
					stagingDto.setLockedDetails(null);
					logMovingService.moveStagingToLog(stagingDto.getApplicationNo());
					stagingRegistrationDetailsDAO.save(stagingDto);
					dlLogList.clear();
				} catch (Exception e) {
					logger.error("Exception while handling regRealsedLock" + e.getMessage());
					if (stagingDto.getSchedulerIssues() == null) {
						stagingDto.setSchedulerIssues(new ArrayList<>());
					}
					stagingDto.getSchedulerIssues().add(
							LocalDateTime.now() + ": where " + Schedulers.APPLICATIONLOCKINGMECHANISMS + ", Issue: " + e.getMessage());
					stagingDto.setLockedDetails(null);
					stagingRegistrationDetailsDAO.save(stagingDto);
				}

			});
		} while (!stagingList.isEmpty());

		List<AadhaarSeedDTO> aadhaarSeedDtoList = aadhaarSeedDAO.findByLockAndStatusIn(Boolean.TRUE,
				Arrays.asList(Status.AadhaarSeedStatus.AOAPPROVED, Status.AadhaarSeedStatus.AOREJECTED,
						Status.AadhaarSeedStatus.INITIATED));

		LocalDateTime currentDate = LocalDateTime.now();
		if (!aadhaarSeedDtoList.isEmpty()) {
			for (AadhaarSeedDTO aadhaarSeedDTO : aadhaarSeedDtoList) {
				ActionDetailsDTO actionDetails = new ActionDetailsDTO();
				List<ActionDetailsDTO> actionLog = new ArrayList<>();
				if (aadhaarSeedDTO.getLockedDetailsLog() != null) {
					actionLog = aadhaarSeedDTO.getLockedDetailsLog();
				}
				if (!actionLog.isEmpty()) {
					actionDetails.setActionBy(aadhaarSeedDTO.getUserId());
					actionDetails.setActionDatetime(currentDate);
					actionDetails.setAction("Lock Released");
					actionDetails.setReason("Locked Released By Schedular");
					actionLog.add(actionDetails);
				} else {
					actionDetails.setActionBy(aadhaarSeedDTO.getUserId());
					actionDetails.setActionDatetime(currentDate);
					actionDetails.setAction("Lock Released");
					actionDetails.setReason("Locked Released By Schedular");
					actionLog.add(actionDetails);
				}
				aadhaarSeedDTO.setLockedDetailsLog(actionLog);
				aadhaarSeedDTO.setLock(null);
				aadhaarSeedDTO.setUserId(null);
				aadhaarSeedDAO.save(aadhaarSeedDTO);

			}
		} else {
			logger.info("No records found to release lock for AadhaarSeeding");
		}
	}

	@Override
	public void autoApproveOSApplications() {

		Optional<PropertiesDTO> osDays = propertiesDAO.findByNocAutoApprovalStatusTrue();
		if (!osDays.isPresent()) {
			logger.info("noc auto approvals days not configured in DB : {}", LocalDateTime.now());
		}
		if (osDays.get().getNocAutoApprovalDays() == null) {
			logger.info("Days are not available to run noc auto approval schedular: {}", LocalDateTime.now());

		}
		List<RegServiceDTO> autoApproveApp = regServiceDAO
				.findByApplicationStatusAndOtherStateNOCStatusAndApprovedDateNotNull(StatusRegistration.APPROVED,
						StatusRegistration.NOCVERIFICATIONPENDING);
		if (CollectionUtils.isEmpty(autoApproveApp)) {
			logger.info("No Application available to do auto approvals for os noc verification: {}",
					LocalDateTime.now());
		}
		LocalDateTime date = LocalDateTime.now().minusDays(osDays.get().getNocAutoApprovalDays());

		registrationService.saveOtherStateRecord(
				autoApproveApp.stream().filter(os -> os.getApprovedDate().isBefore(date)).collect(Collectors.toList()),
				Boolean.TRUE, osDays.get().getNocAutoApprovalDays());

	}

	@Override
	public void newRegAutoVerifyPay() {
		List<String> statusList = Arrays.asList(StatusRegistration.PAYMENTPENDING.getDescription(),
				StatusRegistration.SECORINVALIDPENDING.getDescription(),
				StatusRegistration.CITIZENPAYMENTPENDING.getDescription());
		List<String> appIds = new ArrayList<>();
		logger.info("Start new registartions auto Verify Payments");
		for (int i = 0; i < 400; i++) {
			Pageable pageable = new PageRequest(0, 20);
			List<StagingRegistrationDetailsDTO> stagingRegDetails = stagingRegistrationDetailsDAO
					.findByApplicationStatusInAndApplicationNoNotIn(statusList, appIds, pageable);
			logger.info("regRegistrationAutoVerify iterrator No: {} and Size: {}", i, stagingRegDetails.size());
			if (stagingRegDetails.isEmpty()) {
				appIds.clear();
				break;
			}
			stagingRegDetails.stream().forEach(st -> {
				try {
					paymentGateWayService.processToVerifyPaymets(st, false, false, true);

				} catch (Exception e) {
					logger.error("Exception while autoVerifyPayments {}, is: {}", st.getApplicationNo(), e);
					if (st.getSchedulerIssues() == null) {
						st.setSchedulerIssues(new ArrayList<>());
					}
					st.getSchedulerIssues().add(LocalDateTime.now() + ": where " + Schedulers.NEWREGAUTOVERIFYPAY
							+ ", Issue: " + e.getMessage());

					stagingRegistrationDetailsDAO.save(st);
				}
				appIds.add(st.getApplicationNo());
			});
		}
		appIds.clear();
		logger.info("End  new registartions autoVerifyPayments");

	}

	public void regServicesAutoVerify() {
		logger.info("Start registartions services auto verify process");
		List<String> servStatusList = Arrays.asList(StatusRegistration.PAYMENTPENDING.getDescription());
		List<String> appIds = new ArrayList<>();
		for (int i = 0; i < 500; i++) {
			Pageable pageable = new PageRequest(0, 20);
			List<RegServiceDTO> servicesList = regServiceDAO
					.findByApplicationStatusInAndApplicationNoNotIn(servStatusList, appIds, pageable);
			logger.info("services autoVerifyPayments iterrator No: {} and Size:{} ", i, servicesList.size());
			if (servicesList.isEmpty()) {
				appIds.clear();
				break;
			}
			servicesList.stream().forEach(st -> {
				try {
					paymentGateWayService.processVerify(st.getApplicationNo(), false, st.getPaymentTransactionNo(),
							false, true);
				} catch (Exception e) {
					logger.error("Exception while autoVerifyPayments {}, is: {}", st.getApplicationNo(), e);
					if (st.getSchedulerIssues() == null) {
						st.setSchedulerIssues(new ArrayList<>());
					}
					st.getSchedulerIssues().add(LocalDateTime.now() + ": where " + Schedulers.NEWREGAUTOVERIFYPAY
							+ ", Issue: " + e.getMessage());
				}
				appIds.add(st.getApplicationNo());
			});
			servicesList.clear();
		}
		appIds.clear();
		logger.info("End registartions services auto verify process");

	}

	@Override
	public void clearTokenRequests() {
		LocalDateTime time = LocalDateTime.now();
		List<TokenAuthenticationDTO> list = tokenAuthenticationDAO.findByExpirationDateLessThanEqual(time);
		if (CollectionUtils.isNotEmpty(list)) {
			tokenAuthenticationDAO.delete(list);
		}
	}

	/*
	 * @Override public void freshRCRecordsAssin() { List<RegServiceDTO>
	 * regServiceDTOList = new ArrayList<RegServiceDTO>(); List<RegServiceDTO>
	 * regServiceList = regServiceDAO.findByServiceIdsAndApplicationStatusIn(
	 * ServiceEnum.RCFORFINANCE.getId(),
	 * Arrays.asList(StatusRegistration.AOAPPROVED)); if (!regServiceList.isEmpty())
	 * { regServiceList.stream().forEach(regSerdto -> { Long transportValidDays =
	 * ChronoUnit.DAYS.between(regSerdto.getlUpdate().toLocalDate(),
	 * LocalDate.now()); if (transportValidDays >= 14 & !regSerdto.isMviDone() &&
	 * regSerdto.getFlowId().equals(Flow.RCFORFINANCEMVIACTION)) {
	 * regServiceDTOList.addAll(freshRc(regSerdto)); } else {
	 * logger.info("No Records found to Release freshRc for finance MVI No Action");
	 * } }); regServiceDAO.save(regServiceDTOList); }
	 * 
	 * }
	 */

	private List<RegServiceDTO> freshRc(RegServiceDTO regServiceDTO) {
		List<RegServiceDTO> regServiceDTOList = new ArrayList<RegServiceDTO>();
		regServiceDTO.setFlowId(Flow.RCFORFINANCEMVINOACTION);
		registrationServices.moveActionsDetailsToActionDetailsLogs(regServiceDTO);
		regServiceDTO.setCurrentRoles(null);
		regServiceDTO.setActionDetails(null);
		regServiceDTO.setIterationCount(0);
		regServiceDTO.setCurrentIndex(0);
		registrationServices.initiateApprovalProcessFlow(regServiceDTO);
		regServiceDTO.setLockedDetails(null);
		regServiceDTOList.add(regServiceDTO);
		return regServiceDTOList;
	}

	@Override
	public void generateTaxNonPaymentReport() {
		try {
			rCCancellationService.getNonPaymentTaxDetails();
		} catch (Exception e) {
			logger.debug("Exception Occured at getting non payment report: {}", e);
		}
	}

	@Override
	public void stoppageScheduler() {
		List<VehicleStoppageDetailsDTO> listDocs = vehicleStoppageDetailsDAO.findByStausTrueAndRtoCompletedTrue();
		if (listDocs != null && !listDocs.isEmpty()) {
			for (VehicleStoppageDetailsDTO dto : listDocs) {
				// send message to application is pending
				// assign to DTC and send a message
				// Auto approve and send a message to applicant and mvi
				Optional<RegServiceDTO> stagingDetailsDTO = regServiceDAO.findByApplicationNo(dto.getApplicationNo());
				if (stagingDetailsDTO.isPresent()) {
					List<VehicleStoppageMVIReportVO> listOfReports = registrationServices
							.getPendingQuarters(stagingDetailsDTO.get());
					if (listOfReports != null && !listOfReports.isEmpty()) {
						Long leftDays = registrationServices.daysLeftForAutoapproved(stagingDetailsDTO.get());
						if (leftDays <= 0) {
							// TODO auto approved.
							Optional<RegistrationDetailsDTO> regDoc = registrationDetailDAO.findByApplicationNo(
									stagingDetailsDTO.get().getVehicleStoppageDetails().getRegApplicationNo());
							registrationServices.saveMviReportCommon(
									stagingDetailsDTO.get().getVehicleStoppageDetails().getUserId(), listOfReports,
									stagingDetailsDTO.get(), regDoc.get(), Boolean.TRUE, listOfReports, null);
							ActionDetails actions = new ActionDetails();
							actions.setRole("Syastem");
							actions.setUserId("Syastem");
							actions.setlUpdate(LocalDateTime.now());
							// actions.setIpAddress(ipAddress);
							actions.setStatus("AutoApprove");
							if (stagingDetailsDTO.get().getVehicleStoppageDetails().getActions() == null
									|| stagingDetailsDTO.get().getVehicleStoppageDetails().getActions().isEmpty()) {
								stagingDetailsDTO.get().getVehicleStoppageDetails().setActions(Arrays.asList(actions));
							} else {
								stagingDetailsDTO.get().getVehicleStoppageDetails().getActions().add(actions);
							}
							Set<String> rolels = new HashSet<>();
							rolels.add(RoleEnum.MVI.getName());
							if (stagingDetailsDTO.get().getCurrentRoles().contains(RoleEnum.DTC.getName())) {
								stagingDetailsDTO.get().setCurrentRoles(rolels);
							}
							regServiceDAO.save(stagingDetailsDTO.get());
							vehicleStoppageDetailsDAO.save(stagingDetailsDTO.get().getVehicleStoppageDetails());
							// send a message to applicant and mvi
							if (StringUtils
									.isNoneBlank(stagingDetailsDTO.get().getVehicleStoppageDetails().getMviNumber())) {
								notifications.sendNotifications(MessageTemplate.STOPPAGEAUTOAPPROVEDFORMVI.getId(),
										stagingDetailsDTO.get());
							}
							if (stagingDetailsDTO.get().getRegistrationDetails() != null
									&& stagingDetailsDTO.get().getRegistrationDetails().getApplicantDetails() != null
									&& stagingDetailsDTO.get().getRegistrationDetails().getApplicantDetails()
											.getContact() != null
									&& StringUtils.isNoneBlank(stagingDetailsDTO.get().getRegistrationDetails()
											.getApplicantDetails().getContact().getMobile())) {
								notifications.sendNotifications(MessageTemplate.STOPPAGEAUTOAPPROVEDFORCITIZEN.getId(),
										stagingDetailsDTO.get());
							}

						} else if (stagingDetailsDTO.get().getCurrentRoles().contains(RoleEnum.DTC.getName())) {
							// send message to application is pending to DTC
							if (StringUtils
									.isNoneBlank(stagingDetailsDTO.get().getVehicleStoppageDetails().getDtcNumber())) {
								notifications.sendNotifications(MessageTemplate.STOPPAGEDTC.getId(),
										stagingDetailsDTO.get());
							}

						} else {
							Optional<PropertiesDTO> daysConfig = propertiesDAO.findByStoppageDaysStatusTrue();
							LocalDate stoppageDate = stagingDetailsDTO.get().getCreatedDate().toLocalDate();
							if (stagingDetailsDTO.get().getVehicleStoppageDetails().getMviAssignedDate() != null) {
								stoppageDate = stagingDetailsDTO.get().getVehicleStoppageDetails().getMviAssignedDate();
							}
							if (/*
								 * LocalDate.now().equals(stoppageDate.plusDays(daysConfig.get().
								 * getStoppageMvidays())) ||
								 */LocalDate.now()
									.isAfter(stoppageDate.plusDays(daysConfig.get().getStoppageMvidays()))) {
								stagingDetailsDTO.get().getCurrentRoles().add(RoleEnum.DTC.getName());
								List<UserDTO> userList = userDAO
										.findByPrimaryRoleNameOrAdditionalRolesNameAndOfficeOfficeCodeNative(
												RoleEnum.DTC.name(), RoleEnum.DTC.name(),
												stagingDetailsDTO.get().getMviOfficeCode());
								if (userList != null && !userList.isEmpty()) {
									if (StringUtils.isNoneBlank(userList.stream().findFirst().get().getMobile())) {
										stagingDetailsDTO.get().getVehicleStoppageDetails()
												.setDtcNumber(userList.stream().findFirst().get().getMobile());
									}
								}
								ActionDetails actions = new ActionDetails();
								actions.setRole("Syastem");
								actions.setUserId("Syastem");
								actions.setlUpdate(LocalDateTime.now());
								// actions.setIpAddress(ipAddress);
								actions.setStatus("AssignDTC");
								if (stagingDetailsDTO.get().getVehicleStoppageDetails().getActions() == null
										|| stagingDetailsDTO.get().getVehicleStoppageDetails().getActions().isEmpty()) {
									stagingDetailsDTO.get().getVehicleStoppageDetails()
											.setActions(Arrays.asList(actions));
								} else {
									stagingDetailsDTO.get().getVehicleStoppageDetails().getActions().add(actions);
								}
								vehicleStoppageDetailsDAO.save(stagingDetailsDTO.get().getVehicleStoppageDetails());
								regServiceDAO.save(stagingDetailsDTO.get());
								;
							} else {
								// send message to application is pending to MVI
								if (StringUtils.isNoneBlank(
										stagingDetailsDTO.get().getVehicleStoppageDetails().getMviNumber())) {
									notifications.sendNotifications(MessageTemplate.STOPPAGEMVI.getId(),
											stagingDetailsDTO.get());
								}

							}
						}
					}
				}
			}
		}
	}

	


	@Override
	public List<RegSchedulerDashboardVO> regSchedulerForDashBoard() {
		List<RegSchedulerDashboardDTO> schedulerDashboarList=  regSchedulerDashboardDAO.findByStatusTrue();
		if(CollectionUtils.isNotEmpty(schedulerDashboarList)){
			return regSchedulerDashboardMapper.convertEntity(schedulerDashboarList);
		}
		return Collections.emptyList();
	}

	@Override
	public void reApproveServicesRecords(LocalDateTime fromDateTime, LocalDateTime toDateTime,
			List<Integer> servicesIds) {
		List<RegServiceDTO> ids=regServiceDAO.findApplicationStatusAndApprovedDateAndserviceIdsNativeLK(StatusRegistration.APPROVED,fromDateTime,toDateTime,servicesIds);
		ids.stream().forEach(id->{
			RegServiceDTO regServiceDTO= regServiceDAO.findOne(id.getApplicationNo());
			if(null!=regServiceDTO) {
				try {
				registrationServices.reApproveServRecod(regServiceDTO, RoleEnum.AO.getName());
				}catch (Exception e) {
					logger.error("Exception While generating PR from RegServices:{}", regServiceDTO.getApplicationNo());
					if (regServiceDTO.getSchedulerIssues() == null) {
						regServiceDTO.setSchedulerIssues(new ArrayList<>());
					}
					regServiceDTO.getSchedulerIssues().add(LocalDateTime.now() + ": where reApproveServicesRecords manual running" 
							+ ", Issue: " + e.getMessage());
					regServiceDAO.save(regServiceDTO);
				}
			}
			regServiceDTO=null;
		});
		
	}
	
	
	//============= SAS Reports   Collections======== START

		@Override
		public void savePaymentsData() {
			int days = 5;
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO.findByReportType("PAYMENTS");
			if (propertiesOptional.isPresent()) {
				if (propertiesOptional.get().getDays() != null) {
					days = propertiesOptional.get().getDays();
				}

			}
			LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
			LocalDateTime toDate = LocalDateTime.now();
			logger.info("payments Data clean up  Time [{}]", LocalDateTime.now());
			fpaymentTransactionDAO.deleteAll();
			logger.info("payments Data for last [{}] days starting Time [{}]", days, LocalDateTime.now());
			List<PaymentTransactionDTO> paymentTransactionDetails = paymentTransactionDAO
					.findByResponseResponseTimeBetween(fromDate, toDate);

			logger.info("payments Data for last [{}] days end Time [{}] count [{}]", days, LocalDateTime.now(),
					paymentTransactionDetails.size());
			List<PaymentTransaction_last5d> paymentTranasctionsList = new ArrayList<PaymentTransaction_last5d>();
			paymentTransactionDetails.forEach(data -> {

				PaymentTransaction_last5d dest = new PaymentTransaction_last5d();
				try {
					BeanUtils.copyProperties(dest, data);
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception:: [{}]", e);
					logger.error("Exception [{}]", e.getMessage());
				}
				paymentTranasctionsList.add(dest);

			});
			if (CollectionUtils.isNotEmpty(paymentTranasctionsList)) {
				fpaymentTransactionDAO.save(paymentTranasctionsList);
			}
		}

		@Override
		public void saveFlowDetails() {
			
			int days = 5;
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO.findByReportType("FLOWDETAILS");
			if (propertiesOptional.isPresent()) {
				if (propertiesOptional.get().getDays() != null) {
					days = propertiesOptional.get().getDays();
				}

			}
			LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
			LocalDateTime toDate = LocalDateTime.now();

			logger.info("flow Details Data clean up start Time [{}]", LocalDateTime.now());

			fflowDAO.deleteAll();

			logger.info("flow Details Data for last [{}] days starting Time [{}]", days, LocalDateTime.now());

			List<FlowDTO> flowDetails = flowDAO.findByLUpdateBetween(fromDate, toDate);

			logger.info("flow Details Data for last [{}] days end Time [{}] count [{}]", days, LocalDateTime.now(),
					flowDetails.size());

			List<Flow_last5d> flowDetailsList = new ArrayList<Flow_last5d>();
			flowDetails.forEach(data -> {
				Flow_last5d dest = new Flow_last5d();
				try {
					BeanUtils.copyProperties(dest, data);
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception:: [{}]", e);
					logger.error("Exception [{}]", e.getMessage());
				}
				flowDetailsList.add(dest);

			});
			if (CollectionUtils.isNotEmpty(flowDetailsList)) {
				fflowDAO.save(flowDetailsList);
			}
		}

		@Override
		public void savePermitDetails() {
			
			int days = 5;
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO.findByReportType("PERMITDETAILS");
			if (propertiesOptional.isPresent()) {
				if (propertiesOptional.get().getDays() != null) {
					days = propertiesOptional.get().getDays();
				}

			}

			logger.info("permit Details [{}]", days);
			LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
			LocalDateTime toDate = LocalDateTime.now();
			logger.info("permit Details Data clean up start Time [{}]", LocalDateTime.now());

			fpermitDetailsDAO.deleteAll();
			logger.info("permit Details Data for last [{}] days starting Time [{}]", days, LocalDateTime.now());

			List<PermitDetailsDTO> permitDetailsDetails = permitDetailsDAO.findByLUpdateBetween(fromDate, toDate);

			logger.info("permit Details Data for last [{}] days end Time [{}] count [{}]", days, LocalDateTime.now(),
					permitDetailsDetails.size());

			List<PermitDetails_last5d> permitDetailsList = new ArrayList<PermitDetails_last5d>();
			permitDetailsDetails.forEach(data -> {

				PermitDetails_last5d dest = new PermitDetails_last5d();
				try {
					BeanUtils.copyProperties(dest, data);
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception:: [{}]", e);
					logger.error("Exception [{}]", e.getMessage());
				}
				permitDetailsList.add(dest);

			});
			if (CollectionUtils.isNotEmpty(permitDetailsList)) {
				fpermitDetailsDAO.save(permitDetailsList);
			}
			logger.info("permit Details scheduler job done");
		}

		@Override
		public void saveTaxDetails()   {
			int days = 5;
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO.findByReportType("TAXDETAILS");
			if (propertiesOptional.isPresent()) {
				if (propertiesOptional.get().getDays() != null) {
					days = propertiesOptional.get().getDays();
				}

			}
			LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
			LocalDateTime toDate = LocalDateTime.now();
			logger.info("Tax Details Data clean up start Time [{}]", LocalDateTime.now());

			ftaxDetailsDAO.deleteAll();
			logger.info("Tax Details Data for last [{}] days starting Time [{}]", days, LocalDateTime.now());

			List<TaxDetailsDTO> taxDetails = taxDetailsDAO.findByLUpdateBetween(fromDate, toDate);

			logger.info("Tax Details Data for last [{}] days end Time [{}] count [{}]", days, LocalDateTime.now(),
					taxDetails.size());
			List<TaxDetails_last5d> taxDetailsList = new ArrayList<>();
			taxDetails.forEach(data -> {

				TaxDetails_last5d dest = new TaxDetails_last5d();
				try {
					BeanUtils.copyProperties(dest, data);
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception:: [{}]", e);
					logger.error("Exception [{}]", e.getMessage());
				}
				taxDetailsList.add(dest);

			});
			if (CollectionUtils.isNotEmpty(taxDetailsList)) {
				ftaxDetailsDAO.save(taxDetailsList);
			}
		}
		
		
		@Override
		public void saveRegistrationDetails()   {
			int days = 5;
			Optional<PropertiesDTO> propertiesOptional = propertiesDAO.findByReportType("REGDETAILS");
			if (propertiesOptional.isPresent()) {
				if (propertiesOptional.get().getDays() != null) {
					days = propertiesOptional.get().getDays();
				}

			}
			LocalDateTime fromDate = LocalDateTime.now().minusDays(days);// 20
			LocalDateTime toDate = LocalDateTime.now();//

			logger.info("REG Details and Applicatant Details Data clean up start Time [{}]", LocalDateTime.now());

			fapplicantDetailsDAO.deleteAll();
			fregistrationDetailDAO.deleteAll();

			while (fromDate.isBefore(toDate)) {

				logger.info("REG Details Data for last [{}] days starting Time [{}]", days, LocalDateTime.now());

				List<RegistrationDetailsDTO> registrationDetails = registrationDetailDAO.findByLUpdateBetween(fromDate,
						fromDate.plusDays(1));

				logger.info("from Date [{}]  To Date [{}]", fromDate, fromDate.plusDays(1));

				logger.info("REG Details Data for last [{}] days end Time [{}] count [{}]", days, LocalDateTime.now(),
						registrationDetails.size());

				List<ApplicantDetailsDTO> applicantDetails = new ArrayList<ApplicantDetailsDTO>();
				List<RegistrationDetails_last5d> regList = new ArrayList<RegistrationDetails_last5d>();
				registrationDetails.forEach(data -> {
					applicantDetails.add(data.getApplicantDetails());

					RegistrationDetails_last5d dest = new RegistrationDetails_last5d();
					try {
						BeanUtils.copyProperties(dest, data);
					} catch (IllegalAccessException | InvocationTargetException e) {
						logger.debug("Exception:: [{}]", e);
						logger.error("Exception [{}]", e.getMessage());
					}
					regList.add(dest);
				});
				convertApplicantDetails(applicantDetails);
				applicantDetails.clear();
				if (CollectionUtils.isNotEmpty(regList)) {
					fregistrationDetailDAO.save(regList);
				}
				regList.clear();
				fromDate = fromDate.plusDays(1);
			}
		}

		public void convertApplicantDetails(List<ApplicantDetailsDTO> applicantDetails) {
			if (CollectionUtils.isNotEmpty(applicantDetails)) {
				List<ApplicantDetails_last5d> applicantList = new ArrayList<ApplicantDetails_last5d>();
				applicantDetails.forEach(data -> {

					ApplicantDetails_last5d dest = new ApplicantDetails_last5d();
					try {
						if(null!=data) {
						BeanUtils.copyProperties(dest, data);
						}
					} catch (IllegalAccessException | InvocationTargetException e) {
						logger.debug("Exception:: [{}]", e);
						logger.error("Exception [{}]", e.getMessage());
					}
					applicantList.add(dest);
				});
				if (CollectionUtils.isNotEmpty(applicantList)) {

					fapplicantDetailsDAO.save(applicantList);
				}
			}
		}
		//SAS Reporst Collections End  =========
		
		// STAGING REG SERVICE AUTO APPROVAL==========

		@Autowired
		private StagingRegServiceDetailsAutoApprovalLogDAO autoApprovalDAO;

		@Autowired
		private StagingRegServiceDetailsAutoApprovalLogBackUpDAO autoApprovalBackUpDAO;

		@Override
		public void stagingRegServiceAutoApproval() {

			// Getting list of records just one months back from the Current Date .....
			// Means the Records has been for a month in a Database ....
			List<StagingRegServiceDetailsAutoApprovalDTO> listDto = autoApprovalDAO
					.findByAutoApprovalsDate(LocalDate.now().minusMonths(1));

			List<StagingRegServiceDetailsAutoApprovalBackUpDTO> listStagingBackUpDTO = new ArrayList<>();

			for (StagingRegServiceDetailsAutoApprovalDTO stagging : listDto) {
				StagingRegServiceDetailsAutoApprovalBackUpDTO autoApprovalBackUp = new StagingRegServiceDetailsAutoApprovalBackUpDTO();

				org.springframework.beans.BeanUtils.copyProperties(stagging, autoApprovalBackUp);
				listStagingBackUpDTO.add(autoApprovalBackUp);

			}
			if (CollectionUtils.isNotEmpty(listStagingBackUpDTO)) {
			
				autoApprovalBackUpDAO.save(listStagingBackUpDTO);
			}
		
			autoApprovalDAO
					.deleteByApplicationNoIn(listDto.stream().map(r -> r.getApplicationNo()).collect(Collectors.toList()));

		}	
		
}
