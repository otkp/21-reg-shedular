package org.epragati.scheduler;

import java.time.LocalDateTime;

import org.epragati.auditService.AuditLogsService;
import org.epragati.constants.Schedulers;
import org.epragati.org.vahan.port.service.RegVahanPortService;
import org.epragati.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

	/*
	 * @Value("${scheduler.reg.tr.expired.isEnable}") private Boolean
	 * isTrExpiredSchedulerEnable;
	 */

	@Value("${scheduler.reg.pr.reopen.isEnable}")
	private Boolean isPrReopenSchedulerEnable;

	@Autowired
	private AuditLogsService auditLogsService;

	@Autowired
	private SchedulerService schedulerService;

	@Value("${release.reg.lock.process.isEnable}")
	private Boolean isLockReleaseEnable;

	@Value("${os.noc.autoapprove.process.isEnable}")
	private Boolean isOsNocAutoApprovedEnabled;
	
	@Value("${reg.non.payments.report.isEnable:}")
	private Boolean isEnableNonPaymentReport;	
	
	@Value("${reg.vahan.sync.noc.isEnable}")
	private Boolean isEnableRegVshanSyncNoc;
	
	@Value("${reg.vahan.sync.new.isEnable}")
	private Boolean isEnableRegVahanSyncNew;

	@Value("${no.of.records.vahan.sync:25}")
	private int count; 
	
	@Autowired
	private RegVahanPortService regVahanPortService;
	
	
	@Value("${auto.action.reg.services.scheduler.isEnable:false}")
	private Boolean isEnableAutoActionRegServices;

	@Value("${auto.action.new.reg.scheduler.isEnable:false}")
	private Boolean isEnableAutoActionNewReg;

	
	/*
	 * @Autowired private LastFiveDaysCollectionsDAO lastFiveDaysCollectionsDAO;
	 */
	/**
	 * Do cfst sync process
	 */
	/*
	 * @Scheduled(cron = "${scheduler.reg.tr.expired.cron}") public void
	 * handleTrExpiredRecordsScheduler() {
	 * 
	 * LocalDateTime startTime = LocalDateTime.now(); LocalDateTime endTime = null;
	 * Boolean isExecuteSucess = true; String error = null; if
	 * (isTrExpiredSchedulerEnable) {
	 * logger.info("HandleTrExpiredRecords	 scheduler starting at time is now {}",
	 * LocalDateTime.now());
	 * 
	 * try {
	 * 
	 * schedulerService.handleTrExpiredRecords();
	 * 
	 * } catch (Exception ex) { error = ex.getMessage(); isExecuteSucess = false;
	 * logger.error("Exception occured while cfst sync process scheduler running",
	 * ex); } endTime = LocalDateTime.now();
	 * logger.info("TREXPIRED scheduler end at time is now {}", endTime);
	 * 
	 * } else { isExecuteSucess = false; endTime = LocalDateTime.now();
	 * logger.info("TREXPIRED scheduler is skiped at time is now {}", endTime); }
	 * auditLogsService.saveScedhuleLogs(Schedulers.TREXPIRED, startTime, endTime,
	 * isExecuteSucess, error, null); }
	 */

	/*
	 * @Scheduled(cron = "${scheduler.reg.pr.reopen.cron}") public void
	 * handleEnableUnusedPrScheduler() {
	 * 
	 * LocalDateTime startTime = LocalDateTime.now(); LocalDateTime endTime = null;
	 * Boolean isExecuteSucess = true; String error = null;
	 * 
	 * if (isPrReopenSchedulerEnable) {
	 * logger.info("PR Reopen process scheduler starting at time is now {}",
	 * LocalDateTime.now());
	 * 
	 * try {
	 * 
	 * schedulerService.handlePrReopenRecords();
	 * 
	 * } catch (Exception ex) { error = ex.getMessage(); isExecuteSucess = false;
	 * logger.error("Exception occured while cfst sync process scheduler running",
	 * ex); } endTime = LocalDateTime.now();
	 * logger.info("PR Reopen process scheduler end at time is now {}", endTime);
	 * 
	 * } else { isExecuteSucess = false; endTime = LocalDateTime.now();
	 * logger.info("PR Reopen process scheduler is skiped at time is now {}",
	 * endTime); } auditLogsService.saveScedhuleLogs(Schedulers.PRREOPEN, startTime,
	 * endTime, isExecuteSucess, error, null); }
	 */

	@Scheduled(cron = "${application.lock.reg.realese}")
	public void approvalLockRelease() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isLockReleaseEnable) {
			logger.info("REG Approval lock release scheduler starting at time {[]}", LocalDateTime.now());

			try {
				schedulerService.approvalLockRelease();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while releasing REG approval lock [{}] ", ex.getMessage());
			}
			endTime = LocalDateTime.now();
			logger.info("REG Approval role unlock scheduler end at time {[]}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("REG Approval role unlock scheduler is skiped at time {[]}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.REGSERUNLOCK, startTime, endTime, isExecuteSucess, error, null);
	}

	@Scheduled(cron = "${os.nocauto.approve.process.interval}")
	public void osAutoApprovalProcess() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isOsNocAutoApprovedEnabled) {
			logger.info("OS noc auto approval schedular starting at time {}", LocalDateTime.now());

			try {
				schedulerService.autoApproveOSApplications();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while running os noc auto approvals  {} ", ex.getMessage());
			}
			endTime = LocalDateTime.now();
			logger.info("OS noc auto approval schedular end at time {}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("OS noc auto approval schedular is skiped at time {}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.OSNOCAUTOAPPROVALS, startTime, endTime, isExecuteSucess, error,
				null);
	}

	/*@Scheduled(cron = "${reg.new.payments.auto.verify}")
	public void newRegAutoVerifyPay() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isEnableNewRegAutoVerify) {
			logger.info("New Reg Auto Verify Payments schedular starting at time {}", LocalDateTime.now());

			try {
				schedulerService.newRegAutoVerifyPay();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while running New Reg Auto Verify Payment  {} ", ex);
			}
			endTime = LocalDateTime.now();
			logger.info("OS noc auto approval schedular end at time {}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("New Reg Auto Verify Payment schedular is skiped at time {}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.NEWREGAUTOVERIFYPAY, startTime, endTime, isExecuteSucess, error,
				null);
	}

	@Scheduled(cron = "${reg.services.payments.auto.verify}")
	public void regServicesAutoVerify() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isEnableRegServicesAutoVerify) {
			logger.info("Reg services auto verify payments schedular starting at time {}", LocalDateTime.now());

			try {
				schedulerService.regServicesAutoVerify();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while running Reg services auto verify payments {} ", ex);
			}
			endTime = LocalDateTime.now();
			logger.info("Reg services auto verify payments schedular end at time {}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("Reg services auto verify payments schedular is skiped at time {}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.REGSERVICESAUTOVERIFYPAY, startTime, endTime, isExecuteSucess,
				error, null);
	}*/

	/**
	 * This Scheduler is to delete the previous request tokens and hashes
	 */
	@Scheduled(cron = "${reg.token.authentication.requests.clear}")
	public void clearAuthorizationRequests() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.clearTokenRequests();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.error("Exception occured while running Clear Token requests {} ", ex);
			endTime = LocalDateTime.now();
			logger.info("Exception occured while running Clear Token requests {}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.TOKENAUTHENTICATION, startTime, endTime, isExecuteSucess, error,
				null);
	}

	@Scheduled(cron = "${sas_payments}")
	// @Scheduled(cron = "0 50 2 * * ?")
	public void savePaymentData() throws Exception {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.savePaymentsData();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.error("Exception occured while saving SAS_payments  {} ", ex.getMessage());
			endTime = LocalDateTime.now();
			logger.debug("Exception occured while saving SAS_payments {}", ex);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.SAS_PAYMENTS, startTime, endTime, isExecuteSucess, error,
				null);

	}

	@Scheduled(cron = "${sas_flowdetails}")
	// @Scheduled(cron = "0 40 1 * * ?")
	public void saveFlowDetails() throws Exception {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.saveFlowDetails();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.error("Exception occured while saving SAS_saveFlowDetails {} ", ex.getMessage());
			endTime = LocalDateTime.now();
			logger.debug("Exception occured while saving SAS_saveFlowDetails {} {}", ex);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.SAS_FLOWDETAILS, startTime, endTime, isExecuteSucess, error,
				null);

	}

	@Scheduled(cron = "${sas_permitdetails}")
	// @Scheduled(cron = "0 30 1 * * ?")
	public void savePermitDetails() throws Exception {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.savePermitDetails();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.debug("Exception occured while running SAS_savePermitDetails {} ", ex);
			endTime = LocalDateTime.now();
			logger.error("Exception occured while running SAS_savePermitDetails {}", ex.getMessage());
		}
		auditLogsService.saveScedhuleLogs(Schedulers.SAS_PERMITDETAILS, startTime, endTime, isExecuteSucess, error,
				null);

	}
	@Scheduled(cron = "${sas_taxdetails}")
	// @Scheduled(cron = "0 20 1 * * ?")
	public void saveTaxDetails() {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.saveTaxDetails();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.error("Exception occured while running SAS_saveTaxDetails {} ", ex.getMessage());
			endTime = LocalDateTime.now();
			logger.debug("Exception occured while running SAS_saveTaxDetails {}", ex);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.SAS_TAXDETAILS, startTime, endTime, isExecuteSucess, error,
				null);

	}

	@Scheduled(cron = "${sas_registrationdetails}")
	// @Scheduled(cron = "0 0 1 * * ?")
	public void saveRegistrationDetails() throws Exception {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		try {
			schedulerService.saveRegistrationDetails();
			endTime = LocalDateTime.now();
		} catch (Exception ex) {
			error = ex.getMessage();
			isExecuteSucess = false;
			logger.debug("Exception occured while running  SAS_saveRegistrationDetails {} ", ex);
			endTime = LocalDateTime.now();
			logger.error("Exception occured while running Clear SAS_saveRegistrationDetails {}", ex.getMessage());
		}
		auditLogsService.saveScedhuleLogs(Schedulers.SAS_REGISTRATIONDETAILS, startTime, endTime, isExecuteSucess, error,
				null);

	}
	
	/*
	 * @Scheduled(cron = "${freshRc.mvi.no.action}") public void
	 * freshRcMviNoAction() { LocalDateTime startTime = LocalDateTime.now();
	 * LocalDateTime endTime = null; String error = null; Boolean isExecuteSucess =
	 * true; try { schedulerService.freshRCRecordsAssin(); endTime =
	 * LocalDateTime.now(); } catch (Exception ex) { error = ex.getMessage();
	 * isExecuteSucess = false; endTime = LocalDateTime.now(); logger.
	 * error("Exception occured while releasing freshRC for finance MVI no action record [{}] "
	 * , ex.getMessage()); } auditLogsService.saveScedhuleLogs(Schedulers.FRESHRC,
	 * startTime, endTime, isExecuteSucess, error, null); }
	 */
	
	
	@Scheduled(cron = "${reg.non.payments.report}")
	public void nonPaymentReport() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isEnableNonPaymentReport) {
			logger.info("Generating tax nonpayment report started at : [{}]", LocalDateTime.now());
			try {
				schedulerService.generateTaxNonPaymentReport();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while generating non tax payment report [{}] ", ex.getMessage());
			}
			endTime = LocalDateTime.now();
			logger.info("Generating tax nonpayment report ended at  [{}]", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.error("Generating tax nonpayment report skipped at time {[]}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.NONPAYMENTREPORT, startTime, endTime, isExecuteSucess, error, null);
	}
	
	@Scheduled(cron = "${reg.vahan.noc.sync}")
	public void regVahanSyncNoc() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isEnableRegVshanSyncNoc) {
			logger.info("Registration Details vahan sync noc data  starting at time :{}", LocalDateTime.now());

			try {
				regVahanPortService.getRegVahanSyncRecordsNoc(count);
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while running Registration Details vahan sync old data :{} ", ex);
			}
			endTime = LocalDateTime.now();
			logger.info("Registration Details vahan sync noc data schedular end at time :{}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("Registration Details vahan sync noc data schedular is skiped at time :{}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.VAHANSYNCNOC, startTime, endTime, isExecuteSucess,
				error, null);
	}
	
	@Scheduled(cron = "${reg.vahan.new.sync}")
	public void regVahanSyncNew() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		if (isEnableRegVahanSyncNew) {
			logger.info("Registration Details vahan sync new schedular starting at time :{}", LocalDateTime.now());

			try {
				regVahanPortService.getRegVahanSyncNewRecords(count);
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while running Registration Details vahan sync new :{} ", ex);
			}
			endTime = LocalDateTime.now();
			logger.info("Registration Details vahan sync new schedular end at time :{}", endTime);
		} else {
			endTime = LocalDateTime.now();
			isExecuteSucess = false;
			logger.info("Registration Details vahan sync new schedular is skiped at time :{}", endTime);
		}
		auditLogsService.saveScedhuleLogs(Schedulers.VAHANSYNC, startTime, endTime, isExecuteSucess,
				error, null);
	}
	
	@Scheduled(cron = "${stoppage.process}")
	public void stoppageScheduler() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = null;
		String error = null;
		Boolean isExecuteSucess = true;
		
			logger.info("REG Approval lock release scheduler starting at time {[]}", LocalDateTime.now());

			try {
				schedulerService.stoppageScheduler();
			} catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while releasing REG approval lock [{}] ", ex.getMessage());
			}
			endTime = LocalDateTime.now();
			logger.info("REG Approval role unlock scheduler end at time {[]}", endTime);
		
		auditLogsService.saveScedhuleLogs(Schedulers.STOPPAGE, startTime, endTime, isExecuteSucess, error, null);
	}
	
	//@Scheduled(cron = "${auto.approval}")
		@Scheduled(cron = "0 10 18 * * *")
		public void stagingRegServiceAutoApproval() {
			LocalDateTime startTime = LocalDateTime.now();
			LocalDateTime endTime = null;
			String error = null;
			Boolean isExecuteSucess = true;
			try {
				schedulerService.stagingRegServiceAutoApproval();
			}catch (Exception ex) {
				error = ex.getMessage();
				isExecuteSucess = false;
				logger.error("Exception occured while releasing REG approval lock [{}] ", ex.getMessage());
			}
			endTime = LocalDateTime.now();
			logger.info("REG Approval role unlock scheduler end at time {[]}", endTime);

			auditLogsService.saveScedhuleLogs(Schedulers.AUTO_APPROVAL, startTime, endTime, isExecuteSucess, error, null);
			
			
		}
	
	
}
