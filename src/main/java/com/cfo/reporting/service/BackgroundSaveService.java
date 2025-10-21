package com.cfo.reporting.service;

import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.beans.factory.annotation.Qualifier;


import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class BackgroundSaveService<T> {

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 100;

    private ThreadPoolTaskExecutor taskExecutor;

    // Inyectar el TaskExecutor por constructor
    public BackgroundSaveService(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;

    }

    /**
     * Método que usa el TaskExecutor de Spring
     */
    public CompletableFuture<Void> salvarConTaskExecutor(List<T> datos, Class<T> entityClass) {
        return CompletableFuture.runAsync(() -> {
            procesarDatosEnBackground(datos,entityClass);
        }, taskExecutor);
    }

    /**
     * Método con @Async que usa el executor configurado
     */
    @Async("backgroundTaskExecutor")
    public CompletableFuture<String> salvarConAsync(List<T> datos, Class<T> entityClass,String reportType) {
        try {
            procesarDatosEnBackground(datos,entityClass);
            return CompletableFuture.completedFuture("Procesamiento completado para " + entityClass.getSimpleName());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Método para alta prioridad
     */
    @Async("highPriorityTaskExecutor")
    public CompletableFuture<String> salvarAltaPrioridad(List<T> datos,Class<T> entityClass) {
        try {
            procesarDatosEnBackground(datos,entityClass);
            return CompletableFuture.completedFuture("Procesamiento de alta prioridad completado");
        } catch (Exception e) {
            System.out.println("Error en :"+e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private void procesarDatosEnBackground(List<T> datos, Class<T> entityClass) {
        long startTime = System.currentTimeMillis();
        System.out.println("Starting processing TaskExecutor: " +
                datos.size() + " records from " + entityClass.getSimpleName());

        List<List<T>> chunks = dividirEnChunks(datos, BATCH_SIZE);

        for (int i = 0; i < chunks.size(); i++) {
            procesarChunk(chunks.get(i), i + 1, chunks.size());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Processing completed in " + (endTime - startTime) + "ms");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarChunk(List<T> chunk, int chunkNumber, int totalChunks) {
        try {
            for (T entidad : chunk) {
                //System.out.println("Entidad: "+entidad);
                //entityManager.persist(entidad);
                entityManager.merge(entidad);
            }
//            entityManager.flush();
//            entityManager.clear();

            System.out.printf("Chunk %d/%d procesado (%d registros) - Thread: %s%n",
                    chunkNumber, totalChunks, chunk.size(), Thread.currentThread().getName());

        } catch (Exception e) {
            System.err.println("Error en chunk " + chunkNumber + ": " + e.getMessage());
            throw e;
        }
    }

    private List<List<T>> dividirEnChunks(List<T> lista, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < lista.size(); i += chunkSize) {
            chunks.add(lista.subList(i, Math.min(i + chunkSize, lista.size())));
        }
        return chunks;
    }

    /**
     * Método para obtener métricas del executor
     */
    public String obtenerMetricasExecutor() {
        return String.format("""
            Pool Size: %d
            Active Count: %d
            Queue Size: %d
            Completed Task Count: %d
            """,
                taskExecutor.getPoolSize(),
                taskExecutor.getActiveCount(),
                taskExecutor.getThreadPoolExecutor().getQueue().size(),
                taskExecutor.getThreadPoolExecutor().getCompletedTaskCount()
        );
    }
}