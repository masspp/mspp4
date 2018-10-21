/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-05-30 20:29:47+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.MsppManager;
import ninja.mspp.MsppProperties;
import ninja.mspp.annotation.OnOpenSample;
import ninja.mspp.annotation.OpenSpectrum;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.Sample;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.tools.StringTool;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "Sample List" )
public class SampleListPlugin {
	TabPane tabPane;;

	@OnOpenSample
	public void onOpenSample( Sample sample ) {
		if( this.tabPane == null ) {
			this.addTabPane();
		}

		this.addTab( this.tabPane, sample );
	}

	/**
	 * adds tab pane
	 */
	private void addTabPane() {
		GuiManager manager = GuiManager.getInstance();
		MainFrame mainFrame = manager.getMainFrame();

		TabPane pane = new TabPane();
		mainFrame.addLeftTab( "Samples", pane );
		this.tabPane = pane;
	}

	/**
	 * adds tab
	 * @param tabPane tab pane
	 * @param sample sample
	 */
	private void addTab( TabPane tabPane, Sample sample ) {
		TableView< Spectrum > table = this.createTable( sample );
		Tab tab = new Tab( sample.getName() );
		tabPane.getTabs().add( tab );
		tab.setContent( table );
	}

	/**
	 * creates table view
	 * @param sample sample
	 * @return table view
	 */
	private TableView< Spectrum > createTable( Sample sample ) {
		TableView< Spectrum > table = new TableView< Spectrum >();
		table.setEditable( false );

		TableColumn< Spectrum, String > nameColumn = new TableColumn< Spectrum, String >( "Name" );
		nameColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, String >( "name" ) );
		table.getColumns().add( nameColumn );

		TableColumn< Spectrum, Double > rtColumn = new TableColumn< Spectrum, Double >( "RT" );
		rtColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "rt" ) );
		table.getColumns().add( rtColumn );

		TableColumn< Spectrum, Integer > stageColumn = new TableColumn< Spectrum, Integer >( "Stage" );
		stageColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Integer >( "msStage" ) );
		table.getColumns().add( stageColumn );

		table.getItems().addAll( sample.getSpectra() );
		table.setRowFactory(
			tableView -> {
				TableRow< Spectrum > row = new TableRow< Spectrum >();
				row.setOnMouseClicked(
					event -> {
						if( event.getClickCount() == 2 && !row.isEmpty() ) {
							Spectrum spectrum = row.getItem();
							if( spectrum != null ) {
								this.openSpectrum( spectrum );
							}
						}
					}
				);

				return row;
			}
		);

		return table;
	}

	/**
	 * opens spectrum
	 * @param spectrum
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void openSpectrum( Spectrum spectrum ) {
		MsppManager manager = MsppManager.getInstance();
		ArrayList< PluginMethod< OpenSpectrum > > methods = manager.getMethods( OpenSpectrum.class );
		PluginMethod< OpenSpectrum > method = null;

		MsppProperties properties = MsppProperties.getInstance();
		String view = StringTool.nvl( properties.getProperty( MsppProperties.KEY_SPECTRUM_VIEW ), "" );
		view = "profile";

		for( PluginMethod< OpenSpectrum > tmp : methods ) {
			OpenSpectrum annotation = tmp.getAnnotation();
			if( method == null || annotation.view().equals( view ) ) {
				method = tmp;
			}
		}

		if( method != null ) {
			Object plugin = method.getPlugin();
			try {
				method.getMethod().invoke( plugin, spectrum );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
