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
