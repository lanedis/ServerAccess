/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import ru.naumen.servacc.platform.Platform;

public class FilteredTree extends Composite
{
    private Text filter;
    private Tree tree;

    public Text getFilter()
    {
        return filter;
    }

    public synchronized Tree getTree()
    {
        return tree;
    }

    public FilteredTree(Composite parent, Platform platform, int style)
    {
        super(parent, style);
        // Setup layout
        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = 0;
        setLayout(layout);
        // Filter
        if (platform.useSystemSearchWidget())
        {
            filter = new Text(this, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
        }
        else
        {
            // Add magnifier icon manually, SWT.SEARCH works for Mac OS X only
            Composite composite = new Composite(this, SWT.NONE);
            GridLayout compositeLayout = new GridLayout(2, false);
            compositeLayout.marginHeight = compositeLayout.marginWidth = 0;
            composite.setLayout(compositeLayout);
            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
            Label magnifier = new Label(composite, SWT.NONE);
            magnifier.setImage(ImageCache.getImage("/icons/magnifier-left.png"));
            magnifier.addMouseListener(new MouseAdapter()
            {
                public void mouseDown(MouseEvent e)
                {
                    filter.setFocus();
                }
            });
            filter = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
        }
        filter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        // Tree
        tree = new Tree(this, SWT.BORDER | SWT.SINGLE); // TODO: SWT.VIRTUAL
        tree.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    }
}
