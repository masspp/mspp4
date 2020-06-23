/*
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
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Satoshi Tanaka
 * @since 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.view.list;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.tools.FXTools;

/**
 * spectrum table view
 */
public class SpectrumTableView extends TableView< Spectrum > {
	/**
	 * constructor
	 */
	public SpectrumTableView() {
		super();
		this.initialize();
	}

	/**
	 * initializes
	 */
	protected void initialize() {
		this.getColumns().clear();

		// Name
		TableColumn< Spectrum, String > stringColumn = new TableColumn< Spectrum, String >( "Name" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, String >( "name" )  );
		this.getColumns().add( stringColumn );

		// RT
		TableColumn< Spectrum, Double > doubleColumn = new TableColumn< Spectrum, Double >( "RT" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "startRt" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );

		// Stage
		TableColumn< Spectrum, Integer > intColumn = new TableColumn< Spectrum, Integer >( "Stage" );
		intColumn.setPrefWidth( 80.0 );
		intColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Integer>( "msStage" ) );
		FXTools.setTableColumnRightAlign( intColumn );
		this.getColumns().add( intColumn );

		// Polarity
		stringColumn = new TableColumn< Spectrum, String >( "Polarity" );
		stringColumn.setPrefWidth( 80.0 );
		stringColumn.setCellValueFactory(
			( cellData ) -> {
				Spectrum spectrum = cellData.getValue();
				String string = "";
				int polarity = spectrum.getPolarity();
				if( polarity > 0 ) {
					string = "+";
				}
				else if( polarity < 0 ) {
					string = "-";
				}
				return new ReadOnlyStringWrapper( string );
			}
		);
		this.getColumns().add( stringColumn );

		// precursor
		doubleColumn = new TableColumn< Spectrum, Double >( "Precursor" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "precursor" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );

		// TIC
		doubleColumn = new TableColumn< Spectrum, Double >( "TIC" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "tic" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  0 );
		this.getColumns().add( doubleColumn );

		// BPM
		doubleColumn = new TableColumn< Spectrum, Double >( "BPM" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "bpm" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );

		// BPM
		doubleColumn = new TableColumn< Spectrum, Double >( "BPI" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "bpi" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  0 );
		this.getColumns().add( doubleColumn );
	}
}
