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
