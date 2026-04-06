package com.ecohabit.fx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Animation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import java.io.File;
import java.sql.*;


public class MainApp extends Application {

    private Label pointsLabel, levelLabel, totalLabel, streakLabel, progressText;
    private ProgressBar progress;
    private VBox root;

    @Override
    public void start(Stage stage) {
        // --- Dashboard UI ---
    	// 1. Load the Logo Image
    	Image logoImg = new Image(getClass().getResourceAsStream("ethos_logo.png"));
    	ImageView logoView = new ImageView(logoImg);

    	// 2. Set the size (adjust based on your design)
    	logoView.setFitWidth(350); 
    	logoView.setPreserveRatio(true);

    	// 3. Add a soft "Floating" animation to the logo
    	TranslateTransition floatingLogo = new TranslateTransition(Duration.seconds(2), logoView);
    	floatingLogo.setByY(10);
    	floatingLogo.setCycleCount(Animation.INDEFINITE);
    	floatingLogo.setAutoReverse(true);
    	floatingLogo.play();


        pointsLabel = new Label();
        levelLabel = new Label();
        totalLabel = new Label();
        streakLabel = new Label();
        progressText = new Label();
        progress = new ProgressBar(0);
        progress.setPrefWidth(300);
        progress.setStyle("-fx-accent: #4CAF50;");

        Button addBtn = createStyledButton("Add New Habit", "#4CAF50");
        Button viewBtn = createStyledButton("View My Habits", "#2196F3");
        Button chartBtn = createStyledButton("View Analytics", "#FF9800");
        Button resetBtn = new Button("Reset Game 🔄");
        
        VBox statsPanel = new VBox(10, pointsLabel, levelLabel, totalLabel, streakLabel, progress, progressText);
        statsPanel.setAlignment(Pos.CENTER);
        statsPanel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.7); " + 
            "-fx-background-radius: 20; " +
            "-fx-padding: 20; " +
            "-fx-max-width: 350; " +
            "-fx-border-color: #5d4037; " + 
            "-fx-border-width: 3; " +
            "-fx-border-radius: 20;"
        );
        
        addBtn.setOnAction(e -> openAddHabitWindow());
        viewBtn.setOnAction(e -> openViewHabitsWindow());
        chartBtn.setOnAction(e -> openChartWindow());
        resetBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to reset all points and habits?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try (Connection conn = DBConnection.getConnection()) {
                        // Clear habits and reset points
                        conn.prepareStatement("DELETE FROM habits").executeUpdate();
                        conn.prepareStatement("UPDATE users SET points = 0 WHERE id = 1").executeUpdate();
                        
                        refreshDashboard(); // Update the UI immediately
                        System.out.println("Game Reset Successful!");
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
        });

        root = new VBox(20, logoView, statsPanel, addBtn, viewBtn, chartBtn, resetBtn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40; -fx-background-color: linear-gradient(to bottom, #f0fff4, #dcfce7);");
        resetBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        
        refreshDashboard(); 
        
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        stage.setScene(new Scene(scroll, 500, 800));
        stage.setTitle("EthosXP Dashboard");
        stage.show();
    }
    
    private Button createStyledButton(String text, String color) {
        Button b = new Button(text);
        java.net.URL imageUrl = getClass().getResource("plank.png");

        if (imageUrl == null) {
            System.err.println("CRITICAL: plank.png not found in com.ecohabit.fx package!");
            b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        } else {
            String externalPath = imageUrl.toExternalForm();
            String style = "-fx-background-image: url('" + externalPath + "'); " +
            		"-fx-background-size: 100% 100%; " + 
                    "-fx-background-repeat: no-repeat; " +
                    "-fx-background-color: transparent; " + 
                    "-fx-font-family: 'Luckiest Guy'; " +   
                    "-fx-font-size: 20px; " + // Increased for game feel
                    "-fx-text-fill: #5d4037; " +           
                    "-fx-cursor: hand; " +
                    "-fx-font-weight: bold;";
            b.setStyle(style);
            b.setPrefSize(280, 80); // Adjusted size to better fit the plank aspect ratio

            // Hover effect: Brighten the button when mouse is over it
            b.setOnMouseEntered(e -> b.setOpacity(0.9));
            b.setOnMouseExited(e -> b.setOpacity(1.0));

            // "Pressed" effect for that tactile stone/wood feel
            b.setOnMousePressed(e -> b.setTranslateY(4)); 
            b.setOnMouseReleased(e -> b.setTranslateY(0));
        }

        return b;
    }
    
    private void playSuccessSound() {
        try {
            String path = "src/achieve.mp3"; 
            File file = new File(path);
            if (file.exists()) {
                Media hit = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Sound file not found, skipping sound.");
        }
    }

    private void openAddHabitWindow() {
        Stage stage = new Stage();
        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");

        TextField nameField = new TextField();
        nameField.setPromptText("Habit Name (e.g. Planted a tree)");

        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Energy 💡", "Water 💧", "Waste ♻️", "Food 🍎", "Transport 🚲");
        catBox.setPromptText("Select Category");
        catBox.setMaxWidth(Double.MAX_VALUE);

        Button save = new Button("Save Habit");
        save.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        save.setOnAction(e -> {
            String name = nameField.getText();
            String cat = catBox.getValue();
            if (name.isEmpty() || cat == null) return;

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO habits (user_id, habit_name, category, date, status) VALUES (1, ?, ?, CURDATE(), 'PENDING')";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, cat);
                ps.executeUpdate();
                refreshDashboard();
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        layout.getChildren().addAll(new Label("Select your next Mission, Warrior."), nameField, catBox, save);
        stage.setScene(new Scene(layout, 350, 300));
        stage.setTitle("Add Habit");
        stage.show();
    }

    private void openViewHabitsWindow() {
        Stage stage = new Stage();
        TableView<Habit> table = new TableView<>();

        // --- 1. RPG Icon Column ---
        TableColumn<Habit, String> iconCol = new TableColumn<>("Type");
        iconCol.setCellValueFactory(new PropertyValueFactory<>("categoryIcon"));
        iconCol.setPrefWidth(60);

        // --- 2. Mission Name Column (The one that was missing!) ---
        TableColumn<Habit, String> nameCol = new TableColumn<>("Mission");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        // --- 3. Action Column (Done / Delete) ---
        TableColumn<Habit, Void> actionCol = new TableColumn<>("Reward");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button doneBtn = new Button("Done ✅");
            private final Button delBtn = new Button("🗑");

            {
                doneBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                delBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
                
                doneBtn.setOnAction(e -> {
                    Habit h = getTableView().getItems().get(getIndex());
                    if (!"DONE".equals(h.getStatus())) {
                        updateHabitStatus(h.getId());
                        h.setStatus("DONE");
                        table.refresh();
                        showFloatingPoints(200, 300); 
                        playSuccessSound();
                    }
                });

                delBtn.setOnAction(e -> {
                    Habit h = getTableView().getItems().get(getIndex());
                    deleteHabitFromDB(h.getId());
                    getTableView().getItems().remove(h);
                    refreshDashboard();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Habit h = getTableView().getItems().get(getIndex());
                    if ("DONE".equals(h.getStatus())) {
                        doneBtn.setDisable(true);
                        doneBtn.setText("Completed");
                    } else {
                        doneBtn.setDisable(false);
                        doneBtn.setText("Done ✅");
                    }
                    setGraphic(new HBox(10, doneBtn, delBtn));
                }
            }
        });

        // Add all columns to the table in order
        table.getColumns().addAll(iconCol, nameCol, actionCol);
        
        loadTableData(table);

        VBox layout = new VBox(10, new Label("📜 Eco Quest Log"), table);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f0fff4;");
        stage.setScene(new Scene(layout, 550, 450));
        stage.setTitle("My Habits");
        stage.show();
    }

    private void loadTableData(TableView<Habit> table) {
        try (Connection conn = DBConnection.getConnection()) {
            // Fetching all columns including category
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM habits ORDER BY date DESC");
            while (rs.next()) {
                table.getItems().add(new Habit(
                    rs.getInt("id"), 
                    rs.getString("habit_name"), 
                    rs.getString("date"), 
                    rs.getString("status"),
                    rs.getString("category") 
                ));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

	private void updateHabitStatus(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("UPDATE habits SET status='DONE' WHERE id=" + id).executeUpdate();
            conn.prepareStatement("UPDATE users SET points = points + 10 WHERE id=1").executeUpdate();
            refreshDashboard();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteHabitFromDB(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM habits WHERE id=" + id).executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private boolean hasShownWarriorLevel = false;

    private void refreshDashboard() {
        int pts = getPoints();
        pointsLabel.setText("⭐ Points: " + pts);
        streakLabel.setText("🔥 Streak: " + getStreak() + " Days");
        totalLabel.setText("📊 Total Actions: " + getTotalHabits());
        progressText.setText("Progress: " + pts + " / 200");
        
        // Background logic
        String backgroundStyle;
        if (pts >= 50 && !hasShownWarriorLevel) {
            showLevelUpAnimation();
            hasShownWarriorLevel = true; // Prevents it from showing again until next session
        }
        if (pts < 50) {
            backgroundStyle = "-fx-background-color: linear-gradient(to bottom, #f0fff4, #dcfce7);";
            levelLabel.setText("🏆 Level: Beginner 🌱");
        } else if (pts < 150) {
            backgroundStyle = "-fx-background-color: linear-gradient(to bottom, #dcfce7, #86efac);";
            levelLabel.setText("🏆 Level: Eco Warrior 🌿");
        } else {
            backgroundStyle = "-fx-background-color: linear-gradient(to bottom, #fef08a, #86efac);";
            levelLabel.setText("🏆 Level: Green Hero 🌳");
        }
        
        root.setStyle("-fx-padding: 40; " + backgroundStyle);
        
        // Animations
        animateProgressBar(Math.min(pts / 200.0, 1.0));
        pulseNode(pointsLabel);
        
        String imageName = "forest_bg.png"; // Default
        if (pts >= 50 && pts < 150) {
            imageName = "mountain_bg.png"; // Level 2 Background
        } else if (pts >= 150) {
            imageName = "hero_bg.png";     // Level 3 Background
        }

        // 2. Load the background image safely
        java.net.URL bgResource = getClass().getResource(imageName);
        
        if (bgResource != null) {
            String bgUrl = bgResource.toExternalForm();
            root.setStyle("-fx-background-image: url('" + bgUrl + "'); " +
                          "-fx-background-size: cover; " +
                          "-fx-background-position: center; " + 
                          "-fx-padding: 40;");
        } else {
            root.setStyle("-fx-background-color: #dcfce7; -fx-padding: 40;");
            System.err.println("Warning: " + imageName + " not found!");
        }
     // Define a strong, high-contrast "Wood-Bark" Brown
        String deepBrown = "#3d2b1f"; 

        // Create a unified style for all stats
        String labelStyle = 
            "-fx-text-fill: " + deepBrown + "; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 18px; " +
            "-fx-font-family: 'Luckiest Guy'; " + // Use your game font!
            "-fx-effect: dropshadow(one-pass-box, rgba(255,255,255,0.8), 2, 0, 0, 0);"; // White outline

        pointsLabel.setStyle(labelStyle + "-fx-font-size: 22px;");
        levelLabel.setStyle(labelStyle);
        totalLabel.setStyle(labelStyle);
        streakLabel.setStyle(labelStyle);
        progressText.setStyle(labelStyle);

        // Make the Progress Bar match the theme
        progress.setStyle("-fx-accent: #4a7c44; -fx-control-inner-background: #d7ccc8;");
    }
 
    
    private void showFloatingPoints(double x, double y) {
        Label plusTen = new Label("+10 XP");
        plusTen.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 16px;");
        plusTen.setLayoutX(x);
        plusTen.setLayoutY(y);
        
        root.getChildren().add(plusTen);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), plusTen);
        tt.setByY(-50);
        
        FadeTransition ft = new FadeTransition(Duration.seconds(1), plusTen);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> root.getChildren().remove(plusTen));
        pt.play();
    }
    
    private void animateProgressBar(double targetValue) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(progress.progressProperty(), targetValue);
        KeyFrame kf = new KeyFrame(Duration.millis(800), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
    
    private void pulseNode(javafx.scene.Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
        st.setByX(0.2);
        st.setByY(0.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private int getPoints() {
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT points FROM users WHERE id=1")) {
            if (rs.next()) return rs.getInt("points");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getTotalHabits() {
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM habits")) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int getStreak() {
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT date) FROM habits WHERE status='DONE'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private void openChartWindow() {
        Stage stage = new Stage();
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        javafx.scene.chart.BarChart<String, Number> chart = new javafx.scene.chart.BarChart<>(xAxis, yAxis);
        chart.setTitle("Activity Log");
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT date, COUNT(*) as total FROM habits GROUP BY date");
            while (rs.next()) {
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(rs.getString("date"), rs.getInt("total")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        chart.getData().add(series);
        stage.setScene(new Scene(new VBox(chart), 600, 400));
        stage.show();
    }
    
    private void showLevelUpAnimation() {
        // 1. Load your designed banner
        Image bannerImg = new Image(getClass().getResourceAsStream("levelup.png"));
        ImageView bannerView = new ImageView(bannerImg);
        
        bannerView.setFitWidth(200);
        bannerView.setPreserveRatio(true);
        bannerView.setOpacity(0); // Start invisible
        
        // Position it in the center of the screen
        bannerView.setManaged(false); // Allows us to manually center it over everything
        bannerView.layoutXProperty().bind(root.widthProperty().divide(2).subtract(bannerView.getFitWidth() / 2));
        bannerView.layoutYProperty().bind(root.heightProperty().divide(2).subtract(100));

        root.getChildren().add(bannerView);

        // 2. The "Pop In" Animation
        ScaleTransition popIn = new ScaleTransition(Duration.seconds(0.6), bannerView);
        popIn.setFromX(0); popIn.setFromY(0);
        popIn.setToX(1.1); popIn.setToY(1.1);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), bannerView);
        fadeIn.setToValue(1.0);

        ParallelTransition show = new ParallelTransition(popIn, fadeIn);

        // 3. The "Stay and Fade Out" Logic
        show.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), bannerView);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> root.getChildren().remove(bannerView));
                fadeOut.play();
            });
            pause.play();
        });

        show.play();
        playSuccessSound(); // Play your achieve.mp3!
    }

    public static void main(String[] args) { launch(args); }
}
