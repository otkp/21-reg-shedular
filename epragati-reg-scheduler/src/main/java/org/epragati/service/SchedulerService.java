package org.epragati.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.epragati.master.vo.RegSchedulerDashboardVO;

public interface SchedulerService {

	void approvalLockRelease();

	// void handlePrReopenRecords();

	/* void handleTrExpiredRecords(); */

	void regReleaseLock();

	void autoApproveOSApplications();

	void newRegAutoVerifyPay();

	void regServicesAutoVerify();

	void clearTokenRequests();
	//void freshRCRecordsAssin();
	
	void generateTaxNonPaymentReport();

	void stoppageScheduler();

	

	List<RegSchedulerDashboardVO> regSchedulerForDashBoard();
	
	void reApproveServicesRecords(LocalDateTime fromDateTime,LocalDateTime toDateTime,List<Integer> servicesIds); 

	
	// SAS REPORT RELATED COLLECTIONS START=========
	void savePaymentsData();

	void saveFlowDetails();

	void savePermitDetails();

	void saveTaxDetails();

	void saveRegistrationDetails();

	// SAS REPORT RELATED COLLECTIONS END=========
	
	// STAGING REG SERVICE AUTO APPROVAL==========
	void stagingRegServiceAutoApproval();
	
	
	
}
