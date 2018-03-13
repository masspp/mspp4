package ninja.mspp.model.gui;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * menu group
 */
public class MenuGroup implements Iterable< MenuInfo > {
	private String name;
	private ArrayList< MenuInfo > items;

	/**
	 * constructor
	 * @param name name
	 */
	public MenuGroup( String name ) {
		this.name = name;
		this.items = new ArrayList< MenuInfo >();
	}

	@Override
	public Iterator< MenuInfo > iterator() {
		return this.items.iterator();
	}

	/**
	 * gets the name
	 * @return menu group name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * gets the number of items
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	/**
	 * gets the item
	 * @param index item index
	 * @return item
	 */
	public MenuInfo getItem( int index ) {
		MenuInfo item = null;
		if( index >= 0 && index < this.items.size() ) {
			item = this.items.get( index );
		}
		return item;
	}

	/**
	 * searches item
	 * @param name menu name
	 * @return menu item
	 */
	public MenuInfo searchItem( String name ) {
		MenuInfo item = null;
		for( MenuInfo tmp : this.items ) {
			if( tmp.getName().equals( name ) ) {
				item = tmp;
			}
		}

		return item;
	}

	/**
	 * adds item
	 * @param item item
	 */
	public void addItem( MenuInfo item ) {
		if( item != null ) {
			this.items.add( item );
		}
	}

	/**
	 * sorts items
	 */
	public void sort() {
		this.items.sort(
			( item1, item2 ) -> {
				int order1 = item1.getOrder().ordinal();
				int order2 = item2.getOrder().ordinal();
				int cmp = ( order1 - order2 );

				if( cmp == 0 ) {
					cmp = item1.getName().compareTo( item2.getName() );
				}
				return cmp;
			}
		);
	}

	/**
	 * calculates order valuke
	 * @return order value
	 */
	public double calculateOrderValue() {
		double value = 0.0;

		if( this.items.size() > 0 ) {
			for( MenuInfo item : this.items ) {
				value += (double)item.getOrder().ordinal();
			}
			value /= (double)this.items.size();
		}

		return value;
	}

}
