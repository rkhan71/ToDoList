import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class that uses JavaFX to create a to-do list.
 * @author Rayan Khan
 * @version 1.0
 */
public class ToDoList extends Application {
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Task> completedTasks = FXCollections.observableArrayList();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    /**
     * Main function that can be run.
     * @param args Param for main function
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Function that starts JavaFX application.
     * @param stage Stage for application
     */
    public void start(Stage stage) {
        stage.setTitle("To Do List");

        Text title = new Text("To Do List");
        title.setFont(Font.font(null, FontWeight.BOLD, 20));
        title.setUnderline(true);
        StackPane centerTitle = new StackPane(title);
        Text remaining = new Text("Tasks Remaining: " + tasks.size());
        Text completed = new Text("Tasks Completed: " + completedTasks.size());
        HBox taskStats = new HBox(remaining, completed);
        taskStats.setAlignment(Pos.CENTER);
        taskStats.setSpacing(100);
        VBox top = new VBox(centerTitle, taskStats);
        top.setSpacing(20);
        top.setPadding(new Insets(10, 0, 10, 0));

        TextField taskName = new TextField("Task Name");
        ObservableList<String> types = FXCollections.observableArrayList("Study", "Shop", "Cook", "Sleep");
        ComboBox<String> taskTypes = new ComboBox<String>(types);
        taskTypes.setPromptText("Type");
        ObservableList<Integer> hours = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        ComboBox<Integer> taskLength = new ComboBox<Integer>(hours);
        taskLength.setPromptText("Length");
        Button enqueue = new Button("Enqueue");
        Button dequeue = new Button("Dequeue");
        HBox bottom = new HBox();
        bottom.setSpacing(50);
        bottom.getChildren().addAll(taskName, taskTypes, taskLength, enqueue, dequeue);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(50, 0, 50, 0));

        TableView<Task> table = new TableView<Task>();
        TableColumn todo = new TableColumn("To Do");
        TableColumn name = new TableColumn("Task");
        name.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        TableColumn type = new TableColumn("Task Type");
        type.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        TableColumn due = new TableColumn("Due Date");
        due.setCellValueFactory(new PropertyValueFactory<Task, String>("due"));
        todo.getColumns().addAll(name, type, due);
        table.getColumns().add(todo);
        table.setItems(tasks);
        table.setMinWidth(400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableView<Task> table2 = new TableView<Task>();
        TableColumn done = new TableColumn("Done");
        TableColumn name2 = new TableColumn("Task");
        name2.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        TableColumn type2 = new TableColumn("Task Type");
        type2.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
        TableColumn due2 = new TableColumn("Due Date");
        due2.setCellValueFactory(new PropertyValueFactory<Task, String>("due"));
        due2.setMinWidth(125);
        TableColumn timeCompleted = new TableColumn("Time Completed");
        timeCompleted.setCellValueFactory(new PropertyValueFactory<Task, String>("completed"));
        timeCompleted.setMinWidth(125);
        done.getColumns().addAll(name2, type2, due2, timeCompleted);
        table2.getColumns().add(done);
        table2.setItems(completedTasks);
        table2.setMinWidth(400);
        table2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox center = new HBox(table, table2);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(100);

        enqueue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (taskName.getText() == null | taskName.getText().isEmpty()) {
                    alert("No task name inputted!");
                } else if (taskTypes.getValue() == null) {
                    alert("No task type selected!");
                } else if (taskLength.getValue() == null) {
                    alert("No task length selected!");
                } else {
                    String name = taskName.getText();
                    String type = taskTypes.getValue();
                    long length = taskLength.getValue();
                    for (Task task : tasks) {
                        if (task.getName().equals(name)) {
                            if (task.getType().equals(type) && task.getLength() == length) {
                                alert("Task already exists!");
                            } else if (task.getType().equals(type)) {
                                task.setDue(task.getTime().plusHours(length).format(formatter));
                                table.refresh();
                            } else {
                                alert("Task already exists!");
                            }
                            return;
                        }
                    }
                    tasks.add(new Task(name, type, LocalDateTime.now(), length));
                    remaining.setText("Tasks Remaining: " + tasks.size());
                }
            }
        });
        dequeue.setOnAction(e -> {
            if (!tasks.isEmpty()) {
                completedTasks.add(new Task(tasks.get(0)));
                tasks.remove(0);
                completed.setText("Tasks Completed: " + completedTasks.size());
                remaining.setText("Tasks Remaining: " + tasks.size());
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setBottom(bottom);
        root.setCenter(center);

        Scene scene = new Scene(root, 1000, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    /**
     * Class that represents a task and allows it to be displayed in a TableView using SimpleStringProperty.
     */
    public static class Task {
        private SimpleStringProperty name;
        private SimpleStringProperty type;
        private SimpleStringProperty due;
        private LocalDateTime time;
        private long length;
        private SimpleStringProperty completed;

        /**
         * Constructor for a Task object.
         * @param name Name of task
         * @param type Type of task
         * @param time Time task was created
         * @param length Length of task
         */
        public Task(String name, String type, LocalDateTime time, long length) {
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
            this.time = time;
            this.length = length;
            this.due = new SimpleStringProperty(time.plusHours(length).format(formatter));
        }

        /**
         * Copy constructor for Task object that copies completed task and stores completed time.
         * @param t Task object to copy
         */
        public Task(Task t) {
            this.name = t.name;
            this.type = t.type;
            this.due = t.due;
            this.time = t.time;
            this.length = t.length;
            this.completed = new SimpleStringProperty(LocalDateTime.now().format(formatter));
        }

        /**
         * Getter for name.
         * @return Name
         */
        public String getName() {
            return name.get();
        }

        /**
         * Getter for type.
         * @return Type
         */
        public String getType() {
            return type.get();
        }

        /**
         * Getter for due.
         * @return Due
         */
        public String getDue() {
            return due.get();
        }

        /**
         * Getter for length.
         * @return Length
         */
        public long getLength() {
            return length;
        }

        /**
         * Getter for completed.
         * @return Completed
         */
        public String getCompleted() {
            return completed.get();
        }

        /**
         * Getter for time.
         * @return Time
         */
        public LocalDateTime getTime() {
            return time;
        }

        /**
         * Setter for due.
         * @param due New due date for task
         */
        public void setDue(String due) {
            this.due.set(due);
        }
    }
}
