package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class Data{


    public final static ObservableList<DatasetValue> datasetValues = getList();

    private static ObservableList<DatasetValue> getList(){
      try{
         return FXCollections.observableArrayList(DatasetValue.
                 encapsulateDoubleArray(DataReaderUtil.readDataset("datasets/monthly-milk-production-pounds-p.csv")));
      }catch (IOException e){
         return null;
      }
    }

    public final static double MAX_TABLE_WIDTH = 150;

    public final static TableView table = new TableView();

    public void createUI(Pane parent){

       GridPane grid = new GridPane();
       grid.setHgap(15);
       grid.setVgap(15);
       grid.setPadding(new Insets(20, 20, 20, 20));


       //Load dataset button
       Button loadDataset = new Button("Load data");

       //Save dataset button
       Button saveDataset = new Button("Save data");

       HBox upperBox = new HBox(loadDataset, saveDataset);

       upperBox.setSpacing(10);

       grid.add(upperBox, 0,0);

       //Editable dataset
//       ScrollPane sp = new ScrollPane();
//       sp.setFitToWidth(true);
//       sp.setMaxWidth(MAX_TABLE_WIDTH);

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
          e.getTableView().getItems().get(e.getTablePosition().getRow()).setValue(e.getNewValue());
       });

       table.getColumns().add(values);
//       sp.setContent(table);

       grid.add(table,0, 1);

       //normalize button
       Button normalize = new Button("Normalize");
       grid.add(normalize, 0, 2);

//       //graph space
//       SwingNode graphSpace = new SwingNode();
//       double[] dataset = DatasetValue.getDoubleArray(datasetValues);
//       Map<String, double[]> graph = new HashMap<>();
//       HBox box = new HBox();
//       box.setMinSize(GraphUtil.DEFAULT_WIDTH, GraphUtil.DEFAULT_HEIGHT);
//       graph.put("Data", dataset);
//          SwingUtilities.invokeLater(() ->{
//             JPanel panel = GraphUtil.dataAsPanel(graph);
//             graphSpace.setContent(panel);
//          });
//
//       box.getChildren().add(graphSpace);
//
//       grid.add(box, 1, 0, 2, 2);

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

   private void addRow() {

      // get current position
      TablePosition pos = table.getFocusModel().getFocusedCell();

      // clear current selection
      table.getSelectionModel().clearSelection();

      // create new record and add it to the model
      DatasetValue data = new DatasetValue(Double.NaN);
      table.getItems().add(data);

      // get last row
      int row = table.getItems().size() - 1;
      table.getSelectionModel().select( row, pos.getTableColumn());

      // scroll to new row
      table.scrollTo( data);

   }
}
