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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.tools.FXTools;

public class CheckableSpectrumTableView extends SpectrumTableView {
	/**
	 * check event
	 */
	public interface CheckEvent {
		public void onCheck( Spectrum spectrum, boolean checked );
	}

	private Set< Spectrum > spectrumSet;
	private CheckEvent event;

	/**
	 * constructor
	 */
	public CheckableSpectrumTableView() {
		super();
		this.event = null;

		this.spectrumSet = new HashSet< Spectrum >();
		this.addCheckColumn();
	}

	public CheckEvent getEvent() {
		return event;
	}

	public void setEvent(CheckEvent event) {
		this.event = event;
	}

	/**
	 * gets selected spectgra
	 * @return selected spectra
	 */
	public List< Spectrum > getSelectedSpectra() {
		List< Spectrum > list = new ArrayList< Spectrum >();

		for( Spectrum spectrum : this.getItems() ) {
			if( this.spectrumSet.contains( spectrum ) ) {
				list.add( spectrum );
			}
		}

		return list;
	}

	/**
	 * sets the seletecte flag
	 * @param spectrum spectrum
	 * @param selected selected flag value
	 */
	public void select( Spectrum spectrum, boolean selected ) {
		if( selected ) {
			this.spectrumSet.add( spectrum );
		}
		else {
			this.spectrumSet.remove( spectrum );
		}
		this.refresh();
	}

	/**
	 * adds check column
	 */
	private void addCheckColumn() {
		CheckableSpectrumTableView me = this;

		TableColumn< Spectrum, Spectrum > column = new TableColumn< Spectrum, Spectrum >();
		column.setPrefWidth( 30.0 );
		FXTools.setTableColumnCenterAlign( column );

		CheckBox check = new CheckBox();
		check.selectedProperty().addListener(
			( observable, oldValue, newValue ) -> {
				if( newValue ) {
					for( Spectrum spectrum : me.getItems() ) {
						me.spectrumSet.add( spectrum );
					}
				}
				else {
					me.spectrumSet.clear();
				}
				me.refresh();
				this.onCheck( null, newValue );
			}
		);
		column.setGraphic( check );

		column.setCellValueFactory(
			param -> {
				return new ReadOnlyObjectWrapper< Spectrum >( param.getValue() );
			}
		);

		column.setCellFactory(
			param -> {
				return new TableCell< Spectrum, Spectrum >() {

					@Override
					protected void updateItem(Spectrum item, boolean empty) {
						super.updateItem(item, empty);
						if( empty ) {
							setGraphic( null );
						}
						else {
							CheckBox spectrumCheck = new CheckBox();
							spectrumCheck.setSelected( me.spectrumSet.contains( item ) );
							spectrumCheck.selectedProperty().addListener(
								( observable, oldValue, newValue ) -> {
									me.onCheck( item, newValue );
									if( me.event != null ) {
										me.event.onCheck( item,  newValue );
									}
								}
							);
							setGraphic( spectrumCheck );
						}
					}
				};
			}
		);

		this.getColumns().add( 0, column );
	}

	/**
	 * on check
	 * @param spectrum spectrum
	 * @param checked checked flag
	 */
	private void onCheck( Spectrum spectrum, boolean checked ) {
		if( checked ) {
			this.spectrumSet.add( spectrum );
		}
		else {
			this.spectrumSet.remove( spectrum );
		}
	}
}
