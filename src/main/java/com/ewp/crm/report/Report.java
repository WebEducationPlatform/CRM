package com.ewp.crm.report;

import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class Report {
    private static Logger logger = LoggerFactory.getLogger(Report.class);
    @Autowired
    private ClientRepository clientRepository;

    public void countByLastDays(int interval) {
        List<Client> students2 = clientRepository.getClientByTimeInterval(interval);
        createLineChart(students2, interval);
    }

    public void countByLearningStudent() {
    }

    public void countByCompleteLearning(int interval) {
    }

    public void countBytoGiveUpTeaching(int interval) {
    }

    private void createLineChart(List<Client> students2, int interval) {
        DefaultCategoryDataset lineChartDataset = new DefaultCategoryDataset();
        Multimap<LocalDate, Client> studentInParticularDay = MultimapBuilder.treeKeys().linkedListValues().build();
        for (int i = 0; i < students2.size(); i++) {
            Client client = students2.get(i);
            LocalDate currentDate = client.getDateOfRegistration().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            studentInParticularDay.put(currentDate, students2.get(i));
        }
        LocalDate oldestClientDate = LocalDate.now().minusDays(interval);
        Map<LocalDate, Integer> map = new HashMap<>();
        LocalDate tmp = oldestClientDate;
        for (int i = 0; i < interval; i++) {
            if (!studentInParticularDay.containsKey(tmp)) {
                map.put(tmp, 0);
                tmp = tmp.plusDays(1);
            } else {
                map.put(tmp, studentInParticularDay.get(tmp).size());
                tmp = tmp.plusDays(1);
            }
        }
        TreeMap<LocalDate, Integer> treeMap = new TreeMap<>(map);

        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        XYSeries s1 = new XYSeries("Series 1", true, false);
        for (LocalDate key : treeMap.keySet()) {
            Collection<Client> client2 = studentInParticularDay.get(key);
            if (client2 == null) {
                s1.add(0, key.getDayOfWeek().getValue());
            } else {
                s1.add(client2.size(), key.getDayOfWeek().getValue());
            }
        }




        dataset.addSeries(s1);
        XYPlot plot = new XYPlot(dataset,
                new NumberAxis("X"), new NumberAxis("Y"),
                new XYStepRenderer());
        JFreeChart chart = new JFreeChart(plot);


        /*JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Студенты Java Mentor", "Дата",
                "Студенты",
                lineChartDataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 1500;    *//* Width of the image *//*
        int height = 768;   *//* Height of the image *//*
        */

        File lineChart = new File("images\\reports\\LineChart.jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(lineChart, chart, 500, 500);
        } catch (IOException e) {
            logger.error("Can't create directory ", e);
        }
    }
}
