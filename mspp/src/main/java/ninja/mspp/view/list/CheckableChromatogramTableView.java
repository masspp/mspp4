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
 * @author satstnka
 * @since Tue Sep 10 18:46:25 JST 2019
 *
 * Copyright (c) 2019 satstnka
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
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.tools.FXTools;

public class CheckableChromatogramTableView extends ChromatogramTableView {
	/**
	 * check event
	 */
	public interface CheckEvent {
		public void onCheck( Chromatogram chromatogram, boolean checked );
	}

	private Set< Chromatogram > chromatogramSet;
	private CheckEvent event;

	/**
	 * constructor
	 */
	public CheckableChromatogramTableView() {
		super();
		this.event = null;
		this.chromatogramSet = new HashSet< Chromatogram >();
		this.addCheckColumn();
	}

	public CheckEvent getEvent() {
		return event;
	}

	public void setEvent(CheckEvent event) {
		this.event = event;
	}

	/**
	 * gets selected chromatograms
	 * @return selected chromatograms
	 */
	public List< Chromatogram > getSelectedChromatograms() {
		List< Chromatogram > list = new ArrayList< Chromatogram >();

		for( Chromatogram chromatogram : this.getItems() ) {
			if( this.chromatogramSet.contains( chromatogram ) ) {
				list.add( chromatogram );
			}
		}

		return list;
	}

	/**
	 * sets selected flag
	 * @param chromatogram chromatogram
	 * @param selected selected flag value
	 */
	public void select( Chromatogram chromatogram, boolean selected ) {
		if( selected ) {
			this.chromatogramSet.add( chromatogram );
		}
		else {
			this.chromatogramSet.remove( chromatogram );
		}
		this.refresh();
	}

	/**
	 * clears selection
	 */
	public void clearSelection() {
		this.chromatogramSet.clear();
		this.refresh();
	}

	/**
	 * adds check column
	 */
	private void addCheckColumn() {
		CheckableChromatogramTableView me = this;

		TableColumn< Chromatogram, Chromatogram > column = new TableColumn< Chromatogram, Chromatogram >();
		column.setPrefWidth( 30.0 );
		FXTools.setTableColumnCenterAlign( column );

		CheckBox check = new CheckBox();
		check.selectedProperty().addListener(
			( observable, oldValue, newValue ) -> {
				if( newValue ) {
					for( Chromatogram chromatogram : me.getItems() ) {
						me.chromatogramSet.add( chromatogram );
					}
				}
				else {
					me.chromatogramSet.clear();
				}
				me.refresh();
				me.onCheck( null, newValue );
			}
		);
		column.setGraphic( check );

		column.setCellValueFactory(
			param -> {
				return new ReadOnlyObjectWrapper< Chromatogram >( param.getValue() );
			}
		);

		column.setCellFactory(
			param -> {
				return new TableCell< Chromatogram, Chromatogram >() {
					@Override
					protected void updateItem(Chromatogram item, boolean empty) {
						super.updateItem(item, empty);
						if( empty ) {
							setGraphic( null );
						}
						else {
							CheckBox chromatogramCheck = new CheckBox();
							chromatogramCheck.setSelected( me.chromatogramSet.contains( item ) );
							chromatogramCheck.selectedProperty().addListener(
								( observable, oldValue, newValue ) -> {
									me.onCheck( item, newValue );
									if( me.event != null ) {
										me.event.onCheck( item,  newValue );
									}
								}
							);
							setGraphic( chromatogramCheck );
						}
					}
				};
			}
		);

		this.getColumns().add( 0, column );
	}

	/**
	 * on check
	 * @param chromatogram chromatogram
	 * @param checked checked flag
	 */
	private void onCheck( Chromatogram chromatogram, boolean checked ) {
		if( checked ) {
			this.chromatogramSet.add( chromatogram );
		}
		else {
			this.chromatogramSet.remove( chromatogram );
		}
	}


}
