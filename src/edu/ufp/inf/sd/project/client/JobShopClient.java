package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.*;
import edu.ufp.inf.sd.project.util.geneticalgorithm.CrossoverStrategies;
import edu.ufp.inf.sd.project.util.geneticalgorithm.GeneticAlgorithmJSSP;
import edu.ufp.inf.sd.project.util.tabusearch.TabuSearchJSSP;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class JobShopClient {

    /**
     * Context for connecting a RMI client MAIL_TO_ADDR a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */

    private JobShopFactoryRI jobShopFactoryRI;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            JobShopClient hwc=new JobShopClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public JobShopClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(JobShopClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);
                
                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                jobShopFactoryRI = (JobShopFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return jobShopFactoryRI;
    }
    
    private void playService() {
        try {
            boolean b = this.jobShopFactoryRI.register("tiago","123");
            checkRegister(b);
            boolean c = this.jobShopFactoryRI.register("miguel","1234");
            checkRegister(c);

            JobShopSessionRI jobShopSessionRI = this.jobShopFactoryRI.login("tiago","123");
            JobShopSessionRI jobShopSessionRI1 = this.jobShopFactoryRI.login("miguel","1234");

            assert jobShopSessionRI != null;
            jobShopSessionRI.listJobGroup();

            assert jobShopSessionRI1 != null;
            jobShopSessionRI.listJobGroup();


            jobShopSessionRI.createJobGroup(1,200,"teste",1);
            jobShopSessionRI.createJobGroup(3,300,"teste2",0);
            jobShopSessionRI1.createJobGroup(2,300,"teste3",1);

            if(jobShopSessionRI.createWorker(1)) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Success 1");
            }
            if(jobShopSessionRI.createWorker(1)) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Success 2");
            }
            if(jobShopSessionRI.createWorker(2)) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Success 3 ");

            }
            if(jobShopSessionRI1.createWorker(2)) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Success 4");
            }
            if(jobShopSessionRI1.createWorker(3)) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Success 4");
            }

            jobShopSessionRI.assocWorker(1,1);
            jobShopSessionRI.assocWorker(1,2);
            jobShopSessionRI1.assocWorker(1,3);

            print(jobShopSessionRI.listJobGroup());
            print(jobShopSessionRI1.listJobGroup());
            jobShopSessionRI.printWorkers();
            //============ Call TS remote service ============
           // String jsspInstancePath = "edu/ufp/inf/sd/project/data/la01.txt";
           // int makespan = this.jobShopRI.runTS(jsspInstancePath);
           // Logger.getLogger(this.getClass().getName()).log(Level.INFO,
           //         "[TS] Makespan for {0} = {1}",
           //         new Object[]{jsspInstancePath,String.valueOf(makespan)});


            //============ Call GA ============
            //String queue = "jssp_ga";
            //String resultsQueue = queue + "_results";
            //CrossoverStrategies strategy = CrossoverStrategies.ONE;
            //Logger.getLogger(this.getClass().getName()).log(Level.INFO,
            //       "GA is running for {0}, check queue {1}",
            //      new Object[]{jsspInstancePath,resultsQueue});
            //GeneticAlgorithmJSSP ga = new GeneticAlgorithmJSSP(jsspInstancePath, queue, strategy);
           //ga.run();


        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void checkRegister(boolean b) {
        if(b) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User Registered");
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User not Registered");
        }
    }

    private void print(String s) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, s);
    }
}
