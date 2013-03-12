/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2013 Ausenco Engineering Canada Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.sandwell.JavaSimulation3D;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.jaamsim.ui.FrameBox;
import com.sandwell.JavaSimulation.Entity;

public class OutputBox extends FrameBox {

	private static OutputBox myInstance = new OutputBox();

	private JScrollPane scrollPane;
	private OutputTable table;

	private Entity currentEntity;
	private String[] outputNames;
	OutputTableModel tableModel;

	private class OutputTable extends JTable {
		public OutputTable(TableModel model) {
			super(model);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int row = rowAtPoint(p);
			if (row >= outputNames.length || currentEntity == null) {
				return null;
			}

			String outputName = outputNames[row];

			StringBuilder build = new StringBuilder();
			build.append("<HTML>");
			build.append("<b>Name:</b>  ");
			build.append(outputName);
			build.append("<BR>");
			String desc = currentEntity.getOutputDescripion(outputName);
			if (!desc.isEmpty()) {
				build.append("<BR>");
				build.append("<b>Description:</b> ");
				for (String line : desc.split("\n", 0)) {
					// Replace all <> for html parsing
					String tempLine = line.replaceAll("&", "&amp;");
					tempLine = tempLine.replaceAll("<", "&lt;");
					tempLine = tempLine.replaceAll(">", "&gt;");

					int len = 0;
					build.append("<BR>");
					// Break the line at 100-char boundaries
					for (String word : tempLine.split(" ", -1)) {
						build.append(word).append(" ");
						len += word.length() + 1;
						if (len > 100) {
							build.append("<BR>");
							len = 0;
						}
					}
				}
			}
			return build.toString();
		}
	}

	public OutputBox() {
		super( "Output Viewer" );
		setDefaultCloseOperation(FrameBox.HIDE_ON_CLOSE);

		tableModel = new OutputTableModel();
		table = new OutputTable(tableModel);
		scrollPane = new JScrollPane(table);

		getContentPane().add( scrollPane );
		setSize( 300, 150 );
		setLocation(0, 110);

		outputNames = new String[0];

		table.setDefaultRenderer(Object.class, colRenderer);

		pack();
	}

	@Override
	public void setEntity( Entity entity ) {
		currentEntity = entity;
		if (currentEntity != null) {
			outputNames = currentEntity.getOutputNames(false);
		} else {
			outputNames = new String[0];

		}

		updateValues();
	}

	@Override
	public void updateValues() {
		tableModel.fireTableDataChanged();
	}

	private class OutputTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Name";
			case 1:
				return "Value";
			default:
				assert false;
				return null;
			}
		}

		@Override
		public int getRowCount() {
			return outputNames.length;
		}

		@Override
		public Object getValueAt(int row, int col) {

			switch (col) {
			case 0:
				return outputNames[row];
			case 1:
				return currentEntity.getOutputAsString(outputNames[row], 0);
			default:
				assert false;
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

	/**
	 * Returns the only instance of the property box
	 */
	public synchronized static OutputBox getInstance() {
		if (myInstance == null)
			myInstance = new OutputBox();

		return myInstance;
	}

	@Override
	public void dispose() {
		myInstance = null;
		super.dispose();
	}

}
