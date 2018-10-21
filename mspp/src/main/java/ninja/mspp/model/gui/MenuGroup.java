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
 * @since 2018-03-13 18:14:26+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
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
	 * @return menu item size
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
