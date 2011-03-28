/*******************************************************************************
 * Copyright (c) 2010 Bolton University, UK.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 *******************************************************************************/
package uk.ac.bolton.archimate.editor.diagram.editparts;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.tools.DirectEditManager;

import uk.ac.bolton.archimate.editor.diagram.directedit.LabelCellEditorLocator;
import uk.ac.bolton.archimate.editor.diagram.directedit.LabelDirectEditManager;
import uk.ac.bolton.archimate.editor.diagram.figures.ILabelFigure;
import uk.ac.bolton.archimate.editor.diagram.policies.PartComponentEditPolicy;
import uk.ac.bolton.archimate.editor.diagram.policies.PartDirectEditTitlePolicy;
import uk.ac.bolton.archimate.editor.ui.ViewManager;

/**
 * Edit Part with connections and editable label
 * 
 * @author Phillip Beauvoir
 */
public abstract class AbstractArchimateEditableLabelEditPart
extends AbstractArchimateEditPart implements IColoredEditPart, ITextEditPart {

    private DirectEditManager fDirectEditManager;
    private ConnectionAnchor fAnchor;
    
    @Override
    protected ConnectionAnchor getConnectionAnchor() {
        if(fAnchor == null) {
            fAnchor = new ChopboxAnchor(getFigure());
        }
        return fAnchor;
    }
    
    @Override
    protected void refreshFigure() {
        getFigure().refreshVisuals();
    }
    
    @Override
    public ILabelFigure getFigure() {
        return (ILabelFigure)super.getFigure();
    }
    
    @Override
    protected void createEditPolicies() {
        // Add a policy to handle directly editing the Parts (for example, directly renaming a part)
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new PartDirectEditTitlePolicy());
        
        // Add a policy to handle editing the Parts (for example, deleting a part)
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new PartComponentEditPolicy());
    }
    
    @Override
    public void performRequest(Request request) {
        // REQ_DIRECT_EDIT is Single-click when already selected or a Rename command
        // REQ_OPEN is Double-click
        if(request.getType() == RequestConstants.REQ_DIRECT_EDIT || request.getType() == RequestConstants.REQ_OPEN) {
            if(request instanceof LocationRequest) {
                // Edit the text control if we clicked on it
                if(getFigure().didClickLabel(((LocationRequest)request).getLocation().getCopy())) {
                    getDirectEditManager().show();
                }
                // Else open Properties View on double-click
                else if(request.getType() == RequestConstants.REQ_OPEN){
                    ViewManager.showViewPart(ViewManager.PROPERTIES_VIEW, true);
                }
            }
            else {
                getDirectEditManager().show();
            }
        }
    }
    
    protected DirectEditManager getDirectEditManager() {
        if(fDirectEditManager == null) {
            fDirectEditManager = new LabelDirectEditManager(this, new LabelCellEditorLocator(getFigure().getLabel()),
                    getFigure().getLabel());
        }
        return fDirectEditManager;
    }

}