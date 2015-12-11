/*******************************************************************************
 * Copyright (C) 2015  ORO e ISMB
 * Questo e' il main del programma di ottimizzazione VRPTW
 * Il programma prende in input un file csv con i clienti ed un csv di configurazione (deposito e veicoli) e restituisce in output il file delle route ed un file sintetico di dati dei viaggi.
 * L'algoritmo ultizzato e' un Large Neighborhood 
 ******************************************************************************/

package main;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.SAXException;

import generatedJAXB.*;
import generatedJAXB.Algorithm.Construction;
import generatedJAXB.Algorithm.Strategy;
import generatedJAXB.Algorithm.Strategy.SearchStrategies;
import generatedJAXB.InsertionType.ConsiderFixedCosts;
import generatedJAXB.SearchStrategyType.Modules;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.algorithm.selector.SelectBest;
import jsprit.core.algorithm.termination.TimeTermination;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.instance.reader.SolomonReader;
import jsprit.util.Examples;
import main.OROoptions.CONSTANTS;
import main.OROoptions.PARAMS;

public class Main {
			
	public static void main(String[] args) {
		/*// Some preparation - create output folder
		//System.out.println("start");
		Examples.createOutputFolder();
		
		// Read input parameters
		OROoptions options = new OROoptions(args);
		
		for(int r=0; r<(int)options.get(CONSTANTS.REPETITION); r++) {
			// Time tracking
			long startTime = System.currentTimeMillis();
			// Create a vrp problem builder
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			// A solomonReader reads solomon-instance files, and stores the required information in the builder.
			new SolomonReader(vrpBuilder).read("input/" + options.get(PARAMS.INSTANCE));
			VehicleRoutingProblem vrp = vrpBuilder.build();
			// Create the instace and solve the problem
			VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, 
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG));
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME));
			
			// Solve the problem
			Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
			// Extract the best solution
			VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);
									
			// Print solution on a file
			OROutils.write(solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT));
			// Print solution on the screen (optional)
			//SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
			// Draw solution on the screen (optional)
			//new GraphStreamViewer(vrp, solution).labelWith(Label.ID).setRenderDelay(10).display();
			//System.out.println("end");
		}*/
		
		try {
			//tryAllConfigurations(args);
			//tryOneConfiguration(args);
			//tryMoreStrategies(args);
			tryOneConfigWithNStrat(args);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			System.err.println("Exception while marshalling xml file");
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			System.err.println("Exception while marshalling xml file");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void setTimeLimit(VehicleRoutingAlgorithm vra, long timeMilliSec) {
		TimeTermination tterm = new TimeTermination(timeMilliSec);
		vra.setPrematureAlgorithmTermination(tterm);
		vra.addListener(tterm);
	}
	
	/**
	 * @param args
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public static void tryAllConfigurations(String[] args) throws JAXBException, SAXException 
	{
		ObjectFactory of = new ObjectFactory();
		
		//create algorithm
		Algorithm alg = of.createAlgorithm();
		//alg.setIterations(BigInteger.valueOf(1024));
		alg.setMaxIterations(BigInteger.valueOf(1024));
		InsertionTypeEnum insTypeEnum = InsertionTypeEnum.BEST_INSERTION;
		InsertionType insertion = of.createInsertionType();
		insertion.setName(insTypeEnum);
		ConsiderFixedCosts consFixed = of.createInsertionTypeConsiderFixedCosts();
		consFixed.setValue("true");
		consFixed.setWeight(1.0);
		Construction construction = of.createAlgorithmConstruction();
		construction.setInsertion(insertion);
		alg.setConstruction(construction);
		
		//build strategy
		Strategy strategy = of.createAlgorithmStrategy();
		strategy.setMemory(BigInteger.valueOf(3));
		alg.setStrategy(strategy);
		SearchStrategies searchStrategies = of.createAlgorithmStrategySearchStrategies();
		strategy.setSearchStrategies(searchStrategies);
		
		//try the search strategy with 2 search strategies
		SearchStrategyType searchStrategy1 = of.createSearchStrategyType();
		searchStrategy1.setName("strategy_1");
		searchStrategies.getSearchStrategy().add(searchStrategy1);
		SearchStrategyType searchStrategy2 = of.createSearchStrategyType();
		searchStrategy2.setName("strategy_2");
		searchStrategies.getSearchStrategy().add(searchStrategy2);
		
		List<SelectorType> list = new ArrayList<SelectorType>();
		SelectorType st = of.createSelectorType();
		st.setName(SelectorTypeEnum.SELECT_BEST);
		list.add(st);
		st = of.createSelectorType();
		st.setName(SelectorTypeEnum.SELECT_RANDOMLY);
		list.add(st);
		
		for (SelectorType s1 : list)
		{
			for (SelectorType s2 : list)
			{
				searchStrategy1.setSelector(s1);
				searchStrategy2.setSelector(s2);
				
				/* 
				 * probability = 0.0 means try all the combinations
				 * starting from 0.2: try only (0.2,0.8) (0.3,0.7) (0.4,0.6) (0.5,0.5)   
				 */
				double probability = 0.2;  
				for ( ; probability <= 0.5 ; probability += 0.1 )
				{
					
					searchStrategy1.setProbability(probability);
					searchStrategy2.setProbability(1-probability);
					
					List<AcceptorType> accList = new ArrayList<AcceptorType>();
					AcceptorType at = of.createAcceptorType();
					at.setName(AcceptorTypeEnum.ACCEPT_NEW_REMOVE_FIRST);
					accList.add(at);
					at = of.createAcceptorType();
					at.setName(AcceptorTypeEnum.ACCEPT_NEW_REMOVE_WORST);
					accList.add(at);
					at = of.createAcceptorType();
					at = of.createAcceptorType();
					at.setName(AcceptorTypeEnum.SCHRIMPF_ACCEPTANCE);
					accList.add(at);
					at.setName(AcceptorTypeEnum.EXPERIMENTAL_SCHRIMPF_ACCEPTANCE);
					accList.add(at);
					at = of.createAcceptorType();
					at.setName(AcceptorTypeEnum.GREEDY_ACCEPTANCE);
					accList.add(at);
					at = of.createAcceptorType();
					at.setName(AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST);
					accList.add(at);
					
					
					for ( Iterator<AcceptorType> it1 = accList.iterator(); it1.hasNext(); )
					{
						AcceptorType a1 = it1.next();
						a1.setAlpha(0.0);
						//a1.setInitialThreshold(0.0);
						a1.setWarmup(0);
						
						searchStrategy1.setAcceptor(a1);
						for ( Iterator<AcceptorType> it2 = accList.iterator(); it2.hasNext(); )
						{
							AcceptorType a2 = it2.next();
							searchStrategy2.setAcceptor(a2);
							a2.setAlpha(0.0);
							//a2.setInitialThreshold(0.0);
							a2.setWarmup(0);
							
							for (ModuleTypeEnum e1 : ModuleTypeEnum.values())
							{
								ModuleType module1 = of.createModuleType();
								ModuleType module2 = of.createModuleType();
								module1.setName(e1);
								
								for (ModuleTypeEnum e2 : ModuleTypeEnum.values())
								{
									
									module2.setName(e2);
									Modules modules1 = of.createSearchStrategyTypeModules();
									modules1.getModule().add(module1);
									Modules modules2 = of.createSearchStrategyTypeModules();
									modules2.getModule().add(module2);
									searchStrategy1.setModules(modules1);
									searchStrategy2.setModules(modules2);
									
									if (e1.equals(ModuleTypeEnum.RUIN_AND_RECREATE) &&
											e2.equals(ModuleTypeEnum.RUIN_AND_RECREATE))
									{
										RuinAndRecreateGroupType rr1 = of.createRuinAndRecreateGroupType();
										RuinAndRecreateGroupType rr2 = of.createRuinAndRecreateGroupType();
										module1.setRuinAndRecreateGroup(rr1);
										module2.setRuinAndRecreateGroup(rr2);
										
										for (RuinTypeEnum ruins1 : RuinTypeEnum.values())
										{
											RuinType ruin1 = of.createRuinType();
											ruin1.setName(ruins1);
											ruin1.setShare(0.5);
											rr1.setRuin(ruin1);
											for (RuinTypeEnum ruins2 : RuinTypeEnum.values())
											{
												RuinType ruin2 = of.createRuinType();
												ruin2.setName(ruins2);
												ruin2.setShare(0.5);
												rr2.setRuin(ruin2);
												
												for (InsertionTypeEnum ins1 : InsertionTypeEnum.values())
												{
													InsertionType i1 = of.createInsertionType();
													i1.setName(ins1);
													rr1.setInsertion(i1);
													for (InsertionTypeEnum ins2 : InsertionTypeEnum.values())
													{
														InsertionType i2 = of.createInsertionType();
														i2.setName(ins2);
														rr2.setInsertion(i2);
														
														if (ins1.equals(InsertionTypeEnum.BEST_INSERTION) &&
																ins2.equals(InsertionTypeEnum.BEST_INSERTION)) 
														// REGRET_INSERTION IS REALLY SLOW
														{
															//parsing xml config file
															String description = getDescription(alg);
															
															JAXBContext context = JAXBContext.newInstance(Algorithm.class);
															Marshaller marshaller = context.createMarshaller();
															marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
															marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.w3schools.com NewXMLSchema.xsd");
															marshaller.marshal(alg, new File("input/algorithmConfig.xml"));
															marshaller.marshal(alg, System.out);
															getSolution(description,args);
														}
														
														
													}
												}
											}
										}
									}
								}
							}
						}
						it1.remove();
					}
					
				}
			}
		}
	}
	
	public static void tryOneConfiguration(String[] args) throws JAXBException, SAXException
	{
		ObjectFactory of = new ObjectFactory();
		
		//create algorithm
		Algorithm alg = of.createAlgorithm();
		//alg.setIterations(BigInteger.valueOf(1024));
		alg.setMaxIterations(BigInteger.valueOf(1024));
		InsertionTypeEnum insTypeEnum = InsertionTypeEnum.REGRET_INSERTION;
		InsertionType insertion = of.createInsertionType();
		insertion.setName(insTypeEnum);
		ConsiderFixedCosts consFixed = of.createInsertionTypeConsiderFixedCosts();
		consFixed.setValue("true");
		consFixed.setWeight(1.0);
		Construction construction = of.createAlgorithmConstruction();
		construction.setInsertion(insertion);
		insertion.setConsiderFixedCosts(consFixed);
		alg.setConstruction(construction);
		
		//build strategy
		Strategy strategy = of.createAlgorithmStrategy();
		strategy.setMemory(BigInteger.valueOf(3)); //memory setting
		alg.setStrategy(strategy);
		SearchStrategies searchStrategies = of.createAlgorithmStrategySearchStrategies();
		strategy.setSearchStrategies(searchStrategies);
		
		//try the search strategy with 2 search strategies
		SearchStrategyType searchStrategy1 = of.createSearchStrategyType();
		searchStrategy1.setName("strategy_1");
		searchStrategies.getSearchStrategy().add(searchStrategy1);
		SearchStrategyType searchStrategy2 = of.createSearchStrategyType();
		searchStrategy2.setName("strategy_2");
		searchStrategies.getSearchStrategy().add(searchStrategy2);
		/*SearchStrategyType searchStrategy3 = of.createSearchStrategyType();
		searchStrategy3.setName("strategy_3");
		searchStrategies.getSearchStrategy().add(searchStrategy3);*/
		
		SelectorType st1 = of.createSelectorType();
		st1.setName(SelectorTypeEnum.SELECT_RANDOMLY);
		
		SelectorType st2 = of.createSelectorType();
		st2.setName(SelectorTypeEnum.SELECT_RANDOMLY);
		
		/*SelectorType st3 = of.createSelectorType();
		st3.setName(SelectorTypeEnum.SELECT_RANDOMLY);*/
		
		searchStrategy1.setSelector(st1);
		searchStrategy2.setSelector(st2);
		//searchStrategy3.setSelector(st3);
		double probability = 0.5;
		searchStrategy1.setProbability(probability);
		searchStrategy2.setProbability(1-probability);
		//searchStrategy3.setProbability(0.2);
		
		AcceptorType a1 = of.createAcceptorType();
		a1.setName(AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST);
		AcceptorType a2 = of.createAcceptorType();
		a2.setName(AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST);
	/*	AcceptorType a3 = of.createAcceptorType();
		a3.setName(AcceptorTypeEnum.ACCEPT_NEW_REMOVE_WORST);*/
		
		//setting acceptors
		searchStrategy1.setAcceptor(a1);
		searchStrategy2.setAcceptor(a2);
		//searchStrategy3.setAcceptor(a3);
		a1.setAlpha(0.0);
		a1.setWarmup(0);
		a2.setAlpha(0.0);
		a2.setWarmup(0);
		a2.setAlpha(0.0);
		//a3.setWarmup(0);
		
		//setting module
		ModuleType module1 = of.createModuleType();
		ModuleType module2 = of.createModuleType();
		//ModuleType module3 = of.createModuleType();
		module1.setName(ModuleTypeEnum.RUIN_AND_RECREATE);
		module2.setName(ModuleTypeEnum.RUIN_AND_RECREATE);
		//module3.setName(ModuleTypeEnum.RUIN_AND_RECREATE);
		Modules modules1 = of.createSearchStrategyTypeModules();
		modules1.getModule().add(module1);
		Modules modules2 = of.createSearchStrategyTypeModules();
		modules2.getModule().add(module2);
		/*Modules modules3 = of.createSearchStrategyTypeModules();
		modules3.getModule().add(module3);*/
		searchStrategy1.setModules(modules1);
		searchStrategy2.setModules(modules2);
		//searchStrategy3.setModules(modules3);
		RuinAndRecreateGroupType rrg1 = of.createRuinAndRecreateGroupType();
		module1.setRuinAndRecreateGroup(rrg1);
		RuinAndRecreateGroupType rrg2 = of.createRuinAndRecreateGroupType();
		module2.setRuinAndRecreateGroup(rrg2);
		/*RuinAndRecreateGroupType rrg3 = of.createRuinAndRecreateGroupType();
		module3.setRuinAndRecreateGroup(rrg3);*/
		
		//setting ruin module
		RuinType ruin1 = of.createRuinType();
		ruin1.setName(RuinTypeEnum.RANDOM_RUIN);
		ruin1.setShare(0.5);
		rrg1.setRuin(ruin1);
		RuinType ruin2 = of.createRuinType();
		ruin2.setName(RuinTypeEnum.RANDOM_RUIN);
		ruin2.setShare(0.5);
		rrg2.setRuin(ruin2);
		/*RuinType ruin3 = of.createRuinType();
		ruin3.setName(RuinTypeEnum.RANDOM_RUIN);
		ruin3.setShare(0.5);
		rrg3.setRuin(ruin3);*/
		
		//setting insertion module
		InsertionType insertion1 = of.createInsertionType();
		insertion1.setName(InsertionTypeEnum.REGRET_INSERTION);
		rrg1.setInsertion(insertion1);
		InsertionType insertion2 = of.createInsertionType();
		insertion2.setName(InsertionTypeEnum.REGRET_INSERTION);
		rrg2.setInsertion(insertion2);
		/*InsertionType insertion3 = of.createInsertionType();
		insertion3.setName(InsertionTypeEnum.REGRET_INSERTION);
		rrg3.setInsertion(insertion3);*/
		
		//create description of the algorithm 
		String description = getDescription(alg);
		
		//parsing Java class into XML file: input/algorithmConfig.xml
		JAXBContext context = JAXBContext.newInstance(Algorithm.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.w3schools.com NewXMLSchema.xsd");
		marshaller.marshal(alg, new File("input/algorithmConfig.xml"));
		marshaller.marshal(alg, System.out); //print xml on the screen
		
		//try the created configuration
		getSolution(description,args);
		
	}
	
	public static void tryMoreStrategies(String[] args) throws JAXBException, SAXException
	{
		ObjectFactory of = new ObjectFactory();
		
		//create algorithm
		Algorithm alg = of.createAlgorithm();
		//alg.setIterations(BigInteger.valueOf(1024*20));
		alg.setMaxIterations(BigInteger.valueOf(1024));
		InsertionTypeEnum insTypeEnum = InsertionTypeEnum.BEST_INSERTION;
		InsertionType insertion = of.createInsertionType();
		insertion.setName(insTypeEnum);
		ConsiderFixedCosts consFixed = of.createInsertionTypeConsiderFixedCosts();
		consFixed.setValue("true");
		consFixed.setWeight(1.0);
		Construction construction = of.createAlgorithmConstruction();
		construction.setInsertion(insertion);
		insertion.setConsiderFixedCosts(consFixed);
		alg.setConstruction(construction);
		
		//create strategy
		Strategy strategy = of.createAlgorithmStrategy();
		strategy.setMemory(BigInteger.valueOf(3)); //memory setting
		alg.setStrategy(strategy);
		
		List<SelectorTypeEnum> selList = new ArrayList<SelectorTypeEnum>();
		selList.add(SelectorTypeEnum.SELECT_BEST);
		selList.add(SelectorTypeEnum.SELECT_RANDOMLY);
		
		// try all combinations for equals strategies
		for ( Iterator<SelectorTypeEnum> its1 = selList.iterator(); its1.hasNext(); )
		{		
			SelectorTypeEnum se1 = its1.next();
			for ( Iterator<SelectorTypeEnum> its2 = selList.iterator(); its2.hasNext(); )
			{		
				SelectorTypeEnum se2 = its2.next();
				List<AcceptorTypeEnum> accList = new ArrayList<AcceptorTypeEnum>();
				accList.add(AcceptorTypeEnum.ACCEPT_NEW_REMOVE_FIRST);
				accList.add(AcceptorTypeEnum.ACCEPT_NEW_REMOVE_WORST);
				accList.add(AcceptorTypeEnum.GREEDY_ACCEPTANCE);
				accList.add(AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST);
				for ( Iterator<AcceptorTypeEnum> ita1 = accList.iterator(); ita1.hasNext(); )
				{
					AcceptorTypeEnum ae1 = ita1.next();
					for ( Iterator<AcceptorTypeEnum> ita2 = accList.iterator(); ita2.hasNext(); )
					{
						AcceptorTypeEnum ae2 = ita2.next();
						List<RuinTypeEnum> rList = new ArrayList<RuinTypeEnum>();
						rList.add(RuinTypeEnum.RADIAL_RUIN);
						rList.add(RuinTypeEnum.RANDOM_RUIN);
						
						for ( Iterator<RuinTypeEnum> itr1 = rList.iterator(); itr1.hasNext(); )
						{
							RuinTypeEnum re1 = itr1.next();
							for ( Iterator<RuinTypeEnum> itr2 = rList.iterator(); itr2.hasNext(); )
							{
								RuinTypeEnum re2 = itr2.next();
								double share = 0.5;
								List<InsertionTypeEnum> iList = new ArrayList<InsertionTypeEnum>();
								iList.add(InsertionTypeEnum.BEST_INSERTION);
								iList.add(InsertionTypeEnum.REGRET_INSERTION);
								
								for ( Iterator<InsertionTypeEnum> iti1 = iList.iterator(); iti1.hasNext(); )
								{
									InsertionTypeEnum ie1 = iti1.next();
									for ( Iterator<InsertionTypeEnum> iti2 = iList.iterator(); iti2.hasNext(); )
									{
										InsertionTypeEnum ie2 = iti2.next();
										//build N equals strategy
										// N = 15
										SearchStrategies searchStrategies = of.createAlgorithmStrategySearchStrategies();
										strategy.setSearchStrategies(searchStrategies);
										configureNStrategies(15,searchStrategies,se1,ae1,re1,ie1,share,0.5,0);
										configureNStrategies(15,searchStrategies,se2,ae2,re2,ie2,share,0.5,15);
										
										//create description of the algorithm 
										String description = getDescription(alg);
										
										//parsing Java class into XML file: input/algorithmConfig.xml
										JAXBContext context = JAXBContext.newInstance(Algorithm.class);
										Marshaller marshaller = context.createMarshaller();
										marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
										marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.w3schools.com NewXMLSchema.xsd");
										marshaller.marshal(alg, new File("input/algorithmConfig.xml"));
										marshaller.marshal(alg, System.out); //print xml on the screen
										
										//try the created configuration
										getSolution(description,args);
									}
									iti1.remove();
								}
							}
							itr1.remove();
						}
					}
					ita1.remove();
				}
			}
			its1.remove();
		}
	}
	
	private static void tryOneConfigWithNStrat(String[] args) throws JAXBException, SAXException
	{
		ObjectFactory of = new ObjectFactory();
		
		//create algorithm
		Algorithm alg = of.createAlgorithm();
		//alg.setIterations(BigInteger.valueOf(1024));
		alg.setMaxIterations(BigInteger.valueOf(1024*100));
		InsertionTypeEnum insTypeEnum = InsertionTypeEnum.REGRET_INSERTION;
		InsertionType insertion = of.createInsertionType();
		insertion.setAllowVehicleSwitch(true);
		insertion.setName(insTypeEnum);
		ConsiderFixedCosts consFixed = of.createInsertionTypeConsiderFixedCosts();
		consFixed.setValue("true");
		consFixed.setWeight(1.0);
		Construction construction = of.createAlgorithmConstruction();
		construction.setInsertion(insertion);
		insertion.setConsiderFixedCosts(consFixed);
		alg.setConstruction(construction);
		
		//create strategy
		Strategy strategy = of.createAlgorithmStrategy();
		strategy.setMemory(BigInteger.valueOf(2048)); //memory setting
		alg.setStrategy(strategy);
		
		SearchStrategies searchStrategies = of.createAlgorithmStrategySearchStrategies();
		strategy.setSearchStrategies(searchStrategies);
		
		/*int i = 0;
		for (SelectorTypeEnum se : SelectorTypeEnum.values())
			for (AcceptorTypeEnum ae : AcceptorTypeEnum.values())
				for (RuinTypeEnum re : RuinTypeEnum.values())
					for (InsertionTypeEnum ie : InsertionTypeEnum.values())
					{
						configureNStrategies(1,
								searchStrategies,
								se,
								ae,
								re,
								ie,
								0.5,  //share
								1.0,  //probability
								i);   // consequently configured strategies
						i++;
					}*/
						
		
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_BEST,
				AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST,
				RuinTypeEnum.RANDOM_RUIN,
				InsertionTypeEnum.REGRET_INSERTION,
				0.5,  //share
				1.0,  //probability
				0);   // consequently configured strategies
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_RANDOMLY,
				AcceptorTypeEnum.GREEDY_ACCEPTANCE_MIN_VEH_FIRST,
				RuinTypeEnum.RADIAL_RUIN,
				InsertionTypeEnum.BEST_INSERTION,
				0.5,  //share
				1.0,  //probability
				1);   // consequently configured strategies
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_RANDOMLY,
				AcceptorTypeEnum.ACCEPT_NEW_REMOVE_WORST,
				RuinTypeEnum.RADIAL_RUIN,
				InsertionTypeEnum.REGRET_INSERTION,
				0.5,  //share
				1.0,  //probability
				2);   // consequently configured strategies
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_BEST,
				AcceptorTypeEnum.GREEDY_ACCEPTANCE,
				RuinTypeEnum.RANDOM_RUIN,
				InsertionTypeEnum.BEST_INSERTION,
				0.5,  //share
				1.0,  //probability
				3);   // consequently configured strategies
		
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_BEST,
				AcceptorTypeEnum.ACCEPT_NEW_REMOVE_WORST,
				RuinTypeEnum.RANDOM_RUIN,
				InsertionTypeEnum.BEST_INSERTION,
				0.5,  //share
				1.0,  //probability
				4);   // consequently configured strategies
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_RANDOMLY,
				AcceptorTypeEnum.GREEDY_ACCEPTANCE,
				RuinTypeEnum.RADIAL_RUIN,
				InsertionTypeEnum.BEST_INSERTION,
				0.5,  //share
				1.0,  //probability
				5);   // consequently configured strategies
		configureNStrategies(1,
				searchStrategies,
				SelectorTypeEnum.SELECT_RANDOMLY,
				AcceptorTypeEnum.GREEDY_ACCEPTANCE,
				RuinTypeEnum.RANDOM_RUIN,
				InsertionTypeEnum.REGRET_INSERTION,
				0.5,  //share
				1.0,  //probability
				6);   // consequently configured strategies
		
		
		
		
		
		//create description of the algorithm 
		String description = getDescription(alg);
		
		//parsing Java class into XML file: input/algorithmConfig.xml
		JAXBContext context = JAXBContext.newInstance(Algorithm.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.w3schools.com NewXMLSchema.xsd");
		marshaller.marshal(alg, new File("input/algorithmConfig.xml"));
		marshaller.marshal(alg, System.out); //print xml on the screen
		
		/*//try the created configuration
		for (int i = 1; i <= 8; i++)
		{
			String fname = "RC10"+i+".txt";
			String out = "output/solutions_"+fname.substring(0,fname.lastIndexOf("."))+".csv";
			getSolution(description,args,fname,out);
		}
		
		for (int i = 1; i <= 8; i++)
		{
			String fname = "RC20"+i+".txt";
			String out = "output/solutions_"+fname.substring(0,fname.lastIndexOf("."))+".csv";
			getSolution(description,args,fname,out);
		}*/
		
		
		String fname = "RC101.txt";
		String out = "output/solutions_"+fname.substring(0,fname.lastIndexOf("."))+"(2).csv";
		getSolution(description,args,fname,out);
		
		fname = "RC207.txt";
		out = "output/solutions_"+fname.substring(0,fname.lastIndexOf("."))+"(2).csv";
		getSolution(description,args,fname,out);
		
		//getSolution(description,args);
		
	}
	
	/* Configure nStrat Strategies of the same type */
	private static void configureNStrategies(int nStrat,SearchStrategies searchStrategies,
				SelectorTypeEnum se, AcceptorTypeEnum ae, RuinTypeEnum re, 
				InsertionTypeEnum ie, double share, double prob, int repeat)
	{
		ObjectFactory of = new ObjectFactory();
		
		for (int i = 0; i < nStrat; i++)
		{
			SearchStrategyType searchStrategy = of.createSearchStrategyType();
			searchStrategy.setName( "strategy_" + (i+1+repeat) );
			searchStrategies.getSearchStrategy().add(searchStrategy);
			
			SelectorType st = of.createSelectorType();
			st.setName(se);
			searchStrategy.setSelector(st);
			double probability = prob/((double)nStrat);
			
			searchStrategy.setProbability(probability);
			AcceptorType a1 = of.createAcceptorType();
			a1.setName(ae);
			searchStrategy.setAcceptor(a1);
			a1.setAlpha(0.0);
			a1.setWarmup(0);
			
			ModuleType module1 = of.createModuleType();
			module1.setName(ModuleTypeEnum.RUIN_AND_RECREATE);
			Modules modules1 = of.createSearchStrategyTypeModules();
			modules1.getModule().add(module1);
			searchStrategy.setModules(modules1);
			RuinAndRecreateGroupType rrg1 = of.createRuinAndRecreateGroupType();
			module1.setRuinAndRecreateGroup(rrg1);
			
			RuinType ruin1 = of.createRuinType();
			ruin1.setName(re);
			ruin1.setShare(share);
			rrg1.setRuin(ruin1);
			
			InsertionType insertion1 = of.createInsertionType();
			insertion1.setName(ie);
			/*ConsiderFixedCosts consFixed = of.createInsertionTypeConsiderFixedCosts();
			consFixed.setValue("true");
			consFixed.setWeight(1.0);
			insertion1.setConsiderFixedCosts(consFixed);
			Level l = of.createInsertionTypeLevel();
			l.setMemory("2048");
			insertion1.setAllowVehicleSwitch(true);
			insertion1.setLevel(l);*/
			
			rrg1.setInsertion(insertion1);
		}
	}

	public static String getDescription(Algorithm alg) {
		String description = "**Algorithm Description**\n";
		description += "Construction: " + alg.getConstruction().getInsertion().getName() + " ";
		description += "Iterations: " + alg.getIterations() + " ";
		description += "Memory: " + alg.getStrategy().getMemory() + "\n";
		
		int i = 1;
		for (SearchStrategyType st : alg.getStrategy().getSearchStrategies().getSearchStrategy())
		{
			description += "Selector " + i + ": " + st.getSelector().getName() + " ";
			description += "Acceptor " + i + ": " + st.getAcceptor().getName() + " ";
			description += "Probability " + i + ": " + st.getProbability() + " ";
			description += "Module " + i + ": " + st.getModules().getModule().get(0).getName() + " ";
			description += "Ruin " + i + ": " + st.getModules().getModule().get(0).getRuinAndRecreateGroup().getRuin().getName() + " ";
			description += "Insertion " + i + ": " + st.getModules().getModule().get(0).getRuinAndRecreateGroup().getInsertion().getName() + "\n";
			i++;
		}
		return description;
	}

	private static void getSolution(String description, String[] args) {
		
		Examples.createOutputFolder();
		
		// Read input parameters
		OROoptions options = new OROoptions(args);
		OROutils.writeDescription(description, (String)options.get(CONSTANTS.OUTPUT));
		
		for(int r=0; r<(int)options.get(CONSTANTS.REPETITION); r++) {
			// Time tracking
			long startTime = System.currentTimeMillis();
			// Create a vrp problem builder
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			// A solomonReader reads solomon-instance files, and stores the required information in the builder.
			new SolomonReader(vrpBuilder).read("input/" + options.get(PARAMS.INSTANCE));
			VehicleRoutingProblem vrp = vrpBuilder.build();
			// Create the instace and solve the problem
			VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, 
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG));
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME));
			
			// Solve the problem
			Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
			// Extract the best solution
			VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);
									
			// Print solution on a file
			OROutils.write(solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT));
		}
	}
	
	private static void getSolution(String description, String[] args, String fname,String out) {
		
		Examples.createOutputFolder();
		
		// Read input parameters
		OROoptions options = new OROoptions(args,fname,out);
		OROutils.writeDescription(description, (String)options.get(CONSTANTS.OUTPUT));
		
		for(int r=0; r<(int)options.get(CONSTANTS.REPETITION); r++) {
			// Time tracking
			long startTime = System.currentTimeMillis();
			// Create a vrp problem builder
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			// A solomonReader reads solomon-instance files, and stores the required information in the builder.
			new SolomonReader(vrpBuilder).read("input/" + options.get(PARAMS.INSTANCE));
			VehicleRoutingProblem vrp = vrpBuilder.build();
			// Create the instace and solve the problem
			VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, 
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG));
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME));
			
			// Solve the problem
			Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
			// Extract the best solution
			VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);
									
			// Print solution on a file
			OROutils.write(solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT));
		}
	}
}
