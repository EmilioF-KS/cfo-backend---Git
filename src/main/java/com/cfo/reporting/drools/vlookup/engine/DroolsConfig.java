package com.cfo.reporting.drools.vlookup.engine;

import org.drools.core.event.BeforeActivationFiredEvent;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();

        // 1. Configurar archivo de reglas
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/vlookup-rules.drlold"));

        // 2. Construir el módulo
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();


        // 3. Verificar errores
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Error en reglas DRL: " + results.getMessages());
        }

        // 4. Crear contenedor
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    @Bean
    public KieSession kieSession(KieContainer kieContainer) {
        KieSession session = kieContainer.newKieSession();
       // KieBase kieBase = kieContainer.getKieBase("vlookupBase");
        // System.out.println("Reglas cargads"+kieBase.getKiePackages().size());
//        ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("graal.js");
//        if (jsEngine == null) {
//            jsEngine = new ScriptEngineManager().getEngineByName("nashorn.js");
//        }
//        if (jsEngine == null) {
//            jsEngine = new ScriptEngineManager().getEngineByName("rhino");
//        }
//        session.setGlobal("scriptEngine",jsEngine);
//        session.addEventListener(new DefaultAgendaEventListener() {
//            @Override
//            public void matchCreated(MatchCreatedEvent event) {
//                System.out.println("Regla Activa :"+event.getMatch().getRule().getName());
//
//            }
//
//            public void beforeActivationFiredEvent(BeforeActivationFiredEvent event) {
//                System.out.println("A punto de activar"+event.getActivation().getActivationNumber());
//
//            }
//        });
        if (session == null) {
            throw new IllegalStateException("Could not get Session");
        }
        return session;
    }
}