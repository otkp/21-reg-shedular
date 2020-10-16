package org.epragati.scheduler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.actions.dao.SuspensionDAO;
import org.epragati.actions.dto.RCActionsDTO;
import org.epragati.auditService.AuditLogsService;
import org.epragati.constants.Schedulers;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.RegistrationDetailDAO;
import org.epragati.master.dto.RegistrationDetailsDTO;
import org.epragati.service.SchedulerService;
import org.epragati.service.notification.MessageTemplate;
import org.epragati.service.notification.NotificationTemplates;
import org.epragati.service.notification.NotificationUtil;
import org.epragati.util.Status;
import org.epragati.util.Status.RCActionStatus;
import org.epragati.util.StatusRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CfstScheduler {

	private static final Logger logger = LoggerFactory.getLogger(CfstScheduler.class);

	@Value("${release.lock.process.isEnable}")
	private Boolean isLockReleaseEnable;
	
	
	@Autowired
	private NotificationUtil notifications;
	
	@Autowired
	private NotificationTemplates notificationTemplate;

	@Autowired
	private AuditLogsService auditLogsService;
	
	@Value("${RC.autorevocation.isEnable}")
	private Boolean isEnableDLAutoRevocation;
	
	@Autowired
	private SuspensionDAO suspensionDAO;
	
	@Autowired
	private RegistrationDetailDAO registrationDetailDAO;
	
	@Autowired
	private SchedulerService schedulerService;
	
	

	@Scheduled(cron = "${application.lock.realese}")
	public void regReleaseLock() {
		
		LocalDateTime startTime=LocalDateTime.now();
		LocalDateTime endTime=null;
		Boolean isExecuteSucess=true;
		String error=null;
		if (isLockReleaseEnable) {
			logger.info("Registration lock release scheduler starting at time is now {}", LocalDateTime.now());

			try {
				schedulerService.regReleaseLock();
			} catch (Exception ex) {
				error= ex.getMessage();
				isExecuteSucess=false;
				logger.error("Exception occured while releasing lock for users roles ", ex);
			}
			endTime=LocalDateTime.now();
			logger.info("Registration role unlock scheduler end at time is now {}", endTime);
		} else {
			isExecuteSucess=false;
			endTime=LocalDateTime.now();
			logger.info("Registration role unlock scheduler is skiped at time is now {}", LocalDateTime.now());
		}
		auditLogsService.saveScedhuleLogs(Schedulers.APPLICATIONLOCKINGMECHANISMS,startTime,endTime,isExecuteSucess,error,null);
	}
	
/**
 * 
 */
	
	@Scheduled(cron = "${RC.autorevocation}")
	public void handleAutoRevokationWhenSuspended() {
		List<RCActionsDTO> rcActionsDTOs = new ArrayList<>();
		//SchedulerDTO schedulerDTO = null;
		
		LocalDateTime startTime = LocalDateTime.now();
		String message=StringUtils.EMPTY;
		//saveSchedulerLog("DLAUTOREVOCATION", startTime, null, message);
		try {
			if (isEnableDLAutoRevocation) {
				logger.info("handle AutoRevocation When Suspended");
				List<RCActionsDTO> RCActionsDTOList = suspensionDAO
						.findByActionStatusOrderByRcActionsDetailsActionDateDesc(Status.RCActionStatus.SUSPEND);
				if (CollectionUtils.isNotEmpty(RCActionsDTOList)) {
					startTime = LocalDateTime.now();
					for (RCActionsDTO actionsList : RCActionsDTOList) {
						if (actionsList.getRcActionsDetails().getToDate().equals(LocalDate.now())) {
							actionsList.setActionStatus(Status.RCActionStatus.REVOKED);
							actionsList.getRcActionsDetails().setActionBy("Auto Recovation by Schedular");
							rcActionsDTOs.add(actionsList);
							suspensionDAO.save(rcActionsDTOs);
							Optional<RegistrationDetailsDTO> optinalInput = registrationDetailDAO
									.findByPrNo(actionsList.getPrNo());
							if (optinalInput.isPresent()) {
								optinalInput.get().setApplicationStatus(StatusRegistration.PRGENERATED.toString());
								registrationDetailDAO.save(optinalInput.get());
								sendRevocationNotification(optinalInput.get());

							}
						}

					}
				}

			}
		} catch (Exception exception) {
			message=	exception.getMessage();
			logger.error(" handleAutoRevokationWhenSuspended {}", exception);
		}
	//saveSchedulerLog("RCAUTOREVOCATION", startTime, LocalDateTime.now(), message);
	}
	private void sendRevocationNotification(RegistrationDetailsDTO dlDetails) {
		// Sending Revocation reminder notifications
		if (dlDetails.getApplicantDetails() != null) {
			try {
				if (dlDetails.getActionStatus()!=null || dlDetails.getActionStatus().equalsIgnoreCase(RCActionStatus.SUSPEND.toString())) {
					try {
						notifications.sendEmailNotification(notificationTemplate::fillTemplate, MessageTemplate.RC_REVOKATION.getId(), dlDetails,
								dlDetails.getApplicantDetails().getContact().getEmail());
						notifications.sendEmailNotification(notificationTemplate::fillTemplate, MessageTemplate.RC_REVOKATION.getId(), dlDetails,
								dlDetails.getApplicantDetails().getContact().getMobile());
					} catch (IOException e) {

						throw new BadRequestException("Failed to send notifications");
					}
				}
			} catch (Exception e) {
				logger.error("Exception while sending revocation alerts reminder alerts:{}", e);
			}
		}
	}
	
	
	
	
}
