package ninja.mspp.view.main;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.MenuAction;
import ninja.mspp.annotation.MenuCheck;
import ninja.mspp.annotation.MenuDisable;
import ninja.mspp.annotation.MenuPosition;
import ninja.mspp.model.PluginObject;
import ninja.mspp.model.gui.MenuGroup;
import ninja.mspp.model.gui.MenuInfo;
import ninja.mspp.tools.PluginTool;

/**
 * main frame
 * @author Satoshi Tanaka
 *
 */
public class MainFrame implements Initializable {
	@FXML private MenuBar menubar;
	@FXML private BorderPane mainPane;
	@FXML private TabPane topTabPane;
	@FXML private TabPane leftTabPane;
	@FXML private TabPane rightTabPane;
	@FXML private TabPane bottomTabPane;
	@FXML private SplitPane horizontalSplit;
	@FXML private SplitPane verticalSplit;
	@FXML private Menu fileMenu;
	@FXML private Menu editMenu;
	@FXML private Menu viewMenu;
	@FXML private Menu processingMenu;
	@FXML private Menu toolsMenu;
	@FXML private Menu helpMenu;

	/**
	 * gets the menubar
	 * @return
	 */
	public MenuBar getManuBar() {
		return this.menubar;
	}

	/**
	 * initializes main frame
	 */
	public void initialize() throws Exception {
		this.createMenu();
	}

	/**
	 * sets the main pane
	 * @param node main pane
	 */
	public void setMain( Node node ) {
		this.mainPane.setCenter( node );

		if( node instanceof Canvas ) {
			Canvas canvas = (Canvas)node;
			canvas.widthProperty().bind( this.mainPane.widthProperty() );
			canvas.heightProperty().bind( this.mainPane.heightProperty() );
		}
	}

	/**
	 * adds left tab
	 * @param title title
	 * @param node node
	 */
	public void addLeftTab( String title, Node node ) {
		if( this.leftTabPane == null ) {
			BorderPane pane = new BorderPane();
			TabPane tabPane = new TabPane();
			pane.setCenter( tabPane );
			this.horizontalSplit.getItems().add( 0, pane );
			this.horizontalSplit.setDividerPosition( 0,  0.2 );
			this.leftTabPane = tabPane;
		}

		addTab( this.leftTabPane, title, node );
	}

	/**
	 * adds right tab
	 * @param title title
	 * @param node node
	 */
	public void addRightTab( String title, Node node ) {
		addTab( this.rightTabPane, title, node );
	}

	/**
	 * adds top tab
	 * @param title title
	 * @param node node
	 */
	public void addTopTab( String title, Node node ) {
		addTab( this.topTabPane, title, node );
	}

	/**
	 * adds bottom tab
	 * @param title title
	 * @param node node
	 */
	public void addBottomTab( String title, Node node ) {
		addTab( this.bottomTabPane, title, node );
	}

	/**
	 * creates menu
	 * @throws Exception
	 */
	protected void createMenu() throws Exception {
		MsppManager mgr = MsppManager.getInstance();
		ArrayList< PluginObject< ninja.mspp.annotation.Menu > > menuPlugins = mgr.getPlugins( ninja.mspp.annotation.Menu.class );
		Map< MenuInfo, Object > pluginMap = new HashMap< MenuInfo, Object >();
		Set< MenuInfo > checkables = new HashSet< MenuInfo >();

		for( PluginObject< ninja.mspp.annotation.Menu > plugin : menuPlugins ) {
			Method[] methods = plugin.getPlugin().getClass().getMethods();
			for( Method method : methods ) {
				MenuPosition annotation = method.getAnnotation( MenuPosition.class );
				if( annotation != null ) {
					MenuInfo item = (MenuInfo)method.invoke( plugin.getPlugin() );
					pluginMap.put( item,  plugin.getPlugin() );
					if( annotation.checkable() ) {
						checkables.add( item );
					}
				}
			}
		}

		Object[][] menus = {
			{ this.fileMenu, MenuInfo.FILE_MENU },
			{ this.editMenu, MenuInfo.EDIT_MENU },
			{ this.viewMenu, MenuInfo.VIEW_MENU },
			{ this.processingMenu, MenuInfo.PROCESSING_MENU },
			{ this.toolsMenu,  MenuInfo.TOOLS_MENU },
			{ this.helpMenu, MenuInfo.HELP_MENU }
		};

		for( Object[] array : menus ) {
			Menu menu = (Menu)array[ 0 ];
			MenuInfo info = (MenuInfo)array[ 1 ];
			info.sort();
			createMenu( menu, info, pluginMap, checkables );
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)  {
		this.verticalSplit.setDividerPositions( 0.0, 1.0 );
		this.horizontalSplit.setDividerPositions( 0.0, 1.0 );

		try {
			this.createMenu();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * creates menu
	 */
	protected static void createMenu(
				Menu menu,
				MenuInfo info,
				Map< MenuInfo, Object > pluginMap,
				Set< MenuInfo > checkables
	) {
		menu.setOnShowing(
			( event ) -> {
				Menu eventMenu = (Menu)event.getSource();
				updateMenu( eventMenu );
			}
		);
		for( MenuGroup group : info ) {
			if( menu.getItems().size() > 0 ) {
				SeparatorMenuItem separator = new SeparatorMenuItem();
				menu.getItems().add( separator );
			}
			for( MenuInfo child : group ) {
				System.out.println( group.getName() );
				if( child.isLeaf() ) {
					Object plugin = pluginMap.get( child );
					boolean checkable = checkables.contains( child );
					MenuItem item = null;
					if( checkable ) {
						CheckMenuItem checkItem = new CheckMenuItem( child.getName() );
						item = checkItem;
					}
					else {
						item = new MenuItem( child.getName() );
					}

					item.setUserData( plugin );
					item.setOnAction(
						( event ) -> {
							try {
								PluginTool.invoke( plugin,  MenuAction.class );
							}
							catch( Exception e ) {
								e.printStackTrace();
							}
						}
					);

					menu.getItems().add( item );
				}
				else {
					Menu childMenu = new Menu();
					createMenu( childMenu, child, pluginMap, checkables );
				}
			}
		}

		if( menu.getItems().size() == 0 ) {
			menu.setVisible( false );
		}
	}

	/**
	 * updates menu
	 * @param menu
	 */
	protected static void updateMenu( Menu menu ) {
		for( MenuItem item : menu.getItems() ) {
			Object plugin = item.getUserData();
			boolean disable = PluginTool.getBoolean( plugin, MenuDisable.class, false );
			item.setDisable( disable );

			if( item instanceof CheckMenuItem ) {
				CheckMenuItem checkItem = (CheckMenuItem)item;
				boolean check = PluginTool.getBoolean( plugin,  MenuCheck.class, false );
				checkItem.setSelected( check );
			}
		}
	}

	/**
	 * adds tab
	 * @param pane tab pane
	 * @param title tab title
	 * @param node tab node
	 */
	protected static void addTab( TabPane pane, String title, Node node ) {
		Tab tab = new Tab();
		tab.setText( title );;
		tab.setContent( node );
		pane.getTabs().add( tab );
	}
}
