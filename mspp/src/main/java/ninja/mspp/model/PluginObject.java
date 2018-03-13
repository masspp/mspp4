package ninja.mspp.model;

import java.lang.annotation.Annotation;

/**
 * gets the plug-in object
 */
public class PluginObject < A extends Annotation >  {
	private Object plugin;
	private A annotation;

	/**
	 * constructor
	 * @param plugin plug-in
	 * @param method method
	 */
	public PluginObject( Object plugin ) {
		this( plugin, null );
	}

	/**
	 * constructor
	 * @param plugin plug-in
	 * @param method method
	 * @param annotation annotation
	 */
	public PluginObject( Object plugin, A annotation ) {
		this.plugin = plugin;
		this.annotation = annotation;
	}

	/**
	 * gets the plug-in
	 * @return plug-in
	 */
	public Object getPlugin() {
		return this.plugin;
	}


	/**
	 * gets the annotation
	 * @return
	 */
	public A getAnnotation() {
		return this.annotation;
	}
}
