import javafx.application.*;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class TimelineViewer extends Application {

    TextArea details = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Timeline");
        primaryStage.setScene(new Scene(splitter(), 1000, 800));
        primaryStage.show();
        details.setWrapText(true);
        details.setEditable(false);
    }

    SplitPane splitter() {
        SplitPane splitter = new SplitPane();
        splitter.getItems().addAll(root(), new StackPane(details));
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
            TreeValue selectedItem = (TreeValue) newValue;
            details.setText(selectedItem.detail);
        });
    }

    static class TreeValue extends TreeItem<String> {
        final String detail;

        TreeValue(String text, String detail) {
            super(text);
            this.detail = detail;
        }

    }

    TreeItem<String> rootItem() {
        TreeValue root = new TreeValue("Timeline","Root");
        root.setExpanded(true);
        for (Channel channel : timeline().channels) {
            root.getChildren().add(channelItem(channel));
        }
        return root;
    }

    TreeItem<String> channelItem(Channel channel) {
        TreeValue channelItem = new TreeValue(channel.summary(),"Channel " + channel.summary());
        for (Conversation conversation : channel.conversations) {
            channelItem.getChildren().add(conversationItem(conversation));
        }
        return channelItem;
    }

    TreeItem<String> conversationItem(Conversation conversation) {
        TreeValue conversationItem = new TreeValue(conversation.summary(),"Conversation " + conversation.summary());
        for (Message message : conversation.messages) {
            conversationItem.getChildren().add(messageItem(message));
        }
        return conversationItem;
    }

    TreeItem<String> messageItem(Message message) {
        TreeValue messageItem = new TreeValue(message.summary(),"Message " + message.summary());
        for (Packet packet : message.packets) {
            messageItem.getChildren().add(packetItem(packet));
        }
        return messageItem;
    }

    TreeItem<String> packetItem(Packet packet) {
        return new TreeValue(packet.toString(),packet.line);
    }

    Timeline timeline() {
        FilteredTimelineFactory factory = new FilteredTimelineFactory();
        return factory.timeline();
    }
}