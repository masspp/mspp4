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

import ninja.mspp.tools.StringTool;

public class MenuNode implements Iterable< MenuGroup > {
	/**
	 * order
	 */
	public enum Order {
		HIGHEST,
		HIGHER,
		HIGH,
		MIDDLE,
		UNDEFINED,
		LOW,
		LOWER,
		LOWEST
	}

	private static final String DEFAULT_GROUP = "none";

	public static MenuNode FILE_MENU = new MenuNode( "File" );
	public static MenuNode EDIT_MENU = new MenuNode( "Edit" );
	public static MenuNode VIEW_MENU = new MenuNode( "View" );
	public static MenuNode PROCESSING_MENU = new MenuNode( "Processing" );
	public static MenuNode TOOLS_MENU = new MenuNode( "Tools" );
	public static MenuNode HELP_MENU = new MenuNode( "Help" );

	private String name;
	private Order order;
	private String group;
	private ArrayList< MenuGroup > groups;

	/**
	 * constructor
	 * @param name name
	 */
	public MenuNode( String name ) {
		this( name,  MenuNode.DEFAULT_GROUP, Order.UNDEFINED );
	}

	/**
	 * constructor
	 * @param name name
	 * @param group group name
	 */
	public MenuNode( String name, String group ) {
		this( name, group, Order.UNDEFINED );
	}

	/**
	 * constructor
	 * @param name name
	 * @param order order
	 */
	public MenuNode( String name, Order order ) {
		this( name, MenuNode.DEFAULT_GROUP, order );
	}

	/**
	 * constructor
	 * @param name name
	 * @param group group name
	 * @param order order
	 */
	public MenuNode( String name, String group, Order order ) {
		this.name = name;
		this.group = group;
		this.order = order;
		this.groups = new ArrayList< MenuGroup >();
	}

	@Override
	public Iterator< MenuGroup > iterator() {
		return this.groups.iterator();
	}

	/**
	 * gets the name
	 * @return menu name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * gets the menu group
	 * @return menu group name
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * sets the order
	 * @param order order
	 */
	public void setOrder( Order order ) {
		this.order = order;
	}

	/**
	 * gets the order
	 * @return order
	 */
	public Order getOrder() {
		return this.order;
	}

	/**
	 * searches group
	 * @param name group name
	 * @return If the specified group is found, returns group, otherwise returns null.
	 */
	protected MenuGroup searchGroup( String name ) {
		MenuGroup group = null;
		for( MenuGroup tmp : this.groups ) {
			if( tmp.getName().equals( name ) ) {
				group = tmp;
			}
		}
		return group;
	}

	/**
	 * gets / adds item
	 * @param name menu name
	 * @return menu item
	 */
	public MenuNode item( String name ) {
		return this.item( name, MenuNode.DEFAULT_GROUP, Order.UNDEFINED );

	}

	/**
	 * gets / adds item
	 * @param name menu name
	 * @param group gorup name
	 * @return menu item
	 */
	public MenuNode item( String name, String group ) {
		return this.item( name, group, Order.UNDEFINED );
	}

	/**
	 * gets / adds item
	 * @param name menu name
	 * @param order order
	 * @return menu item
	 */
	public MenuNode item( String name, Order order ) {
		return this.item( name, MenuNode.DEFAULT_GROUP, order );
	}

	/**
	 * gets / adds item
	 * @param name menu name
	 * @param groupName group name
	 * @param order order
	 * @return menu item
	 */
	public MenuNode item( String name, String groupName, Order order ) {
		groupName = StringTool.nvl( group,  "" );
		MenuGroup group = this.searchGroup( groupName );
		if( group == null ) {
			group = new MenuGroup( groupName );
			this.groups.add( group );
		}

		MenuNode item = group.searchItem( name );
		if( item == null ) {
			item = new MenuNode( name, groupName, order );
			group.addItem( item );
		}
		else {
			if( item.getOrder() == Order.UNDEFINED ) {
				item.setOrder( order );
			}
		}

		return item;
	}

	/**
	 * judges if this item is leaf.
	 * @return If true, this item is leaf.
	 */
	public boolean isLeaf() {
		return this.groups.isEmpty();
	}

	/**
	 * counts items
	 * @return item count
	 */
	public int countItems() {
		int count = 0;
		for( MenuGroup group : this.groups ) {
			count += group.size();
		}
		return count;
	}

	/**
	 * sorts
	 */
	public void sort() {
		for( MenuGroup group : this.groups ) {
			group.sort();
		}

		this.groups.sort(
			( group1, group2 ) -> {
				double order1 = group1.calculateOrderValue();
				double order2 = group2.calculateOrderValue();
				if( order1 < order2 ) {
					return -1;
				}
				else if( order1 > order2 ) {
					return 1;
				}

				return group1.getName().compareTo( group2.getName() );
			}
		);
	}
}