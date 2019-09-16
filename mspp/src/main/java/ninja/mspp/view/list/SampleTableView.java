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
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.view.list;

import java.sql.Timestamp;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.model.entity.Sample;

/**
 * sample table view
 */
public class SampleTableView extends TableView< Sample >{
	/**
	 * constructor
	 */
	public SampleTableView() {
		super();
		this.initialize();
	}

	/**
	 * initializes table
	 */
	private void initialize() {
		TableColumn< Sample, String > stringColumn = new TableColumn< Sample, String >( "Name" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "filename" ) );
		this.getColumns().add( stringColumn );

		stringColumn = new TableColumn< Sample, String >( "Instrument" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "instrumentVendor" ) );
		this.getColumns().add( stringColumn );

		stringColumn = new TableColumn< Sample, String >( "Acquisition Software" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "acquisitionsoftware" ) );
		this.getColumns().add( stringColumn );

		TableColumn< Sample, Timestamp > timeColumn = new TableColumn< Sample, Timestamp >( "Registration Date" );
		timeColumn.setPrefWidth( 150.0 );
		timeColumn.setCellValueFactory( new PropertyValueFactory< Sample, Timestamp >( "registrationDate" ) );
		this.getColumns().add( timeColumn );

		stringColumn = new TableColumn< Sample, String >( "Comment" );
		stringColumn.setPrefWidth( 250.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "userComment" ) );
		this.getColumns().add( stringColumn );
	}
}
