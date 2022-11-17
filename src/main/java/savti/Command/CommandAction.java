package savti.Command;

import javax.swing.*;
import java.awt.event.ActionEvent;
/**
 * CommandAction is used as a listener for the commands when the button is pressed.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */

public class CommandAction extends AbstractAction {
    private final Command command ;
    /**
     * Constructor for the CommandAction class.
     * @param command is the command that is being executed if an action is performed.
     */
    public CommandAction(final Command command) {
        super();
        this.command = command;
        }
    @Override
    public void actionPerformed(ActionEvent e) {command.execute();}
}
