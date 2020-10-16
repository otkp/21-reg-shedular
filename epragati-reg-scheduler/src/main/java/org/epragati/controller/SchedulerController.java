package org.epragati.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.epragati.common.service.DeemedAutoActionService;
import org.epragati.constants.MessageKeys;
import org.epragati.jwt.JwtUser;
import org.epragati.master.vo.RegSchedulerDashboardVO;
import org.epragati.scheduler.Scheduler;
import org.epragati.security.utill.JwtTokenUtil;
import org.epragati.service.SchedulerService;
import org.epragati.util.AppMessages;
import org.epragati.util.GateWayResponse;
import org.epragati.util.RequestMappingUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(RequestMappingUrls.SCHEDULER_CLASS_LEVEL_URL)
@RestController
@CrossOrigin
public class SchedulerController {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

	@Autowired
	private AppMessages appMessages;

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private DeemedAutoActionService deemedAutoActionService;

	@GetMapping(value = "approvalLockRelease", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> approvalLockRelease(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.approvalLockRelease();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(value = "regInlock", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> regInlock(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.regReleaseLock();
			return new GateWayResponse<>(true, HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		

	}
	
	
	@GetMapping(value = "stoppageScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> stoppageScheduler(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.stoppageScheduler();
			return new GateWayResponse<>(true, HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}
	
	@GetMapping(value = "autoApproveOSNOCApplications", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> autoApproveOSNOCApplications(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.autoApproveOSApplications();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping(value = "newRegAutoVerifyPay", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> newRegAutoVerifyPay(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.newRegAutoVerifyPay();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping(value = "regServicesAutoVerify", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> regServicesAutoVerify(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.regServicesAutoVerify();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping(value = "clearAuthorizationRequests", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> clearAuthorizationRequests(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.clearTokenRequests();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	//============ SAS COLLECTIONS RELATED CONTROLLERS START ============

	@GetMapping(value = "paymentScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> paymentScheduler() {
		try {
			schedulerService.savePaymentsData();
		} catch (Exception e) {
			logger.debug("Exception occur at paymentScheduler [{}]", e);
			logger.info("Exception occur at paymentScheduler [{}]", e.getMessage());
		}
		return new GateWayResponse<>(true, HttpStatus.OK, "success");

	}

	@GetMapping(value = "flowDetailsScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> saveFlowDetails() {
		try {
			schedulerService.saveFlowDetails();
		} catch (Exception e) {
			logger.debug("Exception occur at flow Details Scheduler [{}]", e);
			logger.info("Exception occur at flow Details Scheduler [{}]", e.getMessage());
		}
		return new GateWayResponse<>(true, HttpStatus.OK, "success");

	}

	@GetMapping(value = "taxDetailsScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> taxDetailsScheduler() {
		try {
			schedulerService.saveTaxDetails();
		} catch (Exception e) {
			logger.debug("Exception occur at tax Details Scheduler [{}]", e);
			logger.info("Exception occur at tax Details Scheduler [{}]", e.getMessage());
		}
		return new GateWayResponse<>(true, HttpStatus.OK, "success");

	}

	@GetMapping(value = "regDetailsScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> regDetailsScheduler() {
		try {
			schedulerService.saveRegistrationDetails();
		} catch (Exception e) {
			logger.debug("Exception occur at reg Details Scheduler [{}]", e);
			logger.info("Exception occur at reg Details Scheduler [{}]", e.getMessage());
		}
		return new GateWayResponse<>(true, HttpStatus.OK, "success");

	}

	@GetMapping(value = "permitDetailsScheduler", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> savePermitDetails() {
		try {
			schedulerService.savePermitDetails();
		} catch (Exception e) {
			logger.debug("Exception occur at permit Details Scheduler [{}]", e);
			logger.info("Exception occur at permit Details Scheduler [{}]", e.getMessage());
		}
		return new GateWayResponse<>(true, HttpStatus.OK, "success");

	}
	
	//============ SAS COLLECTIONS RELATED CONTROLLERS END ============
	
	/*
	 * @GetMapping(value = "freshRcMviNoAction", produces = {
	 * MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }) public
	 * GateWayResponse<?> freshRcMviNoAction(@RequestHeader("Authorization") String
	 * token) { JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token); try {
	 * if (jwtUser == null) { return new
	 * GateWayResponse<>(MessageKeys.NO_AUTHORIZATION); }
	 * scheduler.freshRcMviNoAction(); return new GateWayResponse<>(HttpStatus.OK,
	 * "success"); } catch (Exception e) {
	 * logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION),
	 * e.getMessage()); return new
	 * GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()); } }
	 */
	
	@GetMapping(value = "nonPaymentReport", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> nonPaymentReport(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			scheduler.nonPaymentReport();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping(value = "regVahanSyncNoc", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> regVahanSyncNoc(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			scheduler.regVahanSyncNoc();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping(value = "regVahanSyncNew", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> regVahanSyncNew(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			scheduler.regVahanSyncNoc();
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	
	@GetMapping(value = "regSchedulerForDashBoard", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> regSchedulerForDashBoard(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			List<RegSchedulerDashboardVO> regSchedulers = schedulerService.regSchedulerForDashBoard();
			if(CollectionUtils.isNotEmpty(regSchedulers)){
				return new GateWayResponse<>(regSchedulers);
			}
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return new GateWayResponse<>(HttpStatus.OK, "success");
	}
	
	@GetMapping(value = "reApproveServicesRecords", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> reApproveServicesRecords(@RequestHeader("Authorization") String token,
			@RequestParam(value = "fromDateTime") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDateTime,
			@RequestParam(value = "toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDateTime,
			@RequestParam(value = "servicesIds") List<Integer> servicesIds) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) { 
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.reApproveServicesRecords(fromDateTime.atTime(0, 0, 0, 0000), toDateTime.atTime(23, 59, 59, 999), servicesIds);
			return new GateWayResponse<>(HttpStatus.OK, "success");
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e); 
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}	
	
	@GetMapping(value = "newRegDeemedAutoAction", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> newRegDeemedAutoApproval(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			deemedAutoActionService.newRegDeemedAutoAction();
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return new GateWayResponse<>(HttpStatus.OK, "success");
	}
	
	@GetMapping(value = "regServiceAutoAction", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> regServiceAutoAction(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			deemedAutoActionService.regServiceAutoAction();
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return new GateWayResponse<>(HttpStatus.OK, "success");
	}
	
	@GetMapping(value = "generateTaxNonPaymentReport", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> generateTaxNonPaymentReport(@RequestHeader("Authorization") String token) {
		JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
		try {
			if (jwtUser == null) {
				return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
			}
			schedulerService.generateTaxNonPaymentReport();
		} catch (Exception e) {
			logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return new GateWayResponse<>(HttpStatus.OK, "success");
	}
	
	
	//============ STAGING_REG_SERVICES_AUTO_APPROVAL_LOGS COLLECTIONS RELATED CONTROLLERS ============
	
			@GetMapping(value = "stagingRegServiceAutoApproval", produces = {MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
			public GateWayResponse<?> stagingRegServiceAutoApproval(@RequestHeader("Authorization") String token) {
				
				JwtUser jwtUser = jwtTokenUtil.getUserDetailsByToken(token);
				try {
					if(jwtUser == null) {
						return new GateWayResponse<>(MessageKeys.NO_AUTHORIZATION);
					}
					schedulerService.stagingRegServiceAutoApproval();
					return new GateWayResponse<>(HttpStatus.OK, "success");
				}catch (Exception e) {
					logger.error(appMessages.getResponseMessage(MessageKeys.MESSAGE_EXCEPTION), e); 
					return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
				}
			
			}
		
	
	
	
}
