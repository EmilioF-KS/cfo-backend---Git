package com.cfo.reporting;

import com.cfo.reporting.drools.vlookup.*;
import com.cfo.reporting.drools.vlookup.model.data.EvaluationContext;
import com.cfo.reporting.service.DetailParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableCaching
public class CfoReportingApplication implements CommandLineRunner {
   @Autowired
   private DetailParserService detailParserService;


	public static void main(String[] args) {
		SpringApplication.run(CfoReportingApplication.class, args);
	}


	@Override
	public void run(String... args) {

	//	detailParserService.allDetailsCalculated("scr_worksheet","202503",8);
		// Step 1: Compile and load DRL rules
//		KieServices kieServices = KieServices.Factory.get();
//		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
//		kieFileSystem.write(ResourceFactory.newClassPathResource("rules/vlookup-rules.drl"));
//		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
//		kieBuilder.buildAll();
//		Results results = kieBuilder.getResults();
//		System.out.println("Result :::::" + results);
//		if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
//			throw new IllegalStateException("DRL Errors:\n" + results);
//		}
//
//		KieContainer kieContainer = kieServices.getKieClasspathContainer();
//		//printLoadedRules(kieContainer);
//
//		// Step 2: Prepare Reference Data
//		ReferenceDataLoader dataLoader = new ReferenceDataLoader();
//		Map<String, Map<String, Object>> productTable = new HashMap<>();
//		productTable.put("P-100", Map.of("price", 99.99));
//		productTable.put("P-200", Map.of("price", 149.99));
//		productTable.put("P-300", Map.of("price", 199.99));
//		dataLoader.addTable("products", productTable);
//
//		// Step 3: Prepare Drools session
//		KieBase kieBase = kieContainer.getKieBase();
//		KieSession kieSession = kieBase.newKieSession();
//		kieSession.setGlobal("dataLoader", dataLoader);

		// Step 4: Create evaluation context and result
//		EvaluationContext context = new SimpleEvaluationContext(Map.of(
//				"product_id", "P-200",
//				"quantity", 4
//		));
//		EvaluationResult result = new EvaluationResult();
//
//		// Step 5: Prepare ParsedExpression with VLookupExpression
//		ParsedExpression parsed = new ParsedExpression(
//				"VLOOKUP($product_id, 'products', 'price') - VLOOKUP($product_id, 'newprods', 'price')",
//				List.of(new VLookupExpression(
//						new SimpleExpression("$product_id"),
//						"products",
//						"price",
//						VLookupExpression.MatchType.EXACT,
//						VLookupExpression.RangeMode.FIRST
//				))
//		);
//
//		// Step 6: Insert facts into Drools session
//		kieSession.insert(parsed);
//		kieSession.insert(context);
//		kieSession.insert(result);
//
//		// Step 7: Fire rules
//		kieSession.fireAllRules();
//		kieSession.dispose();
//
//		// Step 8: Output
//		if (result.hasErrors()) {
//			result.getErrors().forEach(System.err::println);
//		} else {
//			System.out.println("Result final: " + result.getValue()); // Should print 299.97
//		}

	}

	private void printLoadedRules() {

//		KieBase kieBase = kieContainer.getKieBase();
//		System.out.println("\n=== REGLAS CARGADAS ===");
//
//		for (KiePackage pkg : kieBase.getKiePackages()) {
//			System.out.println("\nPaquete: " + pkg.getName());
//
//			System.out.println("\nReglas:");
//			for (Rule rule : pkg.getRules()) {
//				System.out.println(" - " + rule.getName());
//			}
//
//			System.out.println("\nFunciones:");
//			for (String function : pkg.getFunctionNames()) {
//				System.out.println(" - " + function);
//			}
//		}
//
//		System.out.println("\nTotal reglas: " + kieBase.getKiePackages().stream()
//				.mapToInt(p -> p.getRules().size()).sum());


	}
}
