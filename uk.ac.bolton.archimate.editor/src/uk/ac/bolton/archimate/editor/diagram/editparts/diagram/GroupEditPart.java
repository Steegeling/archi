/*******************************************************************************
 * Copyright (c) 2010 Bolton University, UK.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 *******************************************************************************/
package uk.ac.bolton.archimate.editor.diagram.editparts.diagram;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.DirectEditManager;

import uk.ac.bolton.archimate.editor.diagram.directedit.LabelCellEditorLocator;
import uk.ac.bolton.archimate.editor.diagram.directedit.LabelDirectEditManager;
import uk.ac.bolton.archimate.editor.diagram.editparts.AbstractConnectedEditPart;
import uk.ac.bolton.archimate.editor.diagram.editparts.IColoredEditPart;
import uk.ac.bolton.archimate.editor.diagram.editparts.ITextEditPart;
import uk.ac.bolton.archimate.editor.diagram.editparts.SnapEditPartAdapter;
import uk.ac.bolton.archimate.editor.diagram.figures.IContainerFigure;
import uk.ac.bolton.archimate.editor.diagram.figures.IDiagramModelObjectFigure;
import uk.ac.bolton.archimate.editor.diagram.figures.ILabelFigure;
import uk.ac.bolton.archimate.editor.diagram.figures.diagram.GroupFigure;
import uk.ac.bolton.archimate.editor.diagram.policies.BasicContainerEditPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.ContainerHighlightEditPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.DiagramConnectionPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.DiagramLayoutPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.GroupContainerComponentEditPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.PartDirectEditTitlePolicy;
import uk.ac.bolton.archimate.editor.ui.ViewManager;
import uk.ac.bolton.archimate.model.IDiagramModelContainer;
import uk.ac.bolton.archimate.model.IDiagramModelObject;


/**
 * Group Edit Part
 * 
 * @author Phillip Beauvoir
 */
public class GroupEditPart extends AbstractConnectedEditPart
implements IColoredEditPart, ITextEditPart {
    
    private ConnectionAnchor fAnchor;
    private DirectEditManager fManager;

    @Override
    protected List<?> getModelChildren() {
        return ((IDiagramModelContainer)getModel()).getChildren();
    }

    @Override
    protected void createEditPolicies() {
        // Allow parts to be connected
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DiagramConnectionPolicy());

        // Add a policy to handle directly editing the Parts (for example, directly renaming a part)
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new PartDirectEditTitlePolicy());

        // Add a policy to handle editing the Parts (for example, deleting a part)
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new GroupContainerComponentEditPolicy());
        
        // Install a custom layout policy that handles dragging things around and creating new objects
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutPolicy());
        
        // Orphaning
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new BasicContainerEditPolicy());
        
        // Snap to Geometry feedback
        installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$

        // Selection Feedback
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy());
    }

    @Override
    protected IFigure createFigure() {
        GroupFigure figure = new GroupFigure((IDiagramModelObject)getModel());
        return figure;
    }
    
    @Override
    public IFigure getContentPane() {
        return ((IContainerFigure)getFigure()).getContentPane();
    }

    @Override
    protected void refreshFigure() {
        // Refresh the figure if necessary
        ((IDiagramModelObjectFigure)getFigure()).refreshVisuals();
    }

    /** 
     * Edit Requests are handled here
     */
    @Override
    public void performRequest(Request request) {
        if(request.getType() == RequestConstants.REQ_DIRECT_EDIT || request.getType() == RequestConstants.REQ_OPEN) {
            // Edit the label if we clicked on it
            if(((ILabelFigure)getFigure()).didClickLabel(((LocationRequest)request).getLocation().getCopy())) {
                if(fManager == null) {
                    Label label = ((ILabelFigure)getFigure()).getLabel();
                    fManager = new LabelDirectEditManager(this, new LabelCellEditorLocator(label), label);
                }
                fManager.show();
            }
            // Open Properties view
            else if(request.getType() == RequestConstants.REQ_OPEN) {
                ViewManager.showViewPart(ViewManager.PROPERTIES_VIEW, true);
            }
        }
    }
    
    @Override
    protected ConnectionAnchor getConnectionAnchor() {
        if(fAnchor == null) {
            fAnchor = ((GroupFigure)getFigure()).createConnectionAnchor();
        }
        return fAnchor;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if(adapter == SnapToHelper.class) {
            return new SnapEditPartAdapter(this).getSnapToHelper();
        }
        
        return super.getAdapter(adapter);
    }
}
