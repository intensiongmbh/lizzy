/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.eclipse.org/legal/epl-2.0/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.plugin.listener;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;

/**
 * Implementation of {@link IPartListener} which executes a {@link ListenerAction}
 * when a part with a given ID is activated.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
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
