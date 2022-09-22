package JavaFXVersion.utilities;

import javafx.scene.Node;

import java.util.List;

public class GUIUtilities {

    public static void ableNodes(List<Node> disable, List<Node> enable){

        for(Node n: disable)
            n.setDisable(true);

        for(Node n: enable)
            n.setDisable(false);
    }

}
