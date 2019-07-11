package ninja.mspp.model.gui;

import ninja.mspp.annotation.method.MenuAction;
import ninja.mspp.annotation.method.MenuCheck;
import ninja.mspp.annotation.method.MenuPosition;
import ninja.mspp.model.PluginMethod;

public class MenuMethods {
	private PluginMethod< MenuPosition > positionMethod;
	private PluginMethod< MenuAction > actionMethod;
	private PluginMethod< MenuCheck > checkMethod;

	/**
	 * constructor
	 * @param positionMethod position method
	 * @param actionMethod action method
	 * @param checkMethod check method
	 */
	public MenuMethods(
			PluginMethod< MenuPosition > positionMethod,
			PluginMethod< MenuAction > actionMethod,
			PluginMethod< MenuCheck > checkMethod
	) {
		this.setPositionMethod( positionMethod );
		this.setActionMethod( actionMethod );
		this.setCheckMethod( checkMethod );
	}

	/**
	 * constructor
	 * @param positionMethod poisiton method
	 * @param actionMethod action method
	 */
	public MenuMethods(
			PluginMethod< MenuPosition > positionMethod,
			PluginMethod< MenuAction > actionMethod
	) {
		this( positionMethod, actionMethod, null );
	}

	public PluginMethod<MenuPosition> getPositionMethod() {
		return positionMethod;
	}

	public void setPositionMethod(PluginMethod<MenuPosition> positionMethod) {
		this.positionMethod = positionMethod;
	}

	public PluginMethod<MenuAction> getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(PluginMethod<MenuAction> actionMethod) {
		this.actionMethod = actionMethod;
	}

	public PluginMethod<MenuCheck> getCheckMethod() {
		return checkMethod;
	}

	public void setCheckMethod(PluginMethod<MenuCheck> checkMethod) {
		this.checkMethod = checkMethod;
	}
}
