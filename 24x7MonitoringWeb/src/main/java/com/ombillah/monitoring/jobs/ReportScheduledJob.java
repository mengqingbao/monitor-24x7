package com.ombillah.monitoring.jobs;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.ombillah.monitoring.domain.ReportContent;
import com.ombillah.monitoring.domain.ReportSchedule;

/**
 * Quartz Job that will send the scheduled report to the user.
 * 
 * @author Oussama M Billah
 * 
 */
public class ReportScheduledJob {

	private JavaMailSender mailSender;
	private List<ReportContent> reportContents;
	private ReportSchedule reportSchedule;

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setReports(List<ReportContent> reports) {
		this.reportContents = reports;
	}

	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}
 

	public void sendReport() throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(reportSchedule.getReportEmail());
		helper.setSubject(reportSchedule.getFrequency() + " Report for: " + reportSchedule.getItemName());
		String body = "<b>" + reportContents.get(0).getItemName() + "</b><br/><br/>";
		body += "<tabe>" +
					"<tr>" +
						"<th style='padding:0 15px 0 10px;'>Time</th>" +
						"<th style='padding:0 15px 0 10px;'>Item Name</th>" +
						"<th style='padding:0 15px 0 10px;'>Average</th>" +
						"<th style='padding:0 15px 0 10px;'>Max</th>" +
						"<th style='padding:0 15px 0 10px;'>min</th>" +
					"</tr>";
		for(ReportContent report : reportContents) {
			body += "<tr>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getReportTime() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getItemName() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getAverage() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getMax() + "</td>" +
						"<td style='padding:0 15px 0 10px;'>" + report.getMin() + "</td>" +
					"</tr>";
		}
		body += "</table>";
		helper.setText(body, true);
		mailSender.send(message);
	}
}
