import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class TimelineViewer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Timeline");
        primaryStage.setScene(new Scene(root(), 300, 250));
        primaryStage.show();
    }

    StackPane root() {
        StackPane root = new StackPane();
        root.getChildren().add(treeView());
        return root;
    }

    TreeView treeView() {
        TreeItem<String> rootItem = new TreeItem<>("Timeline");
        rootItem.setExpanded(true);

        final TreeView tree = new TreeView(rootItem);
        loadChannels(rootItem,timeline());
        tree.setShowRoot(true);
        tree.setRoot(rootItem);
        return tree;
    }

    void loadChannels(TreeItem root, Timeline timeline) {
        for (Channel channel : timeline.channels) {
            TreeItem channelItem = new TreeItem<>()
            root.getChildren().add(new TreeItem<>(channel.summary()));
        }
    }

    Timeline timeline() {
        FilteredTimelineFactory factory = new FilteredTimelineFactory();
        return factory.timeline();
    }
}