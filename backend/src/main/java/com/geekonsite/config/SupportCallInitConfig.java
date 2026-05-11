package com.geekonsite.config;

import com.geekonsite.model.IVROption;
import com.geekonsite.model.TroubleshootingStep;
import com.geekonsite.repository.IVROptionRepository;
import com.geekonsite.repository.TroubleshootingStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@Order(2)
public class SupportCallInitConfig implements CommandLineRunner {
    
    @Autowired
    private IVROptionRepository ivrOptionRepository;
    
    @Autowired
    private TroubleshootingStepRepository troubleshootingStepRepository;
    
    @Override
    public void run(String... args) {
        log.info("Initializing Support Call System...");
        
        // Initialize IVR Options
        if (ivrOptionRepository.count() == 0) {
            initIVROptions();
            log.info("✓ IVR options initialized");
        }
        
        // Initialize Troubleshooting Flows
        if (troubleshootingStepRepository.count() == 0) {
            initTroubleshootingFlows();
            log.info("✓ Troubleshooting flows initialized");
        }
        
        log.info("✓ Support Call System ready");
    }
    
    private void initIVROptions() {
        // 1. Computer Issues
        IVROption computer = new IVROption();
        computer.setOptionKey("1");
        computer.setDisplayText("Computer/Laptop Issues");
        computer.setDescription("Problems with your computer or laptop");
        computer.setCategory(IVROption.IssueCategory.COMPUTER_ISSUE);
        computer.setPriority(1);
        computer.setActive(true);
        computer.setTroubleshootingFlowId("COMPUTER_FLOW");
        ivrOptionRepository.save(computer);
        
        // 2. Printer Issues
        IVROption printer = new IVROption();
        printer.setOptionKey("2");
        printer.setDisplayText("Printer Problems");
        printer.setDescription("Printer not working or connectivity issues");
        printer.setCategory(IVROption.IssueCategory.PRINTER_ISSUE);
        printer.setPriority(2);
        printer.setActive(true);
        printer.setTroubleshootingFlowId("PRINTER_FLOW");
        ivrOptionRepository.save(printer);
        
        // 3. Network/WiFi
        IVROption network = new IVROption();
        network.setOptionKey("3");
        network.setDisplayText("Network & WiFi Issues");
        network.setDescription("Internet connectivity or WiFi problems");
        network.setCategory(IVROption.IssueCategory.NETWORK_WIFI);
        network.setPriority(3);
        network.setActive(true);
        network.setTroubleshootingFlowId("NETWORK_FLOW");
        ivrOptionRepository.save(network);
        
        // 4. Software
        IVROption software = new IVROption();
        software.setOptionKey("4");
        software.setDisplayText("Software Problems");
        software.setDescription("Software installation or usage issues");
        software.setCategory(IVROption.IssueCategory.SOFTWARE_PROBLEM);
        software.setPriority(4);
        software.setActive(true);
        software.setTroubleshootingFlowId("SOFTWARE_FLOW");
        ivrOptionRepository.save(software);
        
        // 5. Virus/Malware
        IVROption virus = new IVROption();
        virus.setOptionKey("5");
        virus.setDisplayText("Virus & Malware Removal");
        virus.setDescription("Suspicious activity or virus detection");
        virus.setCategory(IVROption.IssueCategory.VIRUS_MALWARE);
        virus.setPriority(5);
        virus.setActive(true);
        virus.setTroubleshootingFlowId("VIRUS_FLOW");
        ivrOptionRepository.save(virus);
        
        // 6. Account Access
        IVROption account = new IVROption();
        account.setOptionKey("6");
        account.setDisplayText("Account Access Issues");
        account.setDescription("Password reset or account login problems");
        account.setCategory(IVROption.IssueCategory.ACCOUNT_ACCESS);
        account.setPriority(6);
        account.setActive(true);
        account.setTroubleshootingFlowId("ACCOUNT_FLOW");
        ivrOptionRepository.save(account);
        
        // 7. Billing
        IVROption billing = new IVROption();
        billing.setOptionKey("7");
        billing.setDisplayText("Billing & Payment");
        billing.setDescription("Questions about charges or payments");
        billing.setCategory(IVROption.IssueCategory.BILLING_INQUIRY);
        billing.setPriority(7);
        billing.setActive(true);
        ivrOptionRepository.save(billing);
        
        // 8. General Support
        IVROption general = new IVROption();
        general.setOptionKey("8");
        general.setDisplayText("General Support");
        general.setDescription("Other technical support questions");
        general.setCategory(IVROption.IssueCategory.GENERAL_SUPPORT);
        general.setPriority(8);
        general.setActive(true);
        ivrOptionRepository.save(general);
    }
    
    private void initTroubleshootingFlows() {
        // Computer Flow - Step 1
        TroubleshootingStep compStep1 = new TroubleshootingStep();
        compStep1.setFlowId("COMPUTER_FLOW");
        compStep1.setStepId("COMP_01");
        compStep1.setQuestion("Is your computer turning on when you press the power button?");
        compStep1.setDescription("Check if the computer shows any signs of power");
        
        TroubleshootingStep.Option compOpt1 = new TroubleshootingStep.Option();
        compOpt1.setOptionKey("yes");
        compOpt1.setDisplayText("Yes, it turns on");
        compOpt1.setNextStepId("COMP_02");
        compOpt1.setResolvesIssue(false);
        
        TroubleshootingStep.Option compOpt2 = new TroubleshootingStep.Option();
        compOpt2.setOptionKey("no");
        compOpt2.setDisplayText("No, nothing happens");
        compOpt2.setResolvesIssue(false);
        compOpt2.setNextStepId("COMP_NO_POWER");
        
        compStep1.setOptions(Arrays.asList(compOpt1, compOpt2));
        troubleshootingStepRepository.save(compStep1);
        
        // Computer Flow - Step 2 (Screen issue)
        TroubleshootingStep compStep2 = new TroubleshootingStep();
        compStep2.setFlowId("COMPUTER_FLOW");
        compStep2.setStepId("COMP_02");
        compStep2.setQuestion("Do you see anything on the screen when it turns on?");
        compStep2.setDescription("Check if display is working");
        
        TroubleshootingStep.Option comp2Opt1 = new TroubleshootingStep.Option();
        comp2Opt1.setOptionKey("yes");
        comp2Opt1.setDisplayText("Yes, I can see the screen");
        comp2Opt1.setResolvesIssue(false);
        comp2Opt1.setNextStepId("COMP_03");
        
        TroubleshootingStep.Option comp2Opt2 = new TroubleshootingStep.Option();
        comp2Opt2.setOptionKey("no");
        comp2Opt2.setDisplayText("No, screen is blank/black");
        comp2Opt2.setResolvesIssue(false);
        comp2Opt2.setNextStepId(null); // Will escalate
        
        compStep2.setOptions(Arrays.asList(comp2Opt1, comp2Opt2));
        troubleshootingStepRepository.save(compStep2);
        
        // Computer Flow - Step 3 (Slow performance)
        TroubleshootingStep compStep3 = new TroubleshootingStep();
        compStep3.setFlowId("COMPUTER_FLOW");
        compStep3.setStepId("COMP_03");
        compStep3.setQuestion("Is your computer running slowly?");
        compStep3.setDescription("Performance check");
        
        TroubleshootingStep.Option comp3Opt1 = new TroubleshootingStep.Option();
        comp3Opt1.setOptionKey("yes");
        comp3Opt1.setDisplayText("Yes, it's very slow");
        comp3Opt1.setResolvesIssue(false);
        comp3Opt1.setNextStepId(null); // Remote assistance
        
        TroubleshootingStep.Option comp3Opt2 = new TroubleshootingStep.Option();
        comp3Opt2.setOptionKey("no");
        comp3Opt2.setDisplayText("No, speed is fine");
        comp3Opt2.setResolvesIssue(false);
        comp3Opt2.setNextStepId("COMP_04");
        
        compStep3.setOptions(Arrays.asList(comp3Opt1, comp3Opt2));
        troubleshootingStepRepository.save(compStep3);
        
        // Computer Flow - Step 4 (Software crash)
        TroubleshootingStep compStep4 = new TroubleshootingStep();
        compStep4.setFlowId("COMPUTER_FLOW");
        compStep4.setStepId("COMP_04");
        compStep4.setQuestion("Are you getting error messages or program crashes?");
        compStep4.setDescription("Software issue check");
        
        TroubleshootingStep.Option comp4Opt1 = new TroubleshootingStep.Option();
        comp4Opt1.setOptionKey("yes");
        comp4Opt1.setDisplayText("Yes, programs keep crashing");
        comp4Opt1.setResolvesIssue(false);
        comp4Opt1.setNextStepId(null); // Remote assistance
        
        TroubleshootingStep.Option comp4Opt2 = new TroubleshootingStep.Option();
        comp4Opt2.setOptionKey("no");
        comp4Opt2.setDisplayText("No, everything works normally");
        comp4Opt2.setResolvesIssue(true);
        comp4Opt2.setResolutionMessage("It appears your computer is working normally now. If issues persist, please contact support again.");
        
        compStep4.setOptions(Arrays.asList(comp4Opt1, comp4Opt2));
        compStep4.setIsFinalStep(true);
        troubleshootingStepRepository.save(compStep4);
        
        // Network Flow - Step 1
        TroubleshootingStep netStep1 = new TroubleshootingStep();
        netStep1.setFlowId("NETWORK_FLOW");
        netStep1.setStepId("NET_01");
        netStep1.setQuestion("Are other devices in your home able to connect to WiFi?");
        netStep1.setDescription("Check if issue is device-specific or network-wide");
        
        TroubleshootingStep.Option netOpt1 = new TroubleshootingStep.Option();
        netOpt1.setOptionKey("yes");
        netOpt1.setDisplayText("Yes, other devices work fine");
        netOpt1.setResolvesIssue(false);
        netOpt1.setNextStepId("NET_02");
        
        TroubleshootingStep.Option netOpt2 = new TroubleshootingStep.Option();
        netOpt2.setOptionKey("no");
        netOpt2.setDisplayText("No, nothing can connect");
        netOpt2.setResolvesIssue(false);
        netOpt2.setNextStepId("NET_ROUTER");
        
        netStep1.setOptions(Arrays.asList(netOpt1, netOpt2));
        troubleshootingStepRepository.save(netStep1);
        
        // Network Flow - Step 2
        TroubleshootingStep netStep2 = new TroubleshootingStep();
        netStep2.setFlowId("NETWORK_FLOW");
        netStep2.setStepId("NET_02");
        netStep2.setQuestion("Have you tried turning WiFi off and on again on your device?");
        netStep2.setDescription("Basic WiFi reset");
        
        TroubleshootingStep.Option net2Opt1 = new TroubleshootingStep.Option();
        net2Opt1.setOptionKey("yes_fixed");
        net2Opt1.setDisplayText("Yes, and it fixed the issue");
        net2Opt1.setResolvesIssue(true);
        net2Opt1.setResolutionMessage("Great! Turning WiFi off and on again resolved the connection issue.");
        
        TroubleshootingStep.Option net2Opt2 = new TroubleshootingStep.Option();
        net2Opt2.setOptionKey("yes_not_fixed");
        net2Opt2.setDisplayText("Yes, but still not working");
        net2Opt2.setResolvesIssue(false);
        net2Opt2.setNextStepId("NET_03");
        
        TroubleshootingStep.Option net2Opt3 = new TroubleshootingStep.Option();
        net2Opt3.setOptionKey("no");
        net2Opt3.setDisplayText("Not yet");
        net2Opt3.setResolvesIssue(false);
        net2Opt3.setNextStepId(null); // Guide them to do it
        
        netStep2.setOptions(Arrays.asList(net2Opt1, net2Opt2, net2Opt3));
        troubleshootingStepRepository.save(netStep2);
        
        // Printer Flow - Step 1
        TroubleshootingStep printStep1 = new TroubleshootingStep();
        printStep1.setFlowId("PRINTER_FLOW");
        printStep1.setStepId("PRT_01");
        printStep1.setQuestion("Is the printer turned on and showing any lights?");
        printStep1.setDescription("Check printer power status");
        
        TroubleshootingStep.Option prtOpt1 = new TroubleshootingStep.Option();
        prtOpt1.setOptionKey("yes");
        prtOpt1.setDisplayText("Yes, it's on");
        prtOpt1.setResolvesIssue(false);
        prtOpt1.setNextStepId("PRT_02");
        
        TroubleshootingStep.Option prtOpt2 = new TroubleshootingStep.Option();
        prtOpt2.setOptionKey("no");
        prtOpt2.setDisplayText("No, no lights");
        prtOpt2.setResolvesIssue(false);
        prtOpt2.setNextStepId(null); // Escalate
        
        printStep1.setOptions(Arrays.asList(prtOpt1, prtOpt2));
        troubleshootingStepRepository.save(printStep1);
        
        // Software Flow - Step 1
        TroubleshootingStep swStep1 = new TroubleshootingStep();
        swStep1.setFlowId("SOFTWARE_FLOW");
        swStep1.setStepId("SW_01");
        swStep1.setQuestion("Can you restart the problematic software and tell me if the issue persists?");
        swStep1.setDescription("Software restart test");
        
        TroubleshootingStep.Option swOpt1 = new TroubleshootingStep.Option();
        swOpt1.setOptionKey("yes");
        swOpt1.setDisplayText("Yes, issue still occurs");
        swOpt1.setResolvesIssue(false);
        swOpt1.setNextStepId(null); // Remote assistance
        
        TroubleshootingStep.Option swOpt2 = new TroubleshootingStep.Option();
        swOpt2.setOptionKey("no");
        swOpt2.setDisplayText("No, restart fixed it");
        swOpt2.setResolvesIssue(true);
        swOpt2.setResolutionMessage("Restarting the software resolved the issue. If it happens again, please contact support.");
        
        swStep1.setOptions(Arrays.asList(swOpt1, swOpt2));
        swStep1.setIsFinalStep(true);
        troubleshootingStepRepository.save(swStep1);
        
        log.info("✓ Troubleshooting flows created: Computer, Network, Printer, Software");
    }
}
