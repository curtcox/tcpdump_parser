import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class TimelineViewer extends Application {

    Label label = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Timeline");
        primaryStage.setScene(new Scene(splitter(), 1000, 800));
        primaryStage.show();
    }

    SplitPane splitter() {
        SplitPane splitter = new SplitPane();
        splitter.getItems().addAll(root(), new StackPane(label));
        splitter.setOrientation(Orientation.VERTICAL);
        splitter.setDividerPositions(0.95f);
        return splitter;
    }

    StackPane root() {
        StackPane root = new StackPane();
        root.getChildren().add(treeView());
        return root;
    }

    TreeView treeView() {
        TreeItem<String> root = rootItem();
        TreeView tree = new TreeView(root);
        addSelectionListener(tree);
        tree.setShowRoot(true);
        return tree;
    }

    void addSelectionListener(TreeView tree) {
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
            System.out.println("Selected Text : " + selectedItem.getValue());
            label.setText(selectedItem.getValue());
        });
    }

    TreeItem<String> rootItem() {
        TreeItem<String> root = new TreeItem<>("Timeline");
        root.setExpanded(true);
        for (Channel channel : timeline().channels) {
            root.getChildren().add(channelItem(channel));
        }
        return root;
    }

    TreeItem<String> channelItem(Channel channel) {
        TreeItem<String> channelItem = new TreeItem<>(channel.summary());
        for (Conversation conversation : channel.conversations) {
            channelItem.getChildren().add(conversationItem(conversation));
        }
        return channelItem;
    }

    TreeItem<String> conversationItem(Conversation conversation) {
        TreeItem<String> conversationItem = new TreeItem<>(conversation.summary());
        for (Message message : conversation.messages) {
            conversationItem.getChildren().add(messageItem(message));
        }
        return conversationItem;
    }

    TreeItem<String> messageItem(Message message) {
        TreeItem<String> messageItem = new TreeItem<>(message.summary());
        for (Packet packet : message.packets) {
            messageItem.getChildren().add(packetItem(packet));
        }
        return messageItem;
    }

    TreeItem<String> packetItem(Packet packet) {
        return new TreeItem<>(packet.toString());
    }

    Timeline timeline() {
        FilteredTimelineFactory factory = new FilteredTimelineFactory();
        return factory.timeline();
    }
}