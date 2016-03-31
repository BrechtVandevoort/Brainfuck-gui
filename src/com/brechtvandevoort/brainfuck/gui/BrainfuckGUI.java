package com.brechtvandevoort.brainfuck.gui;

import com.brechtvandevoort.brainfuck.interpreter.BrainfuckEngine;
import com.brechtvandevoort.brainfuck.interpreter.BrainfuckInstance;
import com.brechtvandevoort.brainfuck.programs.BrainfuckPrograms;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.util.Observable;
import java.util.Observer;

/**
 * GUI for editing and executing a Brainfuck program.
 *
 * Created by brecht on 27/03/2016.
 */
public class BrainfuckGUI extends Application implements Observer {

    private BrainfuckEngine _brainfuckEngine;

    private HBox _hBoxCells;
    private ScrollPane _scrollPaneCells;
    private CodeArea _codeArea;
    private GridPane _gridPane;
    private StackPane _root;
    private Scene _scene;
    private VBox _vBoxButtons;
    private Button _executeButton;
    private Button _stepButton;
    private CodeArea _console;

    private static final int MIN_NUM_CELLS = 20;
    private static final int DEFAULT_EXECUTION_INTERVAL = 50;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Brainfuck GUI");

        initGUI();

        primaryStage.setScene(_scene);
        primaryStage.show();
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateElements();
            }
        });
    }

    private void initGUI() {
        initCells();
        initCodeArea();
        initButtons();
        initConsole();
        initGridPane();
        initScene();
    }

    private void initCells() {
        _hBoxCells = new HBox();
        _hBoxCells.setMinWidth(50);

        _scrollPaneCells = new ScrollPane(_hBoxCells);
        _scrollPaneCells.setFitToHeight(true);
        _scrollPaneCells.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        for (int i = 0; i < MIN_NUM_CELLS; i++) {
            Label l = new Label("" + 0);
            l.getStyleClass().add("cell");
            _hBoxCells.getChildren().add(l);
        }

        _hBoxCells.getChildren().get(0).setStyle("-fx-border-color: red");
    }

    private void initCodeArea() {
        _codeArea = new CodeArea();
        _codeArea.appendText(BrainfuckPrograms.BRAINFUCK_PROGRAM_HELLO_WORLD);
    }

    private void initButtons() {
        _executeButton = new Button("Execute");
        _executeButton.getStyleClass().add("button");
        _executeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                executeProgram();
            }
        });
        _stepButton = new Button("Step");
        _stepButton.getStyleClass().add("button");
        _stepButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO
            }
        });

        _vBoxButtons = new VBox();
        _vBoxButtons.setSpacing(10);
        _vBoxButtons.setFillWidth(true);
        _vBoxButtons.getChildren().addAll(_executeButton, _stepButton);
    }

    private void initConsole() {
        _console = new CodeArea();
        _console.setEditable(false);

    }

    private void initGridPane() {
        ColumnConstraints column1 = new ColumnConstraints();
        //column1.setPercentWidth(80);
        column1.setFillWidth(true);
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints();
        //column2.setPercentWidth(20);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setFillHeight(true);
        row2.setVgrow(Priority.ALWAYS);

        _gridPane = new GridPane();
        _gridPane.getStyleClass().add("gridPane");
        _gridPane.getColumnConstraints().addAll(column1,column2);
        _gridPane.getRowConstraints().addAll(row1, row2);
        _gridPane.add(_vBoxButtons, 1, 0, 1,2);
        _gridPane.add(_scrollPaneCells, 0, 0);
        _gridPane.add(_codeArea, 0, 1);
        _gridPane.add(_console, 0, 2, 2, 1);
    }

    private void initScene() {
        _root = new StackPane();
        _root.getChildren().add(_gridPane);
        _scene = new Scene(_root, 800, 500);
        _scene.getStylesheets().add(BrainfuckGUI.class.getResource("brainfuckgui.css").toExternalForm());
    }

    private void executeProgram() {
        _brainfuckEngine = new BrainfuckEngine(_codeArea.getText());
        _brainfuckEngine.getBrainfuckInstance().addObserver(this);
        _brainfuckEngine.setExecutionInterval(DEFAULT_EXECUTION_INTERVAL);

        _console.appendText("--Executing--\n");

        Thread t = new Thread() {
            @Override
            public void run() {
                _brainfuckEngine.execute();
                System.out.println(_brainfuckEngine.getBrainfuckInstance().emptyOutputStream());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _console.appendText("--Done--\n");
                    }
                });
            }
        };

        t.start();
    }

    private void updateElements() {
        BrainfuckInstance instance =  _brainfuckEngine.getBrainfuckInstance();

        _hBoxCells.getChildren().clear();
        int minItems = (instance.getPointerPosition() > MIN_NUM_CELLS)? instance.getUsedRange()+1 : MIN_NUM_CELLS;

        for (int i = 0; i < minItems; i++) {
            Label l = new Label(instance.getValueAt(i) + "");
            l.getStyleClass().add("cell");
            if(i == instance.getPointerPosition()) {
                l.setStyle("-fx-border-color: red");
            }
            _hBoxCells.getChildren().add(l);
        }

        int instruction = _brainfuckEngine.getInstructionPointerPosition();
        _codeArea.setStyleClass(0, _codeArea.getText().length(), "blackText");
        if(!_brainfuckEngine.finished()) {
            _codeArea.setStyleClass(instruction, instruction + 1, "whiteText");
            _codeArea.setEditable(false);
        } else {
            _codeArea.setEditable(true);
        }
        _codeArea.positionCaret(instruction+1);

        while(instance.hasOutput()) {
            _console.appendText("" + (char)instance.readFromOutputStream());
        }
    }
}
