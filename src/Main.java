import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Main extends Application {
    public static void digitsTxtFld(TextField field) {
        field.setTextFormatter(new TextFormatter<Integer>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }
        @Override
    public void start(Stage stage) {
        Label lab1=new Label("From port");
        ComboBox<String> combo = new ComboBox<>();
        MyDB db = new MyDB();
        //select - retunere en arrayList med navne på vores have:
        ArrayList<String> harbourNames = db.query("select name from harbour;","name");

        for (String name: harbourNames) {
                combo.getItems().add(name);
        }

        Label lab2=new Label("To port");
        ComboBox<String> combo1 = new ComboBox<>();

        MyDB db1 = new MyDB();
        ArrayList<String> Destinations = db1.query("select name from harbour;","name");
        for (String name: Destinations)
            combo1.getItems().add(name);

        Label lab3=new Label("Number of containers");
        TextField fld = new TextField();
        Button srch = new Button("Search");
        TextArea res = new TextArea();
        digitsTxtFld(fld);

        MyDB db2 = new MyDB();
        srch.setOnAction(e ->{

            if (combo.getValue().equals(combo1.getValue())) {
                res.setText("Source and destination ports must be different");
                res.setStyle(" -fx-text-fill: rgb(255,0,0);");
            return;
        }
            if (fld.getText() == ("")){
                res.setText("Number must be filled");
                res.setStyle(" -fx-text-fill: rgb(255,0,0);");
            return;
        }
            int numb = Integer.parseInt(fld.getText());
            if (numb <= 0 ){
                res.setText("Input must be greater than 0");
                res.setStyle(" -fx-text-fill: rgb(255,0,0);");
            return;
        }
            if (numb >=100000){
                res.setText("Input can't be higher than 99999");
                res.setStyle(" -fx-text-fill: rgb(255,0,0);");
            return;
            }

            res.setStyle("-fx-text-fill: rgb(0,0,0);");

            //evt. indsætte i metode
            ArrayList<String> vesselNames = db2.query("select v.name as vessel, capacity from transport t " +
                    "inner join vessel v on t.vessel = v.id " +
                    "inner join harbour h1 on t.fromharbour = h1.id " +
                    "inner join harbour h2 on t.toharbour = h2.id " +
                    "left outer join flow f on t.id = f.transport " +
                    "where h1.name = '" + combo.getValue() + "' and h2.name = '" + combo1.getValue() +
                    "' group by t.id " +
                    "having " + numb + "<= v.capacity - f.containers;", "vessel");

            db2.cmd( "insert into flow (transport, containers) " +
                    "values ( 13, " + numb +" );");

            for (String name2: vesselNames) {
                res.setText(String.valueOf(vesselNames) + "\n" + ("There are " + vesselNames.size() + " available vessel(s) to send " +numb+ " containers to " + combo1.getValue()));
            }
                if (vesselNames.isEmpty())
                    res.setText("There is no available vessels for " +numb+ " containers");
    });

        GridPane pane1 = new GridPane();
        BorderPane root=new BorderPane();
        lab1.setPrefSize(150,30);
        lab2.setPrefSize(150,30);
        lab3.setPrefSize(150,30);
        srch.setStyle(
                "-fx-font: 22 arial; -fx-base: rgb(0,250,0);"+
                        " -fx-text-fill: rgb(255,255,255);");

        res.setStyle( "-fx-font: 16 arial;");
        pane1.add(lab1,1,1);
        pane1.add(combo,1,2);
        pane1.add(lab2,2,1);
        pane1.add(combo1,2,2);
        pane1.add(lab3,3,1);
        pane1.add(fld,3,2);
        root.setTop(pane1);
        root.setCenter(srch);
        root.setBottom(res);
        //VBox root = new VBox(lab1,combo,lab2,combo1,lab3,fld,srch,res);
        //Group root = new Group(); // the root is Group or Pane
        Scene scene = new Scene(root, 500, 500, Color.BLUE);
        stage.setTitle("Containers");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);

    }



    // Get names of vessels that can do the transport. Given port 1, port 2, number of containers, get vessels

   // update Insert... Given port 1, port 2, vessel and number and containers, then update flow with that information
}