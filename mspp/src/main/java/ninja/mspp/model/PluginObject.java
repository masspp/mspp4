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
	 */
	public PluginObject( Object plugin ) {
		this( plugin, null );
	}

	/**
	 * constructor
	 * @param plugin plug-in
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
	 * @return annotation
	 */
	public A getAnnotation() {
		return this.annotation;
	}
}
