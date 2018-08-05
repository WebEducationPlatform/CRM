package com.ewp.crm.report;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

import java.io.File;

public class LineChart {

    public static void main( String[ ] args ) throws Exception {
        /*DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        line_chart_dataset.addValue( 15 , "schools" , "1970" );
        line_chart_dataset.addValue( 30 , "schools" , "1980" );
        line_chart_dataset.addValue( 60 , "schools" , "1990" );
        line_chart_dataset.addValue( 120 , "schools" , "2000" );
        line_chart_dataset.addValue( 240 , "schools" , "2010" );
        line_chart_dataset.addValue( 300 , "schools" , "2014" );

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "Schools Vs Years","Year",
                "Schools Count",
                line_chart_dataset,PlotOrientation.VERTICAL,
                true,true,false);

        int width = 640;    *//* Width of the image *//*
        int height = 480;   *//* Height of the image *//*
        File lineChart = new File( "images\\reports\\LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);*/
        //-------------------------------------------------
        /*DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "S1", "C1");
        dataset.addValue(2.0, "S1", "C2");
        dataset.addValue(3.0, "S2", "C1");
        dataset.addValue(4.0, "S2", "C2");
        GroupedStackedBarRenderer renderer
                = new GroupedStackedBarRenderer();
        CategoryPlot plot = new CategoryPlot(dataset,
                new CategoryAxis("Category"), new NumberAxis("Value"),
                renderer);
        JFreeChart chart = new JFreeChart(plot);
        */

        //--------------------------------------------
       /* DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "S1", "C1");
        CategoryPlot plot = new CategoryPlot(dataset,
                new CategoryAxis("Category"), new NumberAxis("Value"),
                new LevelRenderer());
        JFreeChart chart = new JFreeChart(plot);
*/



        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        XYSeries s1 = new XYSeries("Series 1", true, false);
        s1.add(5.0, 5.0);
        s1.add(10.0, 15.5);
        s1.add(15.0, 9.5);
        s1.add(20.0, 7.5);
        dataset.addSeries(s1);
        XYSeries s2 = new XYSeries("Series 2", true, false);
        s2.add(5.0, 5.0);
        s2.add(10.0, 15.5);
        s2.add(15.0, 9.5);
        s2.add(20.0, 3.5);
        dataset.addSeries(s2);
        XYPlot plot = new XYPlot(dataset,
                new NumberAxis("X"), new NumberAxis("Y"),
                new XYStepRenderer());
        JFreeChart chart = new JFreeChart(plot);


        File lineChart = new File( "images\\reports\\TestChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, chart, 500 ,500);
    }
}