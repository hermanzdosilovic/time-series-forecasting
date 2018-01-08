package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Collections;


public class Data{
   //TODO popraviti klikanje na hover
    private ObservableList<DatasetValue> datasetValues;
    private XYChart.Series series;
    private Stage primaryStage;
    private TableView table;

   private static ObservableList<DatasetValue> getList(String path){
      try{
         return FXCollections.observableArrayList(DatasetValue.
                 encapsulateDoubleArray(DataReaderUtil.readDataset(path)));
      }catch (IOException e){
         return null;
      }
    }

    public Data(Stage primaryStage){
       this.primaryStage = primaryStage;
       this.series = new XYChart.Series();
       this.table = new TableView();
       series.setName("Sample");
       datasetValues = FXCollections.observableArrayList();
       series.setData(DatasetValue.getChartData(datasetValues));
       updateSeriesOnListChangeListener(datasetValues, series);
    }

   public ObservableList<DatasetValue> getDatasetValues() {
      return datasetValues;
   }

   public Stage getPrimaryStage() {
      return primaryStage;
   }

   public final static double MAX_TABLE_WIDTH = 150;


    public void createUI(Pane parent){

       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(30, 30, 30, 30));


       //Load dataset button
       Button loadDataset = new Button("Load data");
       loadDataset.setOnAction(loadAction());

       //Save dataset button
       Button saveDataset = new Button("Save data");
       saveDataset.setDisable(true);

       HBox upperBox = new HBox(loadDataset, saveDataset);

       upperBox.setSpacing(10);

       grid.add(upperBox, 0,0);

       //Editable dataset
       table.setEditable(true);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       table.setItems(datasetValues);
       table.setPrefWidth(MAX_TABLE_WIDTH);
       TableColumn<DatasetValue, Double> values = new TableColumn("Data Set Values");
       values.setSortable(false);
       values.setEditable(true);

       values.setCellValueFactory(new PropertyValueFactory<>("value"));

       values.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));

       values.setOnEditCommit((e) ->
               e.getTableView().getItems().set(e.getTablePosition().getRow(), new DatasetValue(e.getNewValue())));

       table.setOnKeyPressed(event -> {
          if(event.getCode() == KeyCode.PLUS){
             addRow();
          }else if(event.getCode() == KeyCode.MINUS){
             removeRow();
          }
       });


       table.getColumns().add(values);

       grid.add(table,0, 1);

       //normalize button
       Button normalize = new Button("Normalize");
       grid.add(normalize, 0, 2);
       normalize.setOnAction(normalizeAction());
       normalize.setDisable(true);
       enableButtonWhenDatasetExistsListener(datasetValues, normalize);

       //line chart
       LineChart line = lineChart(series, "Data");

       grid.add(line, 1, 0, 3, 3);


       parent.getChildren().add(grid);
   }

   private EventHandler<ActionEvent> loadAction() {
      return event -> {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
         fileChooser.setTitle("Load dataset...");

         File f = fileChooser.showOpenDialog(primaryStage);
         if(f != null) {
            datasetValues.clear();
            datasetValues.addAll(getList(f.getPath()));
         }
      };

   }

   private void addRow() {

      // get current position
      int row = table.getSelectionModel().getSelectedIndex();
      TableColumn selectedColumn = table.getFocusModel().getFocusedCell().getTableColumn();

      // clear current selection
      table.getSelectionModel().clearSelection();

      // create new record and add it to the model
      DatasetValue data = new DatasetValue(0.0);
      if(row < table.getItems().size() - 1)table.getItems().add(row + 1, data);
      else table.getItems().add(data);

      table.getSelectionModel().select(row + 1, selectedColumn);

      //edit cell
      table.getFocusModel().focus(row + 1, selectedColumn);
      table.edit(row + 1, selectedColumn);

      // scroll to new row
      table.scrollTo(row + 1);

   }


   private void removeRow() {
      if(table.getItems().size() == 0) return;
      int row = table.getSelectionModel().getSelectedIndex();
      datasetValues.remove(row);
   }

   public static LineChart<Number, Number> lineChart(XYChart.Series series, String lineChartName){
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Sample Number");
      yAxis.setLabel("Sample Value");
      final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
      lineChart.setTitle(lineChartName);
      lineChart.getData().add(series);
      lineChart.setCreateSymbols(true);
      lineChart.setMaxSize(GraphUtil.DEFAULT_WIDTH, GraphUtil.DEFAULT_HEIGHT);
      lineChart.setAnimated(false);
      return lineChart;
   }

   public static LineChart<Number, Number> mseLineChart(String lineChartName){
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
      lineChart.setTitle(lineChartName);
      lineChart.setCreateSymbols(true);
      lineChart.setMaxSize(GraphUtil.MSE_DEFAULT_WIDTH, GraphUtil.MSE_DEFAULT_HEIGHT);
      lineChart.setAnimated(false);
      return lineChart;
   }

   public static void updateSeriesOnListChangeListener(ObservableList<DatasetValue> datasetValues,
                                                       XYChart.Series<Integer, Double> series){
      datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
         while(c.next()) {
            if(c.wasReplaced()){
               for(int i = c.getFrom(); i < c.getTo(); i++){
                  XYChart.Data<Integer, Double> nextAddition = new XYChart.Data<>(i, datasetValues.get(i).getValue());
                  nextAddition.setNode(new DatasetValue.HoveredThresholdNode(
                          nextAddition.getXValue(), nextAddition.getYValue()));
                  series.getData().set(i, nextAddition);
               }
            }
            else if(c.wasAdded()){
               for(int i = c.getFrom(); i < c.getTo(); i++){
                  XYChart.Data<Integer, Double> nextAddition = new XYChart.Data<>(i, datasetValues.get(i).getValue());
                  nextAddition.setNode(new DatasetValue.HoveredThresholdNode(
                          nextAddition.getXValue(), nextAddition.getYValue()));
                  if(i < series.getData().size()) {
                     series.getData().add(i, nextAddition);
                     for (int j = i + 1; j < series.getData().size(); j++) {
                        XYChart.Data<Integer, Double> after = new XYChart.Data<>(j, datasetValues.get(j).getValue());
                        after.setNode(new DatasetValue.HoveredThresholdNode(
                                after.getXValue(), after.getYValue()));
                        series.getData().set(j, after);
                     }
                  }
                  else series.getData().add(nextAddition);
               }
            }
            else if(c.wasRemoved()){
               series.getData().remove(c.getFrom());
               for(int j = c.getFrom(); j < series.getData().size(); j++){
                  XYChart.Data<Integer, Double> after = new XYChart.Data<>(j, datasetValues.get(j).getValue());
                  after.setNode(new DatasetValue.HoveredThresholdNode(
                          after.getXValue(), after.getYValue()));
                  series.getData().set(j, after);
               }
            }
         }
      });
   }

   public static void enableButtonWhenDatasetExistsListener(ObservableList<DatasetValue> datasetValues, Button b){
      datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
         while(c.next()) {
            if(datasetValues.size() == 0) b.setDisable(true);
            else b.setDisable(false);
         }
      });
   }

   private EventHandler<ActionEvent> normalizeAction(){
       return event -> {
          Stage normalizeStage = new Stage();
          normalizeStage.setTitle("Normalize!");
          normalizeStage.initOwner(primaryStage);
          normalizeStage.initModality(Modality.WINDOW_MODAL);

          Label labelFrom = new Label("From:");
          TextField from = new TextField();


          Label labelTo = new Label("To:");
          TextField to = new TextField();

          GridPane pane = new GridPane();
          pane.add(labelFrom, 0, 0);
          pane.add(labelTo, 0, 1);
          pane.add(from, 1, 0);
          pane.add(to, 1, 1);

          pane.setVgap(10);
          pane.setHgap(10);

          Label wrongInput = new Label("Invalid input");
          wrongInput.setTextFill(Color.RED);
          wrongInput.setVisible(false);

          Button ok = new Button("OK");
          ok.setOnAction(e ->{
             try{
                double lowerBound = Double.parseDouble(from.getText());
                double upperBound = Double.parseDouble(to.getText());
                new Thread(() -> {
                   normalize(lowerBound, upperBound);
                }).run();
                normalizeStage.hide();
             }catch(IllegalArgumentException nfe){
                wrongInput.setVisible(true);
                return;
             }
          });

          HBox okBox = new HBox(ok);
          okBox.setAlignment(Pos.CENTER);

          HBox invalidBox = new HBox(wrongInput);
          invalidBox.setAlignment(Pos.CENTER);

          VBox normalizeBox = new VBox(pane, invalidBox, okBox);
          normalizeBox.setSpacing(15);
          normalizeBox.setPadding(new Insets(20, 20, 20,20));

          Scene normalizeScene = new Scene(normalizeBox);
          normalizeStage.setScene(normalizeScene);
          normalizeStage.show();
       };
   }

   private void normalize(double from, double to){
       if(from > to) throw new IllegalArgumentException();
       if(from == to){
          for(int i = 0; i < datasetValues.size(); i++){
             datasetValues.set(i, new DatasetValue(from));
          }
          return;
       }
       double min = Collections.min(datasetValues).getValue();
       double max = Collections.max(datasetValues).getValue();
      for(int i = 0; i < datasetValues.size(); i++){
         double x = datasetValues.get(i).getValue();
         datasetValues.set(i, new DatasetValue(from + (x - min) * (to - from) / (max - min)));
      }
   }

   private static class MyDoubleStringConverter extends StringConverter <Double>{

      @Override
      public String toString(Double object) {
         if(object == null) return "";
         return Double.toString(object);
      }

      @Override
      public Double fromString(String string) {
         try{
            return Double.parseDouble(string);
         }catch(NumberFormatException e){
            //napravi bolji error handling
            return null;
         }
      }
   }

}
