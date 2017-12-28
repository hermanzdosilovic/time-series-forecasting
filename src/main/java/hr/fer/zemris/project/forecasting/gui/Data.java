package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.io.IOException;


public class Data{

    private ObservableList<DatasetValue> datasetValues;
    private XYChart.Series series;

    private static ObservableList<DatasetValue> getList(){
      try{
         return FXCollections.observableArrayList(DatasetValue.
                 encapsulateDoubleArray(DataReaderUtil.readDataset("datasets/monthly-milk-production-pounds-p.csv")));
      }catch (IOException e){
         return null;
      }
    }

    public Data(){
       datasetValues = getList();
       series = DatasetValue.getChartData(datasetValues);
    }

   public ObservableList<DatasetValue> getDatasetValues() {
      return datasetValues;
   }

   public final static double MAX_TABLE_WIDTH = 150;


    public void createUI(Pane parent){

       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(30, 30, 30, 30));


       //Load dataset button
       Button loadDataset = new Button("Load data");

       //Save dataset button
       Button saveDataset = new Button("Save data");

       HBox upperBox = new HBox(loadDataset, saveDataset);

       upperBox.setSpacing(10);

       grid.add(upperBox, 0,0);

       //Editable dataset
       TableView table = new TableView();
       table.setEditable(true);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       table.setItems(datasetValues);
       table.setPrefWidth(MAX_TABLE_WIDTH);
       TableColumn<DatasetValue, Double> values = new TableColumn("Data Set Values");
       values.setSortable(false);
       values.setEditable(true);

       values.setCellValueFactory(new PropertyValueFactory<>("value"));

       values.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));

       values.setOnEditCommit((e) ->{
          e.getTableView().getItems().set(e.getTablePosition().getRow(), new DatasetValue(e.getNewValue()));
       });


       table.getColumns().add(values);

       grid.add(table,0, 1);

       //normalize button
       Button normalize = new Button("Normalize");
       grid.add(normalize, 0, 2);

       //line chart
       LineChart line = lineChart(series);
       addChangeListener(datasetValues, series);

       grid.add(line, 1, 0, 3, 3);


       parent.getChildren().add(grid);
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

//   private void addRow() {
//
//      // get current position
//      TablePosition pos = table.getFocusModel().getFocusedCell();
//
//      // clear current selection
//      table.getSelectionModel().clearSelection();
//
//      // create new record and add it to the model
//      DatasetValue data = new DatasetValue(Double.NaN);
//      table.getItems().add(data);
//
//      // get last row
//      int row = table.getItems().size() - 1;
//      table.getSelectionModel().select( row, pos.getTableColumn());
//
//      // scroll to new row
//      table.scrollTo( data);
//
//   }

   public static LineChart<Number, Number> lineChart(XYChart.Series series){
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Sample Number");
      yAxis.setLabel("Sample Value");
      final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
      lineChart.setTitle("Data");
      lineChart.getData().add(series);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxSize(GraphUtil.DEFAULT_WIDTH, GraphUtil.DEFAULT_HEIGHT);
      lineChart.setAnimated(false);
      return lineChart;
   }

   public static void addChangeListener(ObservableList<DatasetValue> datasetValues, XYChart.Series series){
      datasetValues.addListener(new ListChangeListener<DatasetValue>() {
         @Override
         public void onChanged(Change<? extends DatasetValue> c) {
            while(c.next()) {
               if(c.wasAdded()){
                  for(int i = c.getFrom(); i < c.getTo(); i++){
                     series.getData().set(i, new XYChart.Data<>(i, datasetValues.get(i).getValue()));
                  }
               }
            }
         }
      });

   }
}
