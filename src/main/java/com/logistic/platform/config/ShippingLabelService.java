package com.logistic.platform.config;

import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingLabelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShippingLabelService.class);
    private JasperReport compiledLabelReport;
    private JasperReport jasperReport;

    /**
     * OPTIMIZATION 1: Pre-compile JRXML ONCE when application starts.
     * Never call JasperCompileManager inside controller request paths.
     */
    @PostConstruct
    public void init() {
        try {
            long startTime = System.currentTimeMillis();
            LOGGER.info("Compiling JRXML shipping label template...");

            ClassPathResource resource = new ClassPathResource("templates/shipping_label.jrxml");
            try (InputStream inputStream = resource.getInputStream()) {
                this.compiledLabelReport = JasperCompileManager.compileReport(inputStream);
            }

            LOGGER.info("JRXML template compiled in {} ms", (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            LOGGER.error("Failed to compile JRXML template on startup", e);
            throw new RuntimeException("Could not initialize JasperReport template", e);
        }
    }

    /**
     * OPTIMIZATION 2: Pre-warm the Barcode & AWT Graphics Engine.
     * Loads Batik/Barcode4J graphics classes into JVM memory on app ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpReportEngine() {
        new Thread(() -> {
            try {
                LOGGER.info("Warming up JasperReports barcode graphics engine...");
                long startTime = System.currentTimeMillis();

                // Create minimal dummy data to trigger first-time class loading
                Map<String, Object> dummy = new HashMap<>();
                dummy.put("ConsignmentNo", "WARMUP-001");
                dummy.put("BarcodeNo", "WARMUP-001");
                dummy.put("ItemNo", "1");

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(dummy));
                JasperPrint print = JasperFillManager.fillReport(compiledLabelReport, new HashMap<>(), dataSource);
                JasperExportManager.exportReportToPdf(print);

                LOGGER.info("JasperReports barcode engine pre-warmed in {} ms", (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                LOGGER.warn("JasperReports engine warm-up failed (non-critical)", e);
            }
        }).start();
    }

    /**
     * Fast PDF Generation Endpoint Call (~100-200ms execution time).
     */
    public byte[] generateShippingLabelPdf(List<Map<String, Object>> itemsList) throws JRException {
        long startTime = System.currentTimeMillis();

        // 1. Wrap item list in data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemsList);

        // 2. Fill pre-compiled template directly from memory
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledLabelReport, new HashMap<>(), dataSource);

        // 3. Export to PDF bytes
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        LOGGER.info("Generated PDF shipping label ({} pages) in {} ms", itemsList.size(), (System.currentTimeMillis() - startTime));
        return pdfBytes;
    }

}
