package de.intension.lizzy.plugin.listener;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;

/**
 * Implementation of {@link IPartListener} which executes a {@link ListenerAction}
 * when a part with a given ID is activated.
 */
public class ExecuteActionListener
    implements IPartListener
{

    private String         id;
    private ListenerAction action;

    public ExecuteActionListener(String id, ListenerAction action)
    {
        this.id = id;
        this.action = action;
    }

    @Override
    public void partActivated(MPart part)
    {
        if (part.getElementId().equals(id)) {
            action.execute();
        }
    }

    @Override
    public void partBroughtToTop(MPart part)
    {
        // not used
    }

    @Override
    public void partDeactivated(MPart part)
    {
        // not used
    }

    @Override
    public void partHidden(MPart part)
    {
        // not used
    }

    @Override
    public void partVisible(MPart part)
    {
        // not used
    }

    /**
     * Implement this interface to determine the action
     * beeing executed when the Listener is triggered.
     */
    public interface ListenerAction
    {

        void execute();
    }
}
