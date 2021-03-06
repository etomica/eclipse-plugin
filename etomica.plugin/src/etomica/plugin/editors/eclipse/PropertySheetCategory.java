/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package etomica.plugin.editors.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * A category in a PropertySheet used to group <code>IPropertySheetEntry</code>
 * entries so they are displayed together.
 */
/*package*/class PropertySheetCategory {
    private String categoryName;

    private List entries = new ArrayList();

    private boolean shouldAutoExpand = true;

    /**
     * Create a PropertySheet category with name.
     */
    public PropertySheetCategory(String name) {
        categoryName = name;
    }

    /**
     * Add an <code>IPropertySheetEntry</code> to the list
     * of entries in this category. 
     */
    public void addEntry(IPropertySheetEntry entry) {
        entries.add(entry);
    }

    /**
     * Return the category name.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns <code>true</code> if this category should be automatically 
     * expanded. The default value is <code>true</code>.
     * 
     * @return <code>true</code> if this category should be automatically 
     * expanded, <code>false</code> otherwise
     */
    public boolean getAutoExpand() {
        return shouldAutoExpand;
    }

    /**
     * Sets if this category should be automatically 
     * expanded.
     */
    public void setAutoExpand(boolean autoExpand) {
        shouldAutoExpand = autoExpand;
    }

    /**
     * Returns the entries in this category.
     *
     * @return the entries in this category
     */
    public IPropertySheetEntry[] getChildEntries() {
        return (IPropertySheetEntry[]) entries
                .toArray(new IPropertySheetEntry[entries.size()]);
    }

    /**
     * Removes all of the entries in this category.
     * Doing so allows us to reuse this category entry.
     */
    public void removeAllEntries() {
        entries = new ArrayList();
    }
}
